package com.anstrat.menu;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.guiComponent.ComponentFactory;
import com.anstrat.popup.Popup;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;

public class JoinGameMenu extends MenuScreen {
	
	private static JoinGameMenu me;
	
	private TextField name, password;

	public JoinGameMenu() {
		super();
		
		name     = ComponentFactory.createTextField("Game Name", false);
		password = ComponentFactory.createTextField("Game Password", true);
		Table settings = new Table(Assets.SKIN);
        settings.setBackground(Assets.SKIN.getPatch("single-border"));
        settings.defaults().height((int)(Main.percentHeight*10));
        settings.add("Name:");
        settings.add(name).fillX().expandX();
        settings.row();
        settings.add("Password:");
        settings.add(password).fillX().expandX();
		
		Button join = ComponentFactory.createMenuButton( "Join Game",new ClickListener() {
            @Override
            public void click(Actor actor,float x,float y ){
            	if(name.getText()==""){
            		Popup.showGenericPopup("Error", "You must enter a game name.");
            		return;
            	}
            	//Main.getInstance().network.joinGame(name.getText(), password.getText());
            }
        });
		
		contents.padTop((int) (3*Main.percentHeight));
		contents.defaults().space(5).pad(0).top().width(BUTTON_WIDTH);
		contents.add(settings);
		contents.row();
		contents.add(join).fillY().expandY().height(BUTTON_HEIGHT);
		contents.row();
		
		Table inner = new Table();
		inner.add(ComponentFactory.createLoginLabel()).center();
		
		contents.add(inner);
	}
	
	public static synchronized JoinGameMenu getInstance() {
		if(me == null){
			me = new JoinGameMenu();
		}
		return me;
	}

	@Override
	public void dispose() {
		super.dispose();
		me = null;
	}
}