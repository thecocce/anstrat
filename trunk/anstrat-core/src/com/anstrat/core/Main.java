package com.anstrat.core;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Stack;

import com.anstrat.ai.AIRunner;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.UnitType;
import com.anstrat.gameCore.playerAbilities.PlayerAbilityType;
import com.anstrat.gui.GEngine;
import com.anstrat.guiComponent.TransitionEffect;
import com.anstrat.mapEditor.MapEditor;
import com.anstrat.menu.MainMenu;
import com.anstrat.menu.NetworkDependentTracker;
import com.anstrat.menu.SplashScreen;
import com.anstrat.network.Network;
import com.anstrat.popup.Popup;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class Main extends Game implements ApplicationListener {
	
	public static final String version = "Beta 1";
	public static final String NETWORK_HOST = "localhost";
	public static final int NETWORK_PORT = 25406;
	
	public static float percentWidth;
	public static float percentHeight;
	
	// Network
	public NetworkController network;	// The mapping between UI actions and network commands 
	private Network networkEngine;		// The network engine that constructs and parses network messages
	
	public Music menuMusic;
	
	// Input handlers
	private final InputMultiplexer inputMultiplexer;
	private LinkedList<InputProcessor> inputProcessorsToBeRemoved;
	
	public final GestureMultiplexer gestureMultiplexer;
	private final CustomGestureDetector gestureDetector;
	
	public SpriteBatch batch;
	private Stage overlayStage;	//for drawing transition effects and popups.
	private static Main me;
	
	public final AssetManager manager;
	
	public static synchronized Main getInstance(){
		if(me == null){
			me = new Main();
		}
		return me;
	}

	private Main(){
		manager = new AssetManager();
		inputMultiplexer = new InputMultiplexer();
		inputProcessorsToBeRemoved = new LinkedList<InputProcessor>();
		
		gestureMultiplexer = new GestureMultiplexer();
		
		// Custom gesture detector (handles long press correctly)
		gestureDetector = new CustomGestureDetector(gestureMultiplexer);
		inputMultiplexer.addProcessor(gestureDetector);
	}
	
	@Override
	public void create() {
		
		// Print max texture size
		int[] maxTextureSize = new int[1];
		Gdx.gl10.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);
		Gdx.app.log("glinfo", "Max texture size = " + maxTextureSize[0]);
		
		Gdx.app.log("Game.create()", String.format("Display surface: %dx%d.", Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		System.out.println("PPIX = " + Gdx.graphics.getPpiX());
		System.out.println("PPIY = " + Gdx.graphics.getPpiY());
		
		percentWidth = ((float)Gdx.graphics.getWidth())/100f;
		percentHeight = ((float)Gdx.graphics.getHeight())/100f;
		
		// Music
		menuMusic = Gdx.audio.newMusic(Gdx.files.internal("music/vikingstitle.mp3"));
		
		// Load from file
		Assets.load();													// Textures, fonts
		UnitType.loadAttributesFromFile(
				Gdx.files.internal("data/unitAttributes.xml"));			// Attributes (name, stats etc) for all units
		Options.loadPreferences();										// Settings, sound on/off etc
		PlayerAbilityType.loadAttributesFromFile(
				Gdx.files.internal("data/playerAbilityAttributes.xml"));

		GameInstance.loadGameInstances(Gdx.files.local("games.bin"));	// Loads all saved game instances

		networkEngine = new Network(NETWORK_HOST, NETWORK_PORT);
		network = new NetworkController(networkEngine);
		
		// Create the single instance of sprite batch
		batch = new SpriteBatch();
		overlayStage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, batch);
		
		Popup.initPopups(overlayStage);
		
		// Setup input and gesture processing
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setInputProcessor(inputMultiplexer);
		inputMultiplexer.addProcessor(new CustomInputProcessor());
		
		// Init menu, show splash
		MainMenu.getInstance();
		Gdx.graphics.setTitle("Vengeful Vikings (Beta)");
		setScreen(SplashScreen.getInstance());
		
		networkEngine.start();
		
		// Set the desktop application icon
		FileHandle iconFile = Gdx.files.internal("icon.png");
		
		if(iconFile.exists()){
			Gdx.graphics.setIcon(new Pixmap[]{new Pixmap(iconFile)});
		}
		else{
			Gdx.app.log("Main", String.format("Warning: Could not find app icon '%s'.", iconFile));
		}
	}

	@Override
	public void resize(int width, int height){
		super.resize(width, height);
		overlayStage.setViewport(width, height, false);
	}
	
	@Override
	public void render() {
		// For processor concurrency.
		while(!inputProcessorsToBeRemoved.isEmpty())
			inputMultiplexer.removeProcessor(inputProcessorsToBeRemoved.poll());

		gestureDetector.update(Gdx.graphics.getDeltaTime());
		
		//Don't run AI if using map editor
		if(!(super.getScreen() instanceof MapEditor))
			AIRunner.run(Gdx.graphics.getDeltaTime());
		
		GL10 gl = Gdx.graphics.getGL10();
		
		// Render
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		gl.glClearColor(0.25f, 0.25f, 0.25f, 1f);
		
		// Renders the current screen
		super.render();
		
		//Draw overlays and popups
		overlayStage.act(Gdx.graphics.getDeltaTime());
		overlayStage.draw();
	}
	
	@Override
	public void pause() {
		Gdx.app.log("Main", "Paused");
		if(networkEngine != null) networkEngine.pause();
		Assets.onApplicationPause();
	}

	@Override
	public void resume() {
		Gdx.app.log("Main", "Resumed");
		if(networkEngine != null) networkEngine.resume();
		Assets.onApplicationResume();
	}

	@Override
	public void dispose() {
		Gdx.app.log("", "Main.dispose()");
		
		if(networkEngine != null) networkEngine.stop();
		GameInstance.saveGameInstances(Gdx.files.local("games.bin"));
		Options.savePreferences();
		
		batch.dispose();
		
		// Dispose all screens that have been initialized
		for(Screen screen : screens){
			screen.dispose();
		}
		
		Assets.dispose();
		menuMusic.dispose();
		NetworkDependentTracker.dispose();
		Popup.disposePopups();
		overlayStage.dispose();
		
		me = null;
		State.activeState = null;
	}
	
	private HashSet<Screen> screens  = new HashSet<Screen>(); 
	public Stack<Screen> screenStack = new Stack<Screen>();
	
	@Override
	public void setScreen(Screen screen){
		
		if(super.getScreen() != null && !(super.getScreen() instanceof GEngine))
			screenStack.add(super.getScreen());
		
		super.setScreen(screen);
		
		screens.add(screen);
		
		if(screen instanceof GEngine){
			overlayStage.addActor(new TransitionEffect());
			
			// do not stop menu music, menu music even ingame
			// menuMusic.setLooping(false);
		}
		else if(screen instanceof MainMenu){
			menuMusic.setLooping(true);
			
			if(!menuMusic.isPlaying() && Options.soundOn){
				menuMusic.play();
			}
		}
	}
	
	public void popScreen(){
		if(screenStack.size()>0){
			super.setScreen(screenStack.pop());
		}
		else
			Gdx.app.error("Main", "Tried to pop with an empty screen stack.");
	}
	
	public void addProcessor(InputProcessor ip){
		inputMultiplexer.addProcessor(ip);
	}
	
	public void removeProcessor(InputProcessor ip){
		inputProcessorsToBeRemoved.add(ip);
	}
}