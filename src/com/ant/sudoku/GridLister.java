package com.ant.sudoku;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class GridLister extends Activity {
	ListerSurface mListerSurface;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.lister);
        
        mListerSurface = (ListerSurface) findViewById(R.id.lister_surface);
        
        //get the transfered data
        int index = getIntent().getIntExtra("InitialIndex", 0);
        mListerSurface.setData(index);
	}
}
