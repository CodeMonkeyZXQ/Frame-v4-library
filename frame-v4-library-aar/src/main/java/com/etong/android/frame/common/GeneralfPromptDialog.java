package com.etong.android.frame.common;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.etong.android.frame.R;

public class GeneralfPromptDialog extends Dialog {

	private TextView mTitle;
	private TextView mText;
	private Button mConfirmButton;
	private Button mCancleButton;
	private String title = "提示";
	private String text = " ";
	private String buttonConfirm = "确定";
	private String buttonCancle = "取消";
	private android.view.View.OnClickListener mClickListerer = new android.view.View.OnClickListener() {

		@Override
		public void onClick(View v) {
			GeneralfPromptDialog.this.dismiss();
		}
	};

	public GeneralfPromptDialog(Context context) {
		super(context, R.style.dialog_nor);
	}

	/**
	 * @Title : setTitle
	 * @Description : 设置标题
	 * @params
	 * @param title
	 *            标题
	 * @return void 返回类型
	 */
	public void setTitle(String title) {
		this.title = title;
		if (this.isShowing()) {
			mTitle.setText(title);
		}
	}

	/**
	 * @Title : setText
	 * @Description : 设置内容
	 * @params
	 * @param text
	 *            内容
	 * @return void 返回类型
	 */
	public void setText(String text) {
		this.text = text;
		if (this.isShowing()) {
			mText.setText(text);
		}
	}

	/**
	 * @Title : setButtonText
	 * @Description : 设置按钮文字
	 * @params
	 * @param confirmText
	 *            按钮文字
	 * @return void 返回类型
	 */
	public void setButtonText(String confirmText, String cancleText) {
		if (null != confirmText && !TextUtils.isEmpty(confirmText)) {
			this.buttonConfirm = confirmText;
			if (this.isShowing()) {
				mConfirmButton.setText(confirmText);
			}
		}
		if (null != cancleText && !TextUtils.isEmpty(cancleText)) {
			this.buttonCancle = cancleText;
			if (this.isShowing()) {
				mCancleButton.setText(cancleText);
			}
		}
	}

	/**
	 * @Title : setButtonClickListener
	 * @Description : 设置按钮点击事件
	 * @params
	 * @param onClickListener
	 *            点击事件
	 * @return void 返回类型
	 */
	public void setButtonClickListener(
			android.view.View.OnClickListener onClickListener) {
		this.mClickListerer = onClickListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setCancelable(false);
		this.setContentView(R.layout.dialog_general_prompt);
		mTitle = (TextView) this.findViewById(R.id.dialog_general_title);
		mText = (TextView) this.findViewById(R.id.dialog_general_text);
		mConfirmButton = (Button) this.findViewById(R.id.dialog_general_ok);
		mCancleButton = (Button) this.findViewById(R.id.dialog_general_cancle);
	}

	@Override
	public void show() {
		super.show();
		mTitle.setText(title);
		mText.setText(text);
		mConfirmButton.setText(buttonConfirm);
		mConfirmButton.setOnClickListener(mClickListerer);
		mCancleButton.setText(buttonCancle);
		mCancleButton
				.setOnClickListener(new android.view.View.OnClickListener() {

					@Override
					public void onClick(View v) {
						GeneralfPromptDialog.this.dismiss();
					}
				});
	}

}
