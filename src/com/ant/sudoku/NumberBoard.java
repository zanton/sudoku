package com.ant.sudoku;

import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;

public class NumberBoard {
	private SudokuBoard mSudokuBoard;
	private Coordinate coord;
	private float width;
	private float height;
	private float cellWidth;
	private int[] data;	//1->bright
	private int[] data2; //empty array used to reset data[]
	
	public NumberBoard() {
		data2 = new int[10];
		for (int i=0; i<10; i++)
			data2[i] = 0;
		data = data2;
	}
	
	public void setSudokuBoard(SudokuBoard sb) {
		mSudokuBoard = sb;
	}
	
	public void setSize(float x1, float y1, float x2, float y2) {
		coord = new Coordinate(x1, y1);
		if ((x2-x1)/5 <= (y2-y1)/2) {
			width = x2 - x1;
			cellWidth = width / 5;
			height = cellWidth*2;
		} else {
			height = y2 - y1;
			cellWidth = height / 2;
			width = cellWidth*5;
		}
	}
	
	public void draw(Canvas canvas) {
		Log.i("NumberBoard", "begin draw function");
		float[] pts1 = new float[4*4]; //for bold lines
		float[] pts2 = new float[5*4]; //for light lines
		int n1, n2;
		
		n1 = n2 = 0;
		
		//top line
		pts1[n1++] = coord.x;
		pts1[n1++] = coord.y;
		pts1[n1++] = pts1[n1-3] + 5*cellWidth;
		pts1[n1++] = pts1[n1-3];
		//left line
		pts1[n1++] = coord.x;
		pts1[n1++] = coord.y;
		pts1[n1++] = pts1[n1-3];
		pts1[n1++] = pts1[n1-3] + 2*cellWidth;
		//bottom line
		pts1[n1++] = coord.x;
		pts1[n1++] = coord.y + 2*cellWidth;
		pts1[n1++] = pts1[n1-3] + 5*cellWidth;
		pts1[n1++] = pts1[n1-3];
		//right line
		pts1[n1++] = coord.x + 5*cellWidth;
		pts1[n1++] = coord.y;
		pts1[n1++] = pts1[n1-3];
		pts1[n1++] = pts1[n1-3] + 2*cellWidth;
		
		//light lines
		for (int i=1; i<=4; i++) {
			pts2[n2++] = coord.x + i*cellWidth;
			pts2[n2++] = coord.y;
			pts2[n2++] = pts2[n2-3];
			pts2[n2++] = pts2[n2-3] + 2*cellWidth;
		}
		pts2[n2++] = coord.x;
		pts2[n2++] = coord.y + cellWidth;
		pts2[n2++] = pts2[n2-3] + 5*cellWidth;
		pts2[n2++] = pts2[n2-3];
		
		//draw lines
		canvas.drawLines(pts1, 0, n1, Common.thickPaint);
		canvas.drawLines(pts2, 0, n2, Common.thinPaint);
		
		//draw numbers
		Common.normalPaint.setTextSize(cellWidth*0.82f);
		Common.selectedPaint.setTextSize(cellWidth*0.82f);
		for (int i=1; i<10; i++) {
			Coordinate c = getCoordToDrawNumber(i);
			if (data[i]==0)
				canvas.drawText(Integer.toString(i), c.x, c.y, Common.normalPaint);
			else
				canvas.drawText(Integer.toString(i), c.x, c.y, Common.selectedPaint);
		}
		Coordinate c = getCoordToDrawNumber(10);
		canvas.drawText("C", c.x, c.y, Common.normalPaint);
	}
	
	private Coordinate getCoordToDrawNumber(int t) {
		int x = (t-1) / 5;
		int y = t - 5*x - 1;
		Coordinate c1 = new Coordinate(coord.x+y*cellWidth, coord.y+x*cellWidth);
		Coordinate c2 = new Coordinate(coord.x+(y+1)*cellWidth, coord.y+(x+1)*cellWidth);
		Coordinate c = new Coordinate(c1.x + (c2.x - c1.x)*0.28f, c2.y - (c2.y - c1.y)*0.19f);
		return c;
	}
	
	public boolean includes(float x, float y) {
		if (coord.x<=x && x<=coord.x+width && coord.y<=y && y<=coord.y+height)
			return true;
		else
			return false;
	}
	
	public void doTouchEvent(MotionEvent event) {
		if (data[0] == -1) return;
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			int t = findTouchedNumber(event.getX(), event.getY());
			if (t==-1) {
				return ;
			} else if (t == 10) {
				for (int i=0; i<=9; i++)
					data[i] = 0;
			} else {
				if (data[t] == 0)
					data[0]++;
				else data[0]--;
				data[t] = 1 - data[t];
			}
			Common.mSoundManager.playSound(SoundManager.SOUND_CLICK_ID);
		}
	}
	
	private int findTouchedNumber(float x, float y) {
		for (int i=0; i<5; i++)
			for (int j=0; j<2; j++) {
				float x1 = coord.x + i*cellWidth;
				float x2 = coord.x + (i+1)*cellWidth;
				float y1 = coord.y + j*cellWidth;
				float y2 = coord.y + (j+1)*cellWidth;
				if (x>=x1 && x<=x2 && y>=y1 && y<=y2)
					return j*5+i+1;
			}
		return -1;
	}
	
	public void receiveCellData(int[] data) {
		this.data = data;
	}
	
	public void resetCellData() {
		data = data2;
	}
}
