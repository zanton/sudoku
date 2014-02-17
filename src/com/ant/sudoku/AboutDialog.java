package com.ant.sudoku;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AboutDialog extends Dialog {
	Context context;

	public AboutDialog(Context context) {
		super(context);
		this.context = context;
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.about);
		this.setTitle(R.string.title_about);
		Button but = (Button) this.findViewById(R.id.btnAbout);
		but.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dismiss();
				((SudokuActivity) context).mStatusBoard.runTimer();
			}
		});
	}
}
