package com.ant.sudoku;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

public class Common {
	public static Drawable background;
	
	public static float THICK_STROKE = 2.7f;
	public static float THIN_STROKE = 0.5f;
	public static float NUMBER_STROKE = 3.5f;
	
	public static float TEXT_SIZE = 10f;
	public static float NUMBER_SIZE = 20f;
	
	public static Paint thickPaint;
	public static Paint thinPaint;
	public static Paint textPaint;
	public static Paint redTextPaint;
	public static Paint brightPaint;
	public static Paint brightCellPaint;
	public static Paint fixedNumberPaint;
	public static Paint lightNumberPaint;
	
	//paints for numberboard
	public static Paint normalPaint;
	public static Paint selectedPaint;
	
	public static SoundManager mSoundManager;
	
	public static void init(Resources res, Activity activity) {
		background = res.getDrawable(R.drawable.background);
		
		thickPaint = new Paint();
		thickPaint.setColor(Color.BLACK);
		thickPaint.setStrokeWidth(THICK_STROKE);
		
		thinPaint = new Paint();
		thinPaint.setColor(Color.BLACK);
		thinPaint.setStrokeWidth(THIN_STROKE);
		
		textPaint = new Paint();
		textPaint.setStyle(Paint.Style.FILL);
		textPaint.setStrokeWidth(1);
		textPaint.setAntiAlias(true);
		textPaint.setColor(Color.BLACK);
		textPaint.setTextSize(TEXT_SIZE);
		
		redTextPaint = new Paint(textPaint);
		redTextPaint.setStyle(Paint.Style.FILL);
		redTextPaint.setStrokeWidth(1);
		redTextPaint.setAntiAlias(true);
		redTextPaint.setTextSize(TEXT_SIZE);
		redTextPaint.setColor(Color.RED);
		
		fixedNumberPaint = new Paint(textPaint);
		fixedNumberPaint.setTextSize(NUMBER_SIZE);
		fixedNumberPaint.setStrokeWidth(NUMBER_STROKE);
		
		lightNumberPaint = new Paint(fixedNumberPaint);
		lightNumberPaint.setTextSize(NUMBER_SIZE);
		lightNumberPaint.setStrokeWidth(NUMBER_STROKE*0.7f);
		lightNumberPaint.setColor(Color.MAGENTA);
		
		brightPaint = new Paint();
		brightPaint.setColor(Color.GREEN);
		brightPaint.setAlpha(100);
		
		brightCellPaint = new Paint(brightPaint);
		brightCellPaint.setColor(Color.YELLOW);
		brightCellPaint.setAlpha(110);
		
		normalPaint = new Paint(fixedNumberPaint);
		normalPaint.setTextSize(NUMBER_SIZE);
		normalPaint.setStrokeWidth(NUMBER_STROKE);
		normalPaint.setColor(Color.GRAY);
		
		selectedPaint = new Paint(normalPaint);
		selectedPaint.setColor(Color.RED);
		
		mSoundManager = new SoundManager();
        mSoundManager.initSounds(activity.getBaseContext());
        mSoundManager.addSound(SoundManager.SOUND_CLICK_ID, R.raw.sound_click);
        mSoundManager.addSound(SoundManager.SOUND_CLICK_LR_ID, R.raw.sound_click2);
        mSoundManager.addSound(SoundManager.SOUND_CLICK_SELECT_ID, R.raw.sound_click3);
	}
}
