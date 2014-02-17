package com.ant.sudoku;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.Window;

public class SudokuActivity extends Activity {
	static final int ABOUT_DIALOG_ID = 0;
	static final int HELP_DIALOG_ID = 1;
	static final int READY_DIALOG_ID = 2;
	static final int CONFIRM_CLEAR_DIALOG_ID = 3;
	static final int CONGRAT_DIALOG_ID = 4;
	
	public SudokuSurface mSudokuSurface;
	private SurfaceHolder mSurfaceHolder;
	public SudokuBoard mSudokuBoard;
	public StatusBoard mStatusBoard;
	private NumberBoard mNumberBoard;
	private SudokuGenerator generator;
	
	private AboutDialog mAboutDialog;
	private HelpDialog mHelpDialog;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        
        //initialize game factors
        Common.init(getResources(), this);
        mSudokuBoard = new SudokuBoard();
        mStatusBoard = new StatusBoard(this);
        mNumberBoard = new NumberBoard();
        
        mSudokuSurface = (SudokuSurface) findViewById(R.id.surface);
        mSudokuSurface.setInit(mSudokuBoard, mStatusBoard, mNumberBoard);
        mSudokuBoard.setNumberBoard(mNumberBoard);
        mNumberBoard.setSudokuBoard(mSudokuBoard);
        mSurfaceHolder = mSudokuSurface.getHolder();
        generator = new SudokuGenerator(3);
        
        mAboutDialog = new AboutDialog(this);
        mHelpDialog = new HelpDialog(this);
        
        Collector.initialize(this);
       
        Log.i("SudokuActivity", "onCreate ends");
    }
    
    /*@Override
    protected void onPause() {
    	mSudokuSurface.saveCurrentStatus(); //save to Collector
    }*/
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	mSudokuSurface.saveCurrentStatus();
    	Collector.dismiss();
    	mStatusBoard.timer.cancel();
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	mStatusBoard.stopTimer();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	mStatusBoard.runTimer();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_clearGrid:
        	showDialog(CONFIRM_CLEAR_DIALOG_ID);
            return true;
        case R.id.menu_allGrids:
        	mSudokuSurface.saveCurrentStatus(); //save to Collector
        	mStatusBoard.stopTimer(); //stop timer in StatusBoard
        	Intent intent = new Intent(SudokuActivity.this, GridLister.class);
        	intent.putExtra("InitialIndex", mSudokuSurface.getCurrentIndex());
        	try {
        		startActivityForResult(intent, 0);
        	} catch (ActivityNotFoundException e) {
        		Log.i("SudokuActivity", "GridLister activity not found!");
        	}
        	return true;
        case R.id.menu_help:
        	showDialog(HELP_DIALOG_ID);
        	return true;
        case R.id.menu_about:
        	showDialog(ABOUT_DIALOG_ID);
        	return true;
        /*default:
        	return super.onOptionsItemSelected(item);*/
        }
        return true;
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	switch (id) {
    	case ABOUT_DIALOG_ID:
    		mStatusBoard.stopTimer();
    		return mAboutDialog;
    	case HELP_DIALOG_ID:
    		mStatusBoard.stopTimer();
    		return mHelpDialog;
    	case READY_DIALOG_ID:
    		mStatusBoard.stopTimer();
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setMessage(R.string.txt_ready_dialog);
        	builder.setPositiveButton(R.string.txt_ok, 
        			new DialogInterface.OnClickListener() {
        				public void onClick(DialogInterface dialog, int id) {
        					dialog.cancel();
        				}
        			});
        	builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
        		@Override
        		public void onCancel(DialogInterface dialog) {
        			if (!mStatusBoard.cleared)
        				mStatusBoard.runTimer();
        		}
        	});
            return builder.create();
    	case CONFIRM_CLEAR_DIALOG_ID:
    		AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
    		builder2.setMessage(R.string.txt_clear_dialog);
        	builder2.setPositiveButton(R.string.txt_ok, 
        			new DialogInterface.OnClickListener() {
        				public void onClick(DialogInterface dialog, int id) {
        					mSudokuBoard.clearGrid();
        					mStatusBoard.clearTime();
        					mStatusBoard.runTimer();
        					mSudokuSurface.doDraw();
        				}
        			});
        	builder2.setNegativeButton(R.string.txt_cancel, 
        			new DialogInterface.OnClickListener() {
        				public void onClick(DialogInterface dialog, int id) {
        					dialog.cancel();
        				}
        			});
            return builder2.create();
    	case CONGRAT_DIALOG_ID:
    		AlertDialog.Builder builder3 = new AlertDialog.Builder(this);
    		builder3.setMessage(R.string.txt_congrat_dialog);
        	builder3.setPositiveButton(R.string.txt_ok, 
        			new DialogInterface.OnClickListener() {
        				public void onClick(DialogInterface dialog, int id) {
        					//do nothing
        				}
        			});
            return builder3.create();
    	}
    	return null;
    }
    
    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
    	mStatusBoard.stopTimer();
    	super.onPrepareDialog(id, dialog);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == 0) { 
    		if (resultCode == RESULT_OK) {
    			//get the new grid data from data intent
    			int index = data.getIntExtra("SelectedIndex", -1);
    			if (index != -1)
    				mSudokuSurface.setData(index);
    		}
    	}
    }
}