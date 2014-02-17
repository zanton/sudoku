package com.ant.sudoku;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class ListerSurface extends SurfaceView implements SurfaceHolder.Callback{
	private Context mContext;
	private SurfaceHolder mSurfaceHolder;
	
	private ListerSudokuBoard mListerSudokuBoard;
	private ListerStatusBoard mListerStatusBoard;
	
	private int mCanvasWidth;
	private int mCanvasHeight;
	
	private Button btnNext;
	private Button btnPre;
	private Button btnSelect;
	
	private int index;
	
	public ListerSurface(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.i("ListerSurface", "constructor");
		
		mContext = context;
		mSurfaceHolder = getHolder();
		mSurfaceHolder.addCallback(this);
		
		mListerSudokuBoard = new ListerSudokuBoard();
		mListerStatusBoard = new ListerStatusBoard();
		
		btnNext = new Button();
		btnPre = new Button();
		btnSelect = new Button();
	}
	
	public void setData(int index) {
		this.index = index;
		int order = Collector.parse2GetOrder(index);
		int level = Collector.parse2GetLevel(index);
		int time = Collector.parse2GetTime(index);
		String str = Collector.parse2GetGrid(index);
		//set data for components
		mListerSudokuBoard.setData(str);
		mListerStatusBoard.setData(order,level,time);
		//check if cleared
		if (mListerSudokuBoard.checkGrid())
			mListerStatusBoard.cleared = true;
		else 
			mListerStatusBoard.cleared = false;
	}
	
	private void setSize(int width, int height) {
		//saving size of the surface
		mCanvasWidth = width;
		mCanvasHeight = height;
		
		//set padding between components
		float padding = 4*5.0f;
		
		//set size for StatusBoard
		float statusHeight = 15.0f;
		mListerStatusBoard.setSize(padding, padding, width-padding, padding+statusHeight);
		
		//set size for mSudokuBoard
		float boardWidth = width - 2*padding;
		mListerSudokuBoard.setSize(padding, 2*padding+statusHeight, boardWidth);
		
		//set coordinate for btnNext, btnPre, btnSelect
		float side = boardWidth/4;
		//btnPre
		Coordinate lefttop = new Coordinate((width - 3*side - 2*padding)/2, 4*padding + statusHeight + boardWidth);
		btnPre.addPoint(lefttop.x, lefttop.y + side/2);
		btnPre.addPoint(lefttop.x + side, lefttop.y);
		btnPre.addPoint(lefttop.x + side, lefttop.y + side);
		//btnSelect
		lefttop.transition(side + padding, 0);
		btnSelect.addPoint(lefttop);
		btnSelect.addPoint(lefttop.x + side, lefttop.y);
		btnSelect.addPoint(lefttop.x + side, lefttop.y + side);
		btnSelect.addPoint(lefttop.x, lefttop.y + side);
		//btnNext
		lefttop.transition(side + padding, 0);
		btnNext.addPoint(lefttop);
		btnNext.addPoint(lefttop.x + side, lefttop.y + side/2);
		btnNext.addPoint(lefttop.x, lefttop.y + side);
	}
	
	public void doDraw() {
		Canvas canvas = mSurfaceHolder.lockCanvas(null);
		
		//draw background
		//canvas.drawColor(Color.WHITE);
		Common.background.setBounds(0, 0, mCanvasWidth, mCanvasHeight);
		Common.background.draw(canvas);
		
		//draw StatusBoard and SudokuBoard
		mListerStatusBoard.draw(canvas);
		mListerSudokuBoard.draw(canvas);
		
		//draw three buttons
		btnPre.draw(canvas);
		btnNext.draw(canvas);
		btnSelect.draw(canvas);
		
		mSurfaceHolder.unlockCanvasAndPost(canvas);
	}
	
	private int touchdown_flag; //1->pre, 2->select, 3->next
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		
		//only execute when it's ACTION_DOWN or ACTION_UP
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (btnPre.include(x,y))
				touchdown_flag = 1;
			else if (btnSelect.include(x,y))
				touchdown_flag = 2;
			else if (btnNext.include(x,y))
				touchdown_flag = 3;
			else 
				touchdown_flag = 0;
			break;
		case MotionEvent.ACTION_UP:
			int flag = -1;
			if (btnPre.include(x,y))
				flag = 1;
			else if (btnSelect.include(x,y))
				flag = 2;
			else if (btnNext.include(x,y))
				flag = 3;
			if (flag == touchdown_flag) {
				switch (flag) {
				case 1: //btnPre clicked
					if (index > 0)
						setData(index-1);
					else 
						setData(Collector.size()-1);
					Common.mSoundManager.playSound(SoundManager.SOUND_CLICK_LR_ID);
					break;
				case 2: //btnSelect clicked
					Intent intent = new Intent();
					intent.putExtra("SelectedIndex", index);
					((Activity) mContext).setResult(Activity.RESULT_OK, intent);
					((Activity) mContext).finish();
					Common.mSoundManager.playSound(SoundManager.SOUND_CLICK_SELECT_ID);
					break;
				case 3: //btnNext clicked
					if (index < Collector.size()-1)
						setData(index+1);
					else 
						setData(0);
					Common.mSoundManager.playSound(SoundManager.SOUND_CLICK_LR_ID);
					break;
				}
			}
			break;
		}
		//redraw
		doDraw();
		return true;
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		//doing nothing
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		setSize(width, height);
		doDraw();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		//do nothing
	}
}

class Button {
	private ArrayList<Coordinate> point;
	
	public Button() {
		point = new ArrayList<Coordinate>();
	}
	
	public void addPoint(Coordinate c) {
		point.add(new Coordinate(c));
	}
	
	public void addPoint(float x, float y) {
		point.add(new Coordinate(x,y));
	}
	
	public void draw(Canvas canvas) {
		for (int i=0; i<point.size()-1; i++)
			canvas.drawLine(point.get(i).x, point.get(i).y, point.get(i+1).x, point.get(i+1).y, Common.thickPaint);
		canvas.drawLine(point.get(point.size()-1).x, point.get(point.size()-1).y, point.get(0).x, point.get(0).y, Common.thickPaint);
	}
	
	public boolean include(Coordinate c) {
		int n = point.size();
		for (int i=0; i<n-2; i++)
			if (product(point.get(i), point.get(i+1), c, point.get(i+2)) < 0)
				return false;
		if (product(point.get(n-2), point.get(n-1), c, point.get(0)) < 0)
			return false;
		if (product(point.get(n-1), point.get(0), c, point.get(1)) < 0)
			return false;
		return true;
	}
	
	public boolean include(float x, float y) {
		return include(new Coordinate(x,y));
	}
	
	private float product(Coordinate c1, Coordinate c2, Coordinate c3, Coordinate c4) {
		float t1 = (c3.x-c1.x)/(c2.x-c1.x)-(c3.y-c1.y)/(c2.y-c1.y);
		float t2 = (c4.x-c1.x)/(c2.x-c1.x)-(c4.y-c1.y)/(c2.y-c1.y);
		return t1*t2;
	}
}