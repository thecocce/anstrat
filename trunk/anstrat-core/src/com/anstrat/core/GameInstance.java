package com.anstrat.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import com.anstrat.ai.AIUtils;
import com.anstrat.ai.ScriptAI;
import com.anstrat.command.Command;
import com.anstrat.gameCore.Player;
import com.anstrat.gameCore.State;
import com.anstrat.gameCore.UnitType;
import com.anstrat.geography.Map;
import com.anstrat.gui.GEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/**
 * A single game instance, containing a State.
 * 
 * Create a new one when you start a new game.
 * 
 * These are the objects stored stored on the phone for continuing earlier games.
 * @author Anton
 *
 */
public class GameInstance implements Serializable{
	
	private static final long serialVersionUID = 1L;
	static List<GameInstance> gamesList = new ArrayList<GameInstance>();
	
	public State state;
	private Collection<Integer> aiPlayerIDs;
	
	public GameInstance(long gameID, Map map, Player[] players){
		state = new State(map, players, this);
	}
	
	protected GameInstance(Map map, Player[] players, long randomSeed){
		state = new State(map, players, randomSeed, this);
	}
	
	public static GameInstance createAIGame(Map map, Integer... aiPlayerIds){
		GameInstance gi = createHotseatGame(map);
		gi.aiPlayerIDs = Arrays.asList(aiPlayerIds);
		return gi;
	}
	
	/**
	 * @param map a map or null to generate a random map
	 * @return
	 */
	public static GameInstance createHotseatGame(Map map){
		return GameInstance.createHotseatGame(map, 2);
	}
	
	public static GameInstance createHotseatGame(Map map, int numPlayers){
		
		Player[] players = new Player[numPlayers];
		int team = 0;
		
		for(int i = 0; i < players.length; i++){
			players[i] = new Player(i, "Player " + i, team, Player.getRandomGodFromTeam(team));
			
			// Toggle team
			team = (team + 1) % UnitType.TEAMS.length;
		}
		
		// If no map given, create a random one
		if(map == null) map = new Map(10, 10, new Random());
		
		GameInstance gi = new GameInstance(gamesList.size() + 1, map, players);
		gamesList.add(gi);
		
		return gi;
	}
	
	/**
	 * Switch to the ingame screen using this game. This is the only way you should move to the ingame screen through.
	 */
	public void showGame(boolean startZoom){
		
		// Assign AI if needed (needs to be done here to work with deserialization, as the AI engine is not serialized)
		if(aiPlayerIDs != null){
			for(int i : aiPlayerIDs){
				Player p = state.players[i];
				if(p.ai == null) AIUtils.assignAI(p, new ScriptAI());
			}
		}
		
		State.activeState = state;
		GEngine.getInstance().init(state, startZoom);
		Main.getInstance().setScreen(GEngine.getInstance());
		GEngine.getInstance().userInterface.updateCurrentPlayer();
		System.out.println("Game shown using GameInstance");
	}
	
	public int getTurnNumber(){
		return this.state.turnNr;
	}
	
	public void resign(){
		remove();
	}
	
	/**
	 * Removes this game.
	 */
	public void remove(){
		gamesList.remove(this);
		
		// If AI game, clear all AI's
		if(aiPlayerIDs != null){
			for(int playerID : aiPlayerIDs){
				state.players[playerID].ai = null;
			}
		}
	}
	
	public boolean isAiGame(){
		return this.aiPlayerIDs != null;
	}
	
	public Player getUserPlayer(){
		if(aiPlayerIDs == null) return state.getCurrentPlayer();
		
		// Find user player
		for(Player p : state.players){
			if(!aiPlayerIDs.contains(p.playerId)) return p;
		}
		
		// Should NEVER happen
		throw new RuntimeException("Game only contains AI's!");
	}
	
	public void onCommandExecute(Command command){
		
	}
	
	public static List<GameInstance> getActiveGames(){
		return gamesList;
	}
	
	public static void saveGameInstances(FileHandle handle){
		Serialization.writeObject(new GameInstanceList(gamesList), handle);
	}
	
	public static void loadGameInstances(FileHandle handle){
		Object obj = Serialization.readObject(handle);
		
		if(obj == null){
			Gdx.app.log("GameInstance", "No previous game instances found.");
		}
		else{
			gamesList = ((GameInstanceList)obj).games;
			
			for(GameInstance gi : gamesList){
				if(gi instanceof NetworkGameInstance){
					NetworkGameInstance ngi = (NetworkGameInstance) gi;
					NetworkGameInstance.gameByID.put(ngi.getGameID(), ngi);
				}
			}
		}
	}
	
	// Class used only to serialize/deserialize game instances
	private static class GameInstanceList implements Serializable {
		private static final long serialVersionUID = 1L;
		private List<GameInstance> games;
		
		public GameInstanceList(List<GameInstance> games){
			this.games = games;
		}

		@Override
		public String toString() {
			return String.format("%s(size = %d)", this.getClass().getSimpleName(), games.size());
		}
	}
}
