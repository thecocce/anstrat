package com.anstrat.menu;

import com.anstrat.audio.AudioAssets;
import com.anstrat.core.Assets;
import com.anstrat.core.GameInstance;
import com.anstrat.core.Main;
import com.anstrat.core.Options;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.mapEditor.MapEditor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * The class handling the first menu that shows up when the user starts the application.
 * @author Ekis
 *
 */
public class MainMenu extends MenuScreen {
	
	private static MainMenu me;
	
	public static TextField usernameInput = ComponentFactory.createTextField("Login", false);
	public static TextField passwordInput = ComponentFactory.createTextField("Password", true);
	
	public static String HOTSEAT = "Hotseat", INTERNET = "Internet";
	
	public static boolean versusAI = false;
	
	public static int pendingGames = 0;
	private Table gamesList;
	
	private MainMenu() {
		super();
		
		//Change to classic sound on/off icon later.
		CheckBoxStyle cbst = new CheckBoxStyle(
				new TextureRegionDrawable(Assets.getTextureRegion("sound-off")),
				new TextureRegionDrawable(Assets.getTextureRegion("sound-on")),
				Assets.MENU_FONT,
				Color.WHITE);
		CheckBox muteButton = new CheckBox("", cbst);
		muteButton.setChecked(Options.soundOn);
		muteButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(Options.soundOn = !Options.soundOn)
					AudioAssets.playMusic("VikingsTheme");
				else
					AudioAssets.stopMusic();
			}
			
		});

		Button newGameButton = ComponentFactory.createMenuButton("New Game",new ClickListener() {
        	@Override
        	public void clicked(InputEvent event, float x, float y) {
        		Main.getInstance().setScreen(ChooseGameTypeMenu.getInstance());
        		AudioAssets.playSound("dummy1");
        	}
        });

        Button mapEditorButton = ComponentFactory.createMenuButton("Map Editor",new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	Main.getInstance().setScreen(MapEditor.getInstance());
            	AudioAssets.playSound("dummy1");
            }
        });
        
        Button invitedButton = ComponentFactory.createMenuButton("Invite",new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            }
        });
        
        NinePatchDrawable emp = new NinePatchDrawable(Assets.SKIN.getPatch("empty"));
        TextButtonStyle tbst = new TextButtonStyle(emp,emp,emp);
        tbst.fontColor = tbst.checkedFontColor = Color.WHITE;
        tbst.downFontColor = Color.LIGHT_GRAY;
        tbst.font = Assets.UI_FONT;
        TextButton ver = new TextButton(" "+Main.version, tbst);
        		
        ver.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	Main.getInstance().setScreen(DebugMenu.getInstance());
            	AudioAssets.playSound("dummy1");
            }
        });

        gamesList = new Table(Assets.SKIN);
        updateGamesList();
        
        ScrollPaneStyle spst = new ScrollPaneStyle(emp,emp,emp,emp,emp); 	//First one is  background

        ScrollPane scroll = new ScrollPane(gamesList, spst);
		scroll.setScrollingDisabled(true, false);
		scroll.setFlickScroll(true);
		
		float logoWidth = Main.percentWidth*55f;
	    float buttonWidth  = BUTTON_WIDTH;
        float buttonHeight = BUTTON_HEIGHT;

        // Background with hole		-- temp removed
        //Image transBack = new Image(Assets.getTextureRegion("MenuBackground-transparent"));
        //contents.addActor(transBack);
        //transBack.setBounds(0, 0, Gdx.graphics.getHeight(), Gdx.graphics.getWidth());
        
        contents.defaults().space(Main.percentHeight).top().center();
        
        Table header = new Table();
        header.add(ver).uniform().top().left();
        header.add(new Image(Assets.getTextureRegion("logo"))).height(logoWidth/2.8f).width(logoWidth).center().padTop(Main.percentHeight*7f);
        header.add(muteButton).top().right().uniform().width((Main.percentWidth*100f-logoWidth)/2f).padTop(Main.percentHeight*3f);
        
        contents.add(header);
        contents.row();
        contents.add(newGameButton).height(buttonHeight).width(buttonWidth);
        contents.row();
        contents.add(mapEditorButton).height(buttonHeight).width(buttonWidth);
        contents.row();
        contents.add(scroll).fill().minHeight(1f).expand().padBottom(Main.percentHeight*10f).padTop(Main.percentHeight*5f);
        
        Table footer = new Table();
      
        // The mysterious invite button, if removed or hidden the background dissapears :D
        // Solution: Set height/width to 0...
        footer.add(invitedButton).height(0).width(0).align(Align.right);
        
        contents.row();
        contents.add(footer).fillX();
        
        contents.layout();
        contents.addActor(flag);
	}
	
	public static synchronized MainMenu getInstance() {
		if(me == null){
			me = new MainMenu();
		}
		return me;
	}
	
	
	@Override
	public void hide() {
		super.hide();
	}
	
	@Override
	public void show() {
		super.show();
		updateGamesList();
	}

	@Override
	public void dispose() {
		super.dispose();
		me = null;
	}
	
	public void updateGamesList(){
		// Only update if MainMenu is the active screen
		if(Main.getInstance().getScreen() != this) return;
		
		gamesList.clear();
		gamesList.align(Align.top);
		
		float height     = Main.percentHeight*4f;
		float paddingTop = 0f;//Main.percentHeight*4f;
		
		// Game instances
		Table current = new Table(Assets.SKIN);
		current.add("Your turn:").height(height).padTop(paddingTop);
		current.row();
		
		Table waiting = new Table(Assets.SKIN);
		waiting.add("Waiting for other players:").height(height).padTop(paddingTop);
		
        for(final GameInstance gi : Main.getInstance().games.getActiveGames()){    	
        	Table table = gi.isUserCurrentPlayer() ? current : waiting;
        	table.row();
        	table.add(new GameInstanceView(gi)).fillX().expandX().height(9f*Main.percentHeight);
        }
		
        // Game requests
		Table requests = new Table(Assets.SKIN);
		if (pendingGames > 0) {
			requests.add("Looking for opponent...");
		}
		/*
        for(GameRequest request : Main.getInstance().network.getGameRequests()){
        	requests.row();
        	requests.add(gameRequestToTable(request)).fillX().expandX().height((int)(17*Main.percentHeight));
        }
        */
        if(current.getChildren().size > 1){
     		gamesList.add(current).fillX().expandX();
         	gamesList.row();
        }
        
        if(waiting.getChildren().size > 1){
         	gamesList.add(waiting).fillX().expandX();
         	gamesList.row();
        }
        
        if(pendingGames > 0){
        	gamesList.add(requests).fillX().expandX();
        }
        	
        //gamesList.padBottom(Main.percentHeight*5f);
	}
	
	// Formats the given time to a HH:MM:SS format, timeSpan is given in milliseconds
	/*
	private static String formatTime(long milliseconds){
		long hours = milliseconds / 3600000l;
		milliseconds %= 3600000l;
		long minutes = milliseconds / 60000l;
		milliseconds %= 60000l;
		long seconds = milliseconds / 1000;
		
		StringBuilder builder = new StringBuilder(10);
		
		if(hours > 0){
			if(hours < 10) builder.append('0');
			builder.append(hours);
			builder.append(':');
		}

		if(minutes < 10) builder.append('0');
		builder.append(minutes);
		builder.append(':');
		
		if(seconds < 10) builder.append('0');
		builder.append(seconds);
		
		return builder.toString();
	}
	*/
	
	/*
	private Table gameRequestToTable(GameRequest request){
		Table table = new Table(Assets.SKIN);
    	table.setBackground(Assets.SKIN.getPatch("line-border-thin"));
    	
    	Map map = request.map;
    	Label gameName = new Label(request.gameName, Assets.SKIN);
    	Label limit = new Label("Limit: "+request.timeLimit, Assets.SKIN);
    	Label mapSize = new Label(map==null?"null":(map.getXSize()+"x"+map.getYSize()), Assets.SKIN);
    	Label mapName = new Label(map==null?"null":map.name, Assets.SKIN);
    	
    	String status = "";
    	switch(request.status){
	    	case GameRequest.STATUS_SEARCH_GAME: {
	    		status = "Searching for game";
	    		break;
	    	}
	    	case GameRequest.STATUS_UNKNOWN: {
	    		status = "Status unknown";
	    		break;
	    	}
	    	case GameRequest.STATUS_WAIT_OPPONENT: {
	    		status = "Waiting for opponent";
	    		break;
	    	}
    	}
    	
    	Button cancel = new Button(new Image(Assets.getTextureRegion("cancel")), Assets.SKIN.getStyle("image-toggle", ButtonStyle.class));
    	final long nonce = request.nonce;
    	cancel.setClickListener(new ClickListener() {
	        @Override
	        public void click(Actor actor,float x,float y ){
	        	//Main.getInstance().network.cancelRequest(nonce);
	        }
		});
    	
    	table.left().pad((int)(2*Main.percentWidth));
    	table.defaults().left().fillX().expandX();
    	
    	Table outer = new Table(Assets.SKIN);
    	outer.defaults().left().height((int)(4*Main.percentHeight));
    	
    	Table inner1 = new Table();
    	inner1.add(gameName).fillX().expandX();
    	inner1.add(limit);
    	
    	Table inner2 = new Table();
    	inner2.add(mapName).fillX().expandX();
    	inner2.add(mapSize);
    	
    	outer.add(inner1).expandX().fillX();
    	outer.row();
    	outer.add(inner2).expandX().fillX();
    	outer.row();
    	outer.add(status);
    	
    	table.add(outer).expandX().fillX().padLeft((int)(Main.percentHeight));
    	table.add(cancel).pad((int)(3+Main.percentWidth)).height((int)(7*Main.percentHeight)).width((int)(7*Main.percentHeight)).bottom().right();
    	
    	return table;
	}
	*/
}


