package com.anstrat.core;

import com.anstrat.audio.AudioAssets;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public final class Options {

	public static boolean DEBUG_SHOW_OWNER = false;
	public static float mapBorderOffset = Gdx.graphics.getWidth()/8;
	public static boolean soundOn = false;
	public static Preferences prefs;
	public static float speedFactor = 1f;
	public static boolean showFps = false;
	
	public static void loadPreferences(){
		System.out.println("Loading preferences.");
		
		prefs = Gdx.app.getPreferences("settings");
		if(prefs.contains("sound")){
			System.out.println("\tSound: "+prefs.getBoolean("sound"));
			soundOn = prefs.getBoolean("sound");
			if(soundOn)
				AudioAssets.playMusic("VikingsTheme");
		}
		if(prefs.contains("fps")){
			System.out.println("\tShow fps: "+prefs.getBoolean("fps"));
			showFps = prefs.getBoolean("fps");
		}
		prefs.clear();
	}
	
	public static void savePreferences(){
		System.out.println("Saving preferences.");
		
		if(prefs!=null){
			prefs.putBoolean("sound", soundOn);
			prefs.putBoolean("fps", showFps);
			prefs.flush();
		}
	}
}
