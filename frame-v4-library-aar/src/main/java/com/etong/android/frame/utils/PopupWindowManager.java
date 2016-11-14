package com.etong.android.frame.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

import com.etong.android.frame.R;

public class PopupWindowManager {
	private static PopupWindowManager instance;

	private PopupWindowManager() {

	}

	static public PopupWindowManager getInstance() {
		if (null == instance) {
			instance = new PopupWindowManager();
		}
		return instance;
	}

	public View createPopupWindow(Context context, View parent) {

		View popupView = LayoutInflater.from(context).inflate(
				R.layout.popup_window_common_list, null);

		final PopupWindow popupWindow = new PopupWindow(popupView,
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);

		View close = popupView.findViewById(R.id.popup_window_close);
		if (null != close) {
			close.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					popupWindow.dismiss();
				}
			});
		}

		popupWindow.setTouchable(true);

		popupWindow.setAnimationStyle(R.style.anim_fade);

		popupWindow.showAtLocation(parent, Gravity.BOTTOM
				| Gravity.CENTER_HORIZONTAL, 0, 0);

		return popupView;
	}

	/**
	 * @Title : showPopupWindow
	 * @Description : 显示POPUP WINDOW 的栗�?
	 * @params
	 * @param context
	 * @param view
	 * @return 设定文件
	 * @return View 返回类型
	 */
	public View showPopupWindow(Context context, View view) {

		// �?��自定义的布局，作为显示的内容
		View contentView = LayoutInflater.from(context).inflate(
				R.layout.popup_window_common_list, null);

		final PopupWindow popupWindow = new PopupWindow(contentView,
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);

		View close = contentView.findViewById(R.id.popup_window_close);
		if (null != close) {
			close.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					popupWindow.dismiss();
				}
			});
		}

		popupWindow.setTouchable(true);

		popupWindow.setAnimationStyle(R.style.anim_fade);

		popupWindow.setTouchInterceptor(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return false;
				// 这里如果返回true的话，touch事件将被拦截
				// 拦截�?PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
			}
		});

		// 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
		// 这个是为了点击�?返回Back”也能使其消失，并且并不会影响你的背�?
		// popupWindow.setBackgroundDrawable(new BitmapDrawable());

		// 底部弹出
		popupWindow.showAtLocation(view, Gravity.BOTTOM
				| Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置

		// 设置好参数之后再show
		// popupWindow.showAsDropDown(view);

		return contentView;
	}

}
