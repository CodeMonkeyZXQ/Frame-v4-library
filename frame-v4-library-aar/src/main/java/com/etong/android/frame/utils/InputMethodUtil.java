package com.etong.android.frame.utils;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

/**
 * @ClassName : InputMethodUtil
 * @Description : 对于软键盘的管理
 * @author : zhouxiqing
 * @date : 2015-10-20 上午9:41:45
 * 
 */
public class InputMethodUtil {

	/**
	 * @Title : showInputMethodDelay
	 * @Description : 延迟500ms打开软键盘
	 * @params
	 * @param context
	 *            设定文件
	 * @return void 返回类型
	 */
	public static void showInputMethodDelay(final Context context) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				InputMethodManager imm = ServiceManager
						.getInputMethodManager(context);
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}, 500);
	}

	/**
	 * @Title : showInputMethodDelay
	 * @Description : 延迟打开软键盘
	 * @params
	 * @param context
	 * @param delay
	 *            延迟时间
	 * @return void 返回类型
	 * @throws
	 */
	public static void showInputMethodDelay(final Context context, long delay) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				InputMethodManager imm = ServiceManager
						.getInputMethodManager(context);
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}, delay);
	}

	/**
	 * @Title : showInputMethod
	 * @Description : 打开软键盘
	 * @params
	 * @param context
	 *            设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public static void showInputMethod(final Context context) {
		InputMethodManager imm = ServiceManager.getInputMethodManager(context);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * @Title : hiddenInputMethod
	 * @Description : 关闭软键盘
	 * @params
	 * @param context
	 * @param view
	 *            设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public static void hiddenInputMethod(final Context context, View view) {
		InputMethodManager imm = ServiceManager.getInputMethodManager(context);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	/**
	 * @Title : hideBottomSoftInputMethod
	 * @Description : 设置输入框漂浮在软键盘之上
	 * @params
	 * @param context
	 *            设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public static void hideBottomSoftInputMethod(Context context) {
		((Activity) context).getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
	}

}
