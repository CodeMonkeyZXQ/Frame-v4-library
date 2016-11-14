package com.etong.android.frame.common;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.etong.android.frame.R;
import com.etong.android.frame.utils.CToast;

public class InputDialog extends Dialog {
	private OnConfirmListener mOnConfirmListener;
	private EditText mInput;
	private TextView mTitle;
	private View mClose;
	private View mConfirm;
	@SuppressWarnings("unused")
	private LayoutInflater mInflater;

	public interface OnConfirmListener {
		public void onConfirm(View view, String val);
	};

	public void setOnConfirmListener(OnConfirmListener listener) {
		mOnConfirmListener = listener;
	}

	public InputDialog(Context context) {
		super(context);
		mInflater = LayoutInflater.from(context);
	}

	// 显示dialog
	public void show(String title, String def) {
		super.show();
		if (null != def) {
			mInput.setText(def);
		}

		if (null != title) {
			mTitle.setText(title);
		}

	}

	public EditText getmInput() {
		return mInput;
	}

	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		requestWindowFeature(Window.PROGRESS_VISIBILITY_ON);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_input);

		setCanceledOnTouchOutside(false);
		this.setCancelable(false);

		mInput = (EditText) findViewById(R.id.dialog_input_name);
		mTitle = (TextView) findViewById(R.id.dialog_input_title);
		mClose = findViewById(R.id.dialog_input_close);
		mConfirm = findViewById(R.id.dialog_input_confirm);

		mClose.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				InputDialog.this.hide();
			}
		});

		mConfirm.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (null != mOnConfirmListener) {
					String text = mInput.getText().toString();
					if (text.isEmpty()) {
						CToast.toastMessage("用户名不能为空!", 1);
						return;
					}
					if (text.length() > 10) {
						CToast.toastMessage("用户名长度过长!", 1);
						return;
					}

					mOnConfirmListener.onConfirm(arg0, mInput.getText()
							.toString());
				}
				InputDialog.this.hide();
			}
		});
	}

}
