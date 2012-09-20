package com.anstrat.popup;

import com.anstrat.core.Assets;
import com.anstrat.core.GameInstance;
import com.anstrat.core.Main;
import com.anstrat.gameCore.Unit;
import com.anstrat.gameCore.abilities.Ability;
import com.anstrat.gameCore.effects.Effect;
import com.anstrat.guiComponent.ColorTable;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

/**
 * @author Kalle
 */
public class UnitInfoPopup extends Popup {

	private Table effectsTable, abilityTable;
	
	private UnitTypeCard card;
	private ColorTable bottomTable;

	public UnitInfoPopup() {
		this.setBackground(new NinePatchDrawable(Assets.SKIN.getPatch("black")));
		
		card = new UnitTypeCard(false);
		
		effectsTable = new Table(Assets.SKIN);
		abilityTable = new Table(Assets.SKIN);
		
		bottomTable = new ColorTable(Color.WHITE);
		bottomTable.setBackground(new NinePatchDrawable(Assets.SKIN.getPatch("border-thick-updown")));
		bottomTable.add(abilityTable).fill();
//		bottomTable.row();
//		bottomTable.add(effectsTable);
		bottomTable.debug();
		
		this.defaults().top().left();
		this.padBottom(2*Main.percentHeight).padTop(2*Main.percentHeight);
		this.add(card).height(65 * Main.percentHeight).width(Gdx.graphics.getWidth());
		this.row();
		this.add(bottomTable).width(Gdx.graphics.getWidth()).height(30 * Main.percentHeight);
		
		this.addListener(Popup.POPUP_CLOSE_BUTTON_HANDLER);
	}
	
	/**
	 * Shows a popup with unit info
	 * @param unit The unit to show info for.
	 */
	public void show(Unit unit){
		card.setUnit(unit);
		bottomTable.setColor(card.getBackgroundColor());
		
		// Effects
//		effectsTable.clear();
//		effectsTable.defaults().left();
//		effectsTable.add("Effects:");
//		effectsTable.row();
//		
//		for(int i = 0; i < unit.effects.size(); i++){
//			Effect effect = unit.effects.get(i);
//			
//			Image icon = new Image(Assets.getTextureRegion(effect.iconName));
//			Label name = new Label(effect.name, Assets.SKIN);
//			Label description = new Label(" -" + effect.description, Assets.SKIN);
//			
//			Table inner = new Table();
//			inner.add(icon).height(3f*Main.percentHeight).width(5f*Main.percentWidth).padRight(Main.percentWidth);
//			inner.add(name).expandX().fillX();
//
//			effectsTable.add(inner);
//			effectsTable.row();
//			effectsTable.add(description);
//			effectsTable.row();
//		}
		
		// Abilities
		abilityTable.clear();
		abilityTable.defaults().top().left().expandX().fillX();
		abilityTable.add("Abilities:").height(5*Main.percentHeight);
		abilityTable.row();
		
		for(Ability a : unit.abilities){
			Table ability = new Table();
			
			Image icon = new Image(Assets.getTextureRegion(a.getIconName(unit)));
			Label name = new Label(a.name, Assets.SKIN);
			Label description = new Label(" -" + a.description, Assets.SKIN);
			description.setWrap(true);
			
			Table inner = new Table();
			inner.defaults().top().left();
			inner.add(icon).size(3f*Main.percentHeight).padRight(Main.percentWidth);
			inner.add(name);

			ability.add(inner);
			ability.row();
			ability.add(description).fill().expand();
			abilityTable.add(ability).height(Main.percentHeight*10);
			abilityTable.row();
		}
		
		abilityTable.add().fill().expand();
		
		abilityTable.debug();
		
		this.show();
	}
}
