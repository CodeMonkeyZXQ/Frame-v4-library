package com.etong.android.frame.common;

import java.util.List;

import com.etong.android.frame.R;
import com.etong.android.frame.widget.ArrayWheelAdapter;
import com.etong.android.frame.widget.ListWheelAdapter;
import com.etong.android.frame.widget.WheelView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class WheelDialog extends Dialog {

	private WheelView mWheelView;

	@SuppressLint("InflateParams")
	public WheelDialog(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		WheelView.setWheelBackground(R.drawable.wheel_bg, R.drawable.wheel_val);

		Window window = this.getWindow();
		window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

		View view = this.getLayoutInflater().inflate(R.layout.dialog_wheel,
				null);
		setContentView(view);

		initView(view);
	}

	protected void initView(View view) {
		mWheelView = (WheelView) view.findViewById(R.id.wheel_wheelview);

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T> void addAll(List<T> list) {
		mWheelView.setViewAdapter(new ListWheelAdapter(getContext(),
				R.layout.list_item_wheelview, list));
		mWheelView.setCyclic(false);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T> void addAll(T[] items) {
		mWheelView.setViewAdapter(new ArrayWheelAdapter(getContext(),
				R.layout.list_item_wheelview, items));
		mWheelView.setCyclic(false);
	}

}