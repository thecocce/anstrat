package com.anstrat.menu;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.geography.Map;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.menu.MapSelecter.MapSelectionHandler;
import com.anstrat.network.protocol.GameOptions;
import com.anstrat.network.protocol.GameOptions.MapType;
import com.anstrat.popup.TeamPopup;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class HotseatMenu extends MenuScreen implements MapSelectionHandler {
	private static HotseatMenu me;
	public int player1team, player2team;

	private MapSelecter mapSelecter;
	
	private String mapName;
	private GameOptions.MapType mapType;
	private Button goButton;
	
	private HotseatMenu(){
		mapSelecter = new MapSelecter(this);
		
		final CheckBox fog = ComponentFactory.createCheckBox("Fog of War");
		fog.setChecked(true);
		
		final PlayerSelecter player1Selecter = new PlayerSelecter("Player 1");
		final PlayerSelecter player2Selecter = new PlayerSelecter("Player 2");
		
		goButton = ComponentFactory.createMenuButton("GO!", new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				// Load named map (if selected)
				if(mapType == GameOptions.MapType.SPECIFIC){
					Map map = Assets.loadMap(mapName);
					map.fogEnabled = fog.isChecked();
					Main.getInstance().games.createHotseatGame(map, player1Selecter.getTeam(), player2Selecter.getTeam()).showGame(true);
				}
				else if(mapType != null){
					Dimension d = getMapSize(mapType);
					player1team = player1Selecter.getTeam();
					player2team = player2Selecter.getTeam();
					Main.getInstance().games.createHotseatGame(
							fog.isChecked(),
							d.width,
							d.height,
							player1team,
							player2team).showGame(true);
				}
		   }
		});
		goButton.setDisabled(true);
		Assets.SKIN.setEnabled(goButton, !goButton.isDisabled());
		
		contents.padTop(3f*Main.percentHeight).center();
		contents.defaults().space(Main.percentWidth).pad(0).top().width(BUTTON_WIDTH);
		contents.add(mapSelecter);
		contents.row();
		contents.add(player1Selecter);
		contents.row();
		contents.add(player2Selecter);
		contents.row();
		contents.add(fog);
		contents.row();
		contents.add(goButton).height(BUTTON_HEIGHT).width(BUTTON_WIDTH).padBottom(BUTTON_HEIGHT*0.3f);
		contents.row();
		Table centerLogin = new Table(Assets.SKIN);
		centerLogin.add(ComponentFactory.createLoginLabel());
		contents.add(centerLogin).bottom();
	}
	
	private static Dimension getMapSize(GameOptions.MapType t){
		if(t == GameOptions.MapType.GENERATED_SIZE_LARGE){
			return new Dimension(16,16);
		}
		else if(t == GameOptions.MapType.GENERATED_SIZE_MEDIUM){
			return new Dimension(12,12);
		}
		else if(t == GameOptions.MapType.GENERATED_SIZE_SMALL){
			return new Dimension(8,8);
		}
		else if(t == GameOptions.MapType.GENERATED_SIZE_RANDOM){
			// TODO: Randomize! Make sure to keep a good width/height ratio
			return new Dimension(16,16);
		}
		else {
			throw new IllegalArgumentException("Was not expecting " + t);
		}
	}
	
	/**
	 * Custom dimension class. Can't use java.awt on android. 
	 */
	private static class Dimension{
		public int width, height;
		
		public Dimension(int w, int h){
			this.width = w;
			this.height = h;
		}
	}
	
	public static synchronized HotseatMenu getInstance() {
		if(me == null){
			me = new HotseatMenu();
		}
		return me;
	}

	@Override
	public void mapSelected(MapType type, String mapName) {
		this.mapType = type;
		this.mapName = mapName;
		this.goButton.setDisabled(false);
		Assets.SKIN.setEnabled(goButton, !goButton.isDisabled());
	}
}