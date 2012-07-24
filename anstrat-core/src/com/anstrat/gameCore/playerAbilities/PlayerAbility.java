package com.anstrat.gameCore.playerAbilities;

import java.io.Serializable;

import com.anstrat.gameCore.Player;

public abstract class PlayerAbility implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public final String name;
	public final Player player;
	public final PlayerAbilityType type;
	
	public PlayerAbility(String name, Player player, PlayerAbilityType type) {
		this.name = name;
		this.player = player;
		this.type = type;
	}
	
	public void activate(){
		player.mana -= type.manaCost;
	}
	
	public boolean isAllowed(Player player){
		return player.mana >= type.manaCost;	// Player must have the required mana
	}
	
	public static PlayerAbility[] getAbilities() {
		return null;
	}
}