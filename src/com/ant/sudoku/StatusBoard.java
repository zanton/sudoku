package com.ant.sudoku;

import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Canvas;

public class StatusBoard {
	private SudokuActivity mSudokuActivity;
	
	private Coordinate coord;
	private float width;
	
	private int order;
	private int level;
	private int time;
	
	public Timer timer; //call cancel() when the activity finishes
	private boolean timeRunning;
	public boolean cleared;
	
	public StatusBoard(SudokuActivity activity) {
		mSudokuActivity = activity;
		
		coord = null;
		width = 0;
		order = 0;
		level = 0;
		time = 0;
		timer = new Timer();
		try {
			timer.schedule(new TimerTask() {
				public void run() {
					incTime();
				}
			}, 1000l, 1000l);
		} catch (IllegalArgumentException e) {
			//do nothing
		} catch (IllegalStateException e) {
			//do nothing
		}
		
		timeRunning = false;
	}
	
	public void setData(int order, int level, int time, boolean cleared) {
		this.order = order;
		this.level = level;
		this.time = time;
		this.cleared = cleared;
	}
	
	public void runTimer() {
		if (!cleared)
			timeRunning = true;
	}
	
	public void incTime() {
		if (timeRunning) {
			time++;
			mSudokuActivity.mSudokuSurface.doDraw();
		}
	}
	
	public void stopTimer() {
		timeRunning = false;
	}
	
	public String getData() {
		return Integer.toString(order) + ',' + Integer.toString(level) + ',' + Integer.toString(time) + ',';
	}
	
	public void setSize(float x1, float y1, float x2, float y2) {
		coord = new Coordinate(x1, y2 - 2);
		Common.textPaint.setTextSize(y2 - y1);
		Common.redTextPaint.setTextSize(y2 - y1);
		width = x2 - x1;
	}
	
	private String getTimeString() {
		int sec = time % 60;
		int min = time / 60;
		int hour = min / 60;
		min = min % 60;
		//second
		String str = Integer.toString(sec);
		if (str.length()<2) 
			str = ":0" + str;
		else
			str = ':' + str;
		//minute
		if (min < 10)
			str = '0' + Integer.toString(min) + str;
		else 
			str = Integer.toString(min) + str;
		//hour
		if (hour > 0) {
			if (hour < 10)
				str = '0' + Integer.toString(hour) + ':' + str;
			else 
				str = Integer.toString(hour) + ':' + str;
		}
		
		return str;
	}
	
	public void draw(Canvas canvas) {
		//order, level, time
		String str1 = '#' + Integer.toString(order);
		String str2 = "Level: ";
		switch (level) {
		case 0:
			str2 += "Easy";
			break;
		case 1:
			str2 += "Medium";
			break;
		case 2:
			str2 += "Hard";
			break;
		}
		String str3 = getTimeString();
		
		//cleared or not
		String str_clear = "";
		if (mSudokuActivity.mSudokuBoard.checkGrid()) {
			str_clear = "Cleared";
			if (!cleared) {
				stopTimer();
				mSudokuActivity.showDialog(SudokuActivity.CONGRAT_DIALOG_ID);
				cleared = true;
			}
		} else {
			cleared = false;
			runTimer();
		}
		
		//draw
		canvas.drawText(str1, coord.x, coord.y, Common.textPaint);
		canvas.drawText(str_clear, coord.x + 30, coord.y, Common.redTextPaint);
		canvas.drawText(str2, width*0.35f, coord.y, Common.textPaint);
		canvas.drawText(str3, width-45, coord.y, Common.textPaint);
	}
	
	//clear time
	public void clearTime() {
		time = 0;
	}
}
