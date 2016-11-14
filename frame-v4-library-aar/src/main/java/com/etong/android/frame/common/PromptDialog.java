package com.etong.android.frame.common;

import com.etong.android.frame.R;

import android.content.Context;
import android.app.Dialog;
import android.os.Bundle;

public class PromptDialog extends Dialog {

	Context context;

	public PromptDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog_prompt);
	}

}
