package com.ant.sudoku;

import android.graphics.Canvas;
import android.util.Log;

public class ListerSudokuBoard {
	private Coordinate coord;
	private float width;
	private float cellWidth;
	private int[][][] flag;
	
	public ListerSudokuBoard() {
		//initialize flag array
		flag = new int[9][9][10];
		for (int i=0; i<9; i++)
			for (int j=0; j<9; j++) {
				for (int k=0; k<=9; k++)
					flag[i][j][k] = 0;
			}
	}
	
	/*//reset the displayed grid for this ListerSudokuBoard (change flag array)
	public void reset(String str) {
		for (int i=0; i<9; i++)
			for (int j=0; j<9; j++)
				for (int k=0; k<=9; k++) {
					char c = str.charAt(i*90 + j*9 + k);
					if (c == '-') 
						flag[i][j][k] = -1;
					else 
						flag[i][j][k] = c - '0';
				}
	}*/
	
	//set the data for it to display
	public void setData(String str) {
		for (int i=0; i<9; i++)
			for (int j=0; j<9; j++)
				for (int k=0; k<=9; k++) {
					char c = str.charAt(i*90 + j*10 + k);
					if (c == '-') 
						flag[i][j][k] = -1;
					else 
						flag[i][j][k] = c - '0';
				}
	}
	
	public void setSize(float x, float y, float width) {
		coord = new Coordinate(x, y);
		this.width = width;
		cellWidth = width / 9;
	}
	
	public void draw(Canvas canvas) {
		Log.i("SudokuBoard", "begin draw function");
		
		float[] pts1 = new float[8*4];
		float[] pts2 = new float[12*4];
		int n1, n2;
		
		n1 = n2 = 0;
		//set vertical lines into pts1
		for (int i=0; i<=9; i++)
			if (i % 3 == 0) {
				pts1[n1++] = coord.x + i*cellWidth;
				pts1[n1++] = coord.y;
				pts1[n1++] = pts1[n1-3];
				pts1[n1++] = coord.y + 9*cellWidth;
			} else {
				pts2[n2++] = coord.x + i*cellWidth;
				pts2[n2++] = coord.y;
				pts2[n2++] = pts2[n2-3];
				pts2[n2++] = coord.y + 9*cellWidth;
			}
		//set horizontal lines into pts2
		for (int i=0; i<=9; i++)
			if (i % 3 == 0) {
				pts1[n1++] = coord.x;
				pts1[n1++] = coord.y + i*cellWidth;
				pts1[n1++] = coord.x + 9*cellWidth;
				pts1[n1++] = pts1[n1-3];
			} else {
				pts2[n2++] = coord.x;
				pts2[n2++] = coord.y + i*cellWidth;
				pts2[n2++] = coord.x + 9*cellWidth;
				pts2[n2++] = pts2[n2-3];
			}
		
		//draw pts1, pts2
		canvas.drawLines(pts1, 0, n1, Common.thickPaint);
		canvas.drawLines(pts2, 0, n2, Common.thinPaint);
		
		//draw numbers
		/*for (int i=0; i<9; i++)
			for (int j=0; j<9; j++)
				if (grid[i][j] > 0) {
					drawOneNumber(canvas, i, j, grid[i][j]);
				}*/
		for (int i=0; i<9; i++)
			for (int j=0; j<9; j++)
				if (flag[i][j][0] == -1) {
					for (int k=1; k<=9; k++)
						if (flag[i][j][k] == 1) {
							drawFixedOneNumber(canvas, i, j, k);
							break;
						}
				} else if (flag[i][j][0] == 1) {
					for (int k=1; k<=9; k++)
						if (flag[i][j][k] == 1) {
							drawOneNumber(canvas, i, j, k);
							break;
						}
				} else if (flag[i][j][0] > 1) {
					for (int k=1; k<=9; k++)
						if (flag[i][j][k] == 1) {
							drawManyNumbers(canvas, i, j, k);
						}
				}
	}
	
	//draw the number in fixed one_number cell
	private void drawFixedOneNumber(Canvas canvas, int i, int j, int num) {
		Coordinate c1 = getCoordLeftTop(i,j);
		Coordinate c2 = getCoordRightBot(i,j);
		float x = c1.x + (c2.x - c1.x)*0.28f;
		float y = c2.y - (c2.y - c1.y)*0.19f;
		float size = (c2.y-c1.y)*0.813f;
		Common.fixedNumberPaint.setTextSize(size);
		int t = getFixedNumber(i,j);
		canvas.drawText(Integer.toString(t), x, y, Common.fixedNumberPaint);
	}
	
	//draw the number in cell which has only one number candidate
	private void drawOneNumber(Canvas canvas, int i, int j, int num) {
		Coordinate c1 = getCoordLeftTop(i,j);
		Coordinate c2 = getCoordRightBot(i,j);
		float x = c1.x + (c2.x - c1.x)*0.28f;
		float y = c2.y - (c2.y - c1.y)*0.19f;
		float size = (c2.y-c1.y)*0.813f;
		Common.lightNumberPaint.setTextSize(size);
		canvas.drawText(Integer.toString(num), x, y, Common.lightNumberPaint);
	}
	
	//draw one of the many number candidates that the cell has
	private void drawManyNumbers(Canvas canvas, int i, int j, int num) {
		Coordinate c1 = getCoordLeftTop(i,j);
		Coordinate c2 = getCoordRightBot(i,j);
		float padding = (c2.x - c1.x)*0.12f;
		float x = c1.x + padding*1.3f + ((num-1)%3)*( (c2.x - c1.x)*0.2f + padding*0.6f );
		float y = c1.y + ((num-1)/3 + 1)*( (c2.y - c1.y)*0.25f + padding*0.4f );
		float size = (c2.y-c1.y)*0.35f;
		Common.lightNumberPaint.setTextSize(size*0.7f);
		canvas.drawText(Integer.toString(num), x, y, Common.lightNumberPaint);
	}
	
	private Coordinate getCoordLeftTop(int y, int x) {
		return new Coordinate(coord.x+x*cellWidth, coord.y+y*cellWidth);
	}
	
	private Coordinate getCoordRightBot(int y, int x) {
		return new Coordinate(coord.x+(x+1)*cellWidth, coord.y+(y+1)*cellWidth);
	}
	
	private int getFixedNumber(int i, int j) {
		if (flag[i][j][0] != -1)
			return -1;
		int t;
		for (t=1; t<=9; t++)
			if (flag[i][j][t]==1) break;
		return t;
	}
	
	//check if this subject includes the point
	public boolean includes(float x, float y) {
		if (x>=coord.x && x<=coord.x+width && y>=coord.y && y<=coord.y+width)
			return true;
		else 
			return false;
	}
	
	//check grid
	public boolean checkGrid() {
		//check -1 and 1 only
		for (int i=0; i<9; i++)
			for (int j=0; j<9; j++)
				if (flag[i][j][0]!=-1 && flag[i][j][0]!=1)
					return false;
		int[] a = new int[10];
		//check rows
		for (int i=0; i<9; i++) {
			int t;
			for (t=1; t<=9; t++)
				a[t] = 0;
			for (int j=0; j<9; j++) {
				for (t=1; t<=9; t++)
					if (flag[i][j][t] == 1) 
						break;
				if (a[t]==0) 
					a[t] = 1;
				else 
					return false;
			}
		}
		//check columns
		for (int j=0; j<9; j++) {
			int t;
			for (t=1; t<=9; t++)
				a[t] = 0;
			for (int i=0; i<9; i++) {
				for (t=1; t<=9; t++)
					if (flag[i][j][t] == 1)
						break;
				if (a[t]==0)
					a[t] = 1;
				else 
					return false;
			}
		}
		//check block
		for (int x=0; x<3; x++)
			for (int y=0; y<3; y++) {
				int t;
				for (t=1; t<=9; t++)
					a[t] = 0;
				for (int i=x*3; i<x*3+3; i++)
					for (int j=y*3; j<y*3+3; j++) {
						for (t=1; t<=9; t++)
							if (flag[i][j][t] == 1)
								break;
						if (a[t]==0)
							a[t] = 1;
						else 
							return false;
					}
			}
				
		return true;
	}
}
