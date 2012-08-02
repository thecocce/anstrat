package com.anstrat.gui;

import com.anstrat.core.Assets;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class FancyNumbers {

	public static final float sideIncrement = 0.7f;
	
	public static void drawDamageRange(int min, int max, float x, float y, float size, boolean flipped, SpriteBatch batch){
		batch.setColor(Color.RED);
		drawNumber(min, x, y, size, flipped, batch);
		if(min >= 10)
			x += 2*size*sideIncrement;
		else
			x += size*sideIncrement;
		TextureRegion dash = Assets.getTextureRegion("hurt-dash");
		batch.draw(dash, x, y, size, 	// if flipped, invert height
				flipped ? -size : size);
		x += size*sideIncrement;
		drawNumber(max, x, y, size, flipped, batch);
		batch.setColor(Color.WHITE);
	}
	public static void drawValueDecrement(int before, int after, float x, float y, float size, boolean flipped, SpriteBatch batch){
		drawNumber(before, x, y, size, flipped, batch);
		if(before >= 100)
			x += size*sideIncrement;
		if(before >= 10)
			x += size*sideIncrement;
		x += size*sideIncrement;
		TextureRegion arrow = Assets.getTextureRegion("rightArrow");
		batch.draw(arrow, x+size*0.1f, y, size*0.8f, 	// if flipped, invert height
				flipped ? -size : size);
		x += size*sideIncrement;
		drawNumber(after, x, y, size, flipped, batch);
	}
	
	/**
	 * Tint before using
	 * @param number
	 * @param x
	 * @param y
	 * @param size
	 * @param flipped
	 * @param batch
	 */
	public static void drawNumber(int number, float x, float y, float size, boolean flipped, SpriteBatch batch){
		int hundreds = number/100;
		int tens = number/10%10;
		int ones = number%10;
		
		TextureRegion onesTexture, tensTexture, hundredsTextrue;
		
		if(hundreds > 0)
		{
			hundredsTextrue = Assets.getTextureRegion("ap-"+hundreds);
			batch.draw(hundredsTextrue, x, y, size, 	// if flipped, invert height
					flipped ? -size : size);
			x += size*sideIncrement;
		}
		if(tens > 0 || hundreds > 0)
		{
			tensTexture = Assets.getTextureRegion("ap-"+tens);
			batch.draw(tensTexture, x, y, size, 	// if flipped, invert height
					flipped ? -size : size);
			x += size*sideIncrement;
		}
		onesTexture = Assets.getTextureRegion("ap-"+ones);
		batch.draw(onesTexture, x, y, size, 	// if flipped, invert height
				flipped ? -size : size);
		
		
	}
}