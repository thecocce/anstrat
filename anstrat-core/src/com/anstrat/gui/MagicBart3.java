package com.anstrat.gui;

import com.anstrat.core.Assets;
import com.anstrat.core.Main;
import com.anstrat.gameCore.State;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;

public class MagicBart3 {
	private TextureRegion magic_empty, 
		magic_one, magic_two, magic_three,
		magic_one_g, magic_two_g, magic_three_g;
	private TextureRegion[] mxf = new TextureRegion[6];
	private float mw;
	
	public MagicBart3(){
		magic_empty = Assets.getTextureRegion("magic-bar-empty");
		for(int i=0;i<6;i++){
			mxf[i] = Assets.getTextureRegion("magic-bar-"+(i+1));
		}
		magic_one = Assets.getTextureRegion("magic-one");
		magic_two = Assets.getTextureRegion("magic-two");
		magic_three = Assets.getTextureRegion("magic-three");
		magic_one_g = Assets.getTextureRegion("magic-one-glowing");
		magic_two_g = Assets.getTextureRegion("magic-two-glowing");
		magic_three_g = Assets.getTextureRegion("magic-three-glowing");
		mw = magic_empty.getRegionWidth();
	}
	
	public void draw(){
		SpriteBatch sb = Main.getInstance().batch;
		float pw = Main.percentWidth;
		float x_start = pw*5f;
		float x_width = pw*90f;
		float x_part = x_width/6f;
		float y_start = pw*148f;
		float y_height = pw*5f;
		sb.begin();
		sb.draw(magic_empty, x_start, y_start, x_width, y_height);
		int mAmt = State.activeState.players[State.activeState.currentPlayerId].mana;
		
		if(mAmt > 0)
			sb.draw(mxf[mAmt-1], x_start, y_start, x_width, y_height);
		
		sb.draw(mAmt>=2?magic_one_g:magic_one, x_start + x_part * 1.5f - magic_one.getRegionWidth() / 2f + pw*0.65f, 
				y_start - y_height/2f, magic_one.getRegionWidth(), y_height*2f);
		sb.draw(mAmt>=4?magic_two_g:magic_two, x_start + x_part * 3.5f - magic_two.getRegionWidth() / 2f - pw*0.5f, 
				y_start - y_height/2f, magic_two.getRegionWidth(), y_height*2f);
		sb.draw(mAmt>=6?magic_three_g:magic_three, x_start + x_part * 5.5f - magic_three.getRegionWidth() / 2f - pw*1f, 
				y_start - y_height/2f, magic_three.getRegionWidth(), y_height*2f);
		sb.end();
	}
}