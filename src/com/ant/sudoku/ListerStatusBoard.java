package com.ant.sudoku;

import android.graphics.Canvas;

public class ListerStatusBoard {
	private Coordinate coord;
	private float width;
	
	private int order;
	private int level;
	private int time;
	public boolean cleared;
	
	public ListerStatusBoard() {
		cleared = false;
	}
	
	public void setData(int order, int level, int time) {
		this.order = order;
		this.level = level;
		this.time = time;
	}
	
	public void setSize(float x1, float y1, float x2, float y2) {
		coord = new Coordinate(x1, y2 - 2);
		Common.textPaint.setTextSize(y2 - y1);
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
		if (cleared)
			str_clear = "Cleared";
		
		//draw
		canvas.drawText(str1, coord.x, coord.y, Common.textPaint);
		canvas.drawText(str_clear, coord.x + 30, coord.y, Common.redTextPaint);
		canvas.drawText(str2, width*0.4f, coord.y, Common.textPaint);
		canvas.drawText(str3, width-45, coord.y, Common.textPaint);
	}
}
