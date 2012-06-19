package com.anstrat.command;

import com.anstrat.gameCore.Player;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.playerAbilities.PlayerAbility;
import com.anstrat.gameCore.playerAbilities.PlayerAbilityFactory;
import com.anstrat.gameCore.playerAbilities.PlayerAbilityType;
import com.anstrat.gameCore.playerAbilities.TargetedPlayerAbility;
import com.anstrat.geography.TileCoordinate;

public class ActivateTargetedPlayerAbilityCommand extends Command {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private PlayerAbilityType type;
	private int playerid;
	private TileCoordinate target;
	
	public ActivateTargetedPlayerAbilityCommand(Player player, TileCoordinate target, PlayerAbilityType type){
		this.type = type;
		this.playerid = player.playerId;
		this.target = target;
	}
	
	@Override
	protected void execute() {
		
		PlayerAbility ability = PlayerAbilityFactory.createAbility(type, State.activeState.players[playerid]);
		if(ability instanceof TargetedPlayerAbility){
			((TargetedPlayerAbility)ability).activate(ability.player, target);
		}		
	}
	
	@Override
	public boolean isAllowed(){
		PlayerAbility ability = PlayerAbilityFactory.createAbility(type, State.activeState.players[playerid]);
		
		return super.isAllowed()
				&& ability != null
				&& ability instanceof TargetedPlayerAbility
				&& ((TargetedPlayerAbility)ability).isAllowed(ability.player, target);
	}
	
}
