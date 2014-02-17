package com.ant.sudoku;

import android.graphics.Canvas;
import android.graphics.Point;
import android.util.Log;
import android.view.MotionEvent;

public class SudokuBoard {
	private NumberBoard mNumberBoard;
	private Coordinate coord;
	private float width;
	private float cellWidth;
	private int[][][] flag;
	private int brightRow;
	private int brightCol;
	private Point downPoint;
	
	public SudokuBoard() {
		brightRow = -1;
		brightCol = -1;
		downPoint = new Point(-1,-1);
		
		//initialize flag array
		flag = new int[9][9][10];
		for (int i=0; i<9; i++)
			for (int j=0; j<9; j++) {
				for (int k=0; k<=9; k++)
					flag[i][j][k] = 0;
			}
	}
	
	//set the data for it to display
	public void setData(String str) {
		//put data for flag[][]
		for (int i=0; i<9; i++)
			for (int j=0; j<9; j++)
				for (int k=0; k<=9; k++) {
					char c = str.charAt(i*90 + j*10 + k);
					if (c == '-') 
						flag[i][j][k] = -1;
					else 
						flag[i][j][k] = c - '0';
				}
		//reset the bright row and column
		brightRow = -1;
		brightCol = -1;
		downPoint.x = -1;
		downPoint.y = -1;
		//reset the NumberBoard
		mNumberBoard.resetCellData();
	}
	
	//get the data
	public String getData() {
		String str = "";
		for (int i=0; i<9; i++)
			for (int j=0; j<9; j++)
				for (int k=0; k<=9; k++) {
					if (flag[i][j][k] == -1)
						str += '-';
					else 
						str += Integer.toString(flag[i][j][k]);
				}
		return str;
	}
	
	public void setSize(float x, float y, float width) {
		coord = new Coordinate(x, y);
		this.width = width;
		cellWidth = width / 9;
	}
	
	public void setNumberBoard(NumberBoard nb) {
		this.mNumberBoard = nb;
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
		
		
		//draw bright row and column
		if (brightRow != -1) {
			Coordinate c1 = getCoordLeftTop(brightRow,0);
			Coordinate c2 = getCoordRightBot(brightRow,8);
			canvas.drawRect(c1.x, c1.y, c2.x, c2.y, Common.brightPaint);	
		}
		if (brightCol != -1) {
			Coordinate c1 = getCoordLeftTop(0, brightCol);
			Coordinate c2 = getCoordRightBot(8, brightCol);
			canvas.drawRect(c1.x, c1.y, c2.x, c2.y, Common.brightPaint);	
		}
		
		//draw bright cells
		if (brightRow>=0 && brightRow<9 && brightCol>=0 && brightCol<9 && (flag[brightRow][brightCol][0]==-1 || flag[brightRow][brightCol][0]==1)) {
			int t = getFixedNumber(brightRow,brightCol);
			if (flag[brightRow][brightCol][0] == 1)
				for (t=1; t<=9; t++)
					if (flag[brightRow][brightCol][t] == 1)
						break;
			for (int i=0; i<9; i++)
				for (int j=0; j<9; j++) {
					if ((flag[i][j][0]==-1 || flag[i][j][0]==1) && (flag[i][j][t]==1)) { 
						Coordinate c1 = getCoordLeftTop(i,j);
						Coordinate c2 = getCoordRightBot(i,j);
						canvas.drawRect(c1.x, c1.y, c2.x, c2.y, Common.brightCellPaint);	
					}
				}
		}
	}
	
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
	
	private int getFixedNumber(int i, int j) {
		if (flag[i][j][0] != -1)
			return -1;
		int t;
		for (t=1; t<=9; t++)
			if (flag[i][j][t]==1) break;
		return t;
	}
	
	private void drawOneNumber(Canvas canvas, int i, int j, int num) {
		Coordinate c1 = getCoordLeftTop(i,j);
		Coordinate c2 = getCoordRightBot(i,j);
		float x = c1.x + (c2.x - c1.x)*0.28f;
		float y = c2.y - (c2.y - c1.y)*0.19f;
		float size = (c2.y-c1.y)*0.813f;
		Common.lightNumberPaint.setTextSize(size);
		canvas.drawText(Integer.toString(num), x, y, Common.lightNumberPaint);
	}
	
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
	
	//check if this subject includes the point
	public boolean includes(float x, float y) {
		if (x>=coord.x && x<=coord.x+width && y>=coord.y && y<=coord.y+width)
			return true;
		else 
			return false;
	}
	
	//get the coordinate and return the cell
	private Point findCell(float x, float y) {
		Point p = new Point(-1,-1);
		for (int i=0; i<9; i++)
			if (coord.x+i*cellWidth<x && x<coord.x+(i+1)*cellWidth) {
				p.y = i;
				break;
			}
		for (int i=0; i<9; i++)
			if (coord.y+i*cellWidth<y && y<coord.y+(i+1)*cellWidth) {
				p.x = i;
				break;
			}
		return p;
	}
	
	public void doTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		
		Point p;
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			p = findCell(x,y);
			//brighten row and column
			if (p.equals(brightRow, brightCol)) {
				downPoint.x = -1;
				downPoint.y = -1;
			} else {
				downPoint = p;
				brightenRowCol(p.x,p.y);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			p = findCell(x,y);
			//brighten row and column
			if (!downPoint.equals(-1,-1)) 
				brightenRowCol(p.x,p.y);
			break;
		case MotionEvent.ACTION_UP:
			p = findCell(x,y);
			//brighten row and column
			if (downPoint.equals(-1,-1)) {
				debrightRowCol();
				downPoint.x = -1;
				downPoint.y = -1;
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			break;
		}
	}
	
	//brighten one certain Row and one certain Column
	private void brightenRowCol(int x, int y) {
		brightRow = x;
		brightCol = y;
		transferCellData(x,y);
	}
	
	//debright the Row Col
	private void debrightRowCol() {
		brightRow = -1;
		brightCol = -1;
		resetCellData();
	}
	
	//transfer data for NumberBoard
	private void transferCellData(int x, int y) {
		if (x>=0 && x<9 && y>=0 && y<9) {
			mNumberBoard.receiveCellData(flag[x][y]);
		}
	}
	
	//reset NumberBoard
	private void resetCellData() {
		mNumberBoard.resetCellData();
	}
	
	//clear grid
	public void clearGrid() {
		for (int i=0; i<9; i++)
			for (int j=0; j<9; j++)
				if (flag[i][j][0] > 0)
					for (int k=0; k<10; k++)
						flag[i][j][k] = 0;
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
