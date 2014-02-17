package com.ant.sudoku;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HelpDialog extends Dialog {
	Context context;
	
	public HelpDialog(Context context) {
		super(context);
		this.context = context;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.help);
		this.setTitle(R.string.title_help);
		Button but = (Button) this.findViewById(R.id.btnHelp);
		but.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dismiss();
				((SudokuActivity) context).mStatusBoard.runTimer();
			}
		});
	}
}
