package com.ant.sudoku;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class SudokuSurface extends SurfaceView implements SurfaceHolder.Callback {
	private Context mContext;
	private SurfaceHolder mSurfaceHolder;
	private SudokuBoard mSudokuBoard;
	private StatusBoard mStatusBoard;
	private NumberBoard mNumberBoard;
	private int mCanvasWidth;
	private int mCanvasHeight;
	
	private int index; //index of current grid in Collector
	
	public SudokuSurface(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.i("SudokuSurface", "constructor");
		
		mContext = context;
		mSurfaceHolder = getHolder();
		mSurfaceHolder.addCallback(this);
		//initialize
		index = -1;
	}
	
	public void setInit(SudokuBoard sudokuBoard, StatusBoard statusBoard, NumberBoard numberBoard) {
		mSudokuBoard = sudokuBoard;
		mStatusBoard = statusBoard;
		mNumberBoard = numberBoard;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		
		//execute action by touched position
		if (mSudokuBoard.includes(x,y))
			mSudokuBoard.doTouchEvent(event);
		else if (mNumberBoard.includes(x,y))
			mNumberBoard.doTouchEvent(event);
		
		//redraw
		doDraw();
		
		return true;
	}
	
	//set data (grid, order, level, time) for SudokuBoard and StatusBoard
	public void setInitialData() {
		index = Collector.getLatestPlayingIndex();
		int order = Collector.parse2GetOrder(index);
		int level = Collector.parse2GetLevel(index);
		int time = Collector.parse2GetTime(index);
		String str = Collector.parse2GetGrid(index);
		mSudokuBoard.setData(str);
		
		if (mSudokuBoard.checkGrid())
			mStatusBoard.setData(order, level, time, true);
		else 
			mStatusBoard.setData(order, level, time, false);
		
		((SudokuActivity) mContext).showDialog(SudokuActivity.READY_DIALOG_ID);
	}
	
	public void setData(int index) {
		this.index = index;
		int order = Collector.parse2GetOrder(index);
		int level = Collector.parse2GetLevel(index);
		int time = Collector.parse2GetTime(index);
		String str = Collector.parse2GetGrid(index);
		//set data for components
		mSudokuBoard.setData(str);
		
		if (mSudokuBoard.checkGrid())
			mStatusBoard.setData(order, level, time, true);
		else { 
			mStatusBoard.setData(order, level, time, false);
			mStatusBoard.runTimer();
		}
	}
	
	//push changed data into Collector
	public void saveCurrentStatus() {
		Collector.removeLatestPlayingMark();
		String str1 = mStatusBoard.getData();
		String str2 = mSudokuBoard.getData();
		Collector.updateData(index, "p," + str1 + str2);
	}

	private void setSize(int width, int height) {
		//saving size of the surface
		mCanvasWidth = width;
		mCanvasHeight = height;
		
		//set padding between components
		float padding = 5.0f;
		
		//set size for StatusBoard
		float statusHeight = 15.0f;
		mStatusBoard.setSize(padding, padding, width-padding, padding+statusHeight);
		
		//set size for mSudokuBoard
		float boardWidth = width - 2*padding;
		mSudokuBoard.setSize(padding, 2*padding+statusHeight, boardWidth);
		
		//set size for mNumberBoard
		mNumberBoard.setSize(width*1/6, 4*padding+statusHeight+boardWidth, width*5/6, height - 2*padding);
	}
	
	public void doDraw() {
		Canvas canvas = mSurfaceHolder.lockCanvas(null);
		if (canvas != null) {
			//canvas.drawColor(Color.WHITE);
			Common.background.setBounds(0, 0, mCanvasWidth, mCanvasHeight);
			Common.background.draw(canvas);
	
			mStatusBoard.draw(canvas);
			mSudokuBoard.draw(canvas);
			mNumberBoard.draw(canvas);
			
			mSurfaceHolder.unlockCanvasAndPost(canvas);
		}
	}
	
	public int getCurrentIndex() {
		return index;
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		//doing nothing
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		setSize(width, height);
		if (index == -1) {
			setInitialData();
		} else {
			//in case back from grid selector and the setData() method was not called
			if (!mStatusBoard.cleared)
				mStatusBoard.runTimer();
		}
		doDraw();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		//do nothing
		int i;
		i = 2;
	}
}
