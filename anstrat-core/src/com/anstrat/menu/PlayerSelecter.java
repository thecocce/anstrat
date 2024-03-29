package com.anstrat.menu;

import com.anstrat.core.Main;
import com.anstrat.guiComponent.ComponentFactory;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class PlayerSelecter extends Table {
	private TeamSelecter teamTable;
	
	/** TODO: Add god selecter **/
	private int god = 0;
	
	private TextField playerNameButton;
	
	/** Player selecter without name */
	public PlayerSelecter(){
		this(false, null);
	}
	
	/** Player selection with name **/
	public PlayerSelecter(String defaultName){
		this(true, defaultName);
	}
	
	private PlayerSelecter(boolean nameSelection, String defaultName){
		playerNameButton = ComponentFactory.createTextField(defaultName, false);
		teamTable = new TeamSelecter(); 

		if(nameSelection){
			add(playerNameButton).fillX().expandX().height(Main.percentHeight * 8f);
			row();
		}

		add(teamTable).fillX().expandX();
	}
	
	public String getPlayerName(){
		String name = playerNameButton.getText();
		return name.equals("") ? playerNameButton.getMessageText() : name;
	}
	
	public int getGod(){
		return god;
	}
	
	public int getTeam(){
		return teamTable.getTeam();
	}
}
