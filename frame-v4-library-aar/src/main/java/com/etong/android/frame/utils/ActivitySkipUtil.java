package com.etong.android.frame.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * @ClassName : ActivitySkipUtil
 * @Description : Activity跳转工具类
 * @author : zhouxiqing
 * @date : 2016-3-21 下午4:02:11
 * 
 */
public class ActivitySkipUtil {

	/**
	 * @Title : skipActivity
	 * @Description : Activity跳转
	 * @params
	 * @param activity
	 *            当前
	 * @param cla
	 *            跳转
	 * @return void 返回类型
	 */
	public static void skipActivity(Activity activity, Class<?> cla) {
		if (activity != null) {
			Intent intent = new Intent();
			intent.setClass(activity, cla);
			activity.startActivity(intent);
		}
	}

	/**
	 * @Title : skipActivity
	 * @Description : Activity跳转
	 * @params
	 * @param fragment
	 *            当前
	 * @param cla
	 *            跳转
	 * @return void 返回类型
	 */
	public static void skipActivity(Fragment fragment, Class<?> cla) {
		if (fragment != null) {
			Intent intent = new Intent();
			intent.setClass(fragment.getActivity(), cla);
			fragment.startActivity(intent);
		}
	}

	/**
	 * @Title : skipActivity
	 * @Description : Activity跳转
	 * @params
	 * @param activity
	 *            当前
	 * @param cla
	 *            跳转
	 * @param data
	 *            数据
	 * @return void 返回类型
	 */
	public static void skipActivity(Activity activity, Class<?> cla, Bundle data) {
		if (activity != null) {
			Intent intent = new Intent();
			intent.setClass(activity, cla);
			intent.putExtras(data);
			activity.startActivity(intent);
		}
	}

	/**
	 * @Title : skipActivity
	 * @Description : Activity跳转
	 * @params
	 * @param fragment
	 *            当前
	 * @param cla
	 *            跳转
	 * @param data
	 *            数据
	 * @return void 返回类型
	 */
	public static void skipActivity(Fragment fragment, Class<?> cla, Bundle data) {
		if (fragment != null) {
			Intent intent = new Intent();
			intent.setClass(fragment.getActivity(), cla);
			intent.putExtras(data);
			fragment.startActivity(intent);
		}
	}

	/**
	 * @Title : skipActivityForResult
	 * @Description : Activity跳转
	 * @params
	 * @param activity
	 *            当前
	 * @param cla
	 *            跳转
	 * @param requestCode
	 *            返回值接收标记
	 * @param data
	 *            数据
	 * @return void 返回类型
	 */
	public static void skipActivityForResult(Activity activity, Class<?> cla,
			int requestCode, Bundle data) {
		if (activity != null) {
			Intent intent = new Intent();
			intent.setClass(activity, cla);
			if (data != null)
				intent.putExtras(data);
			activity.startActivityForResult(intent, requestCode);
		}
	}

	/**
	 * @Title : skipActivityForResult
	 * @Description : Activity跳转
	 * @params
	 * @param fragment
	 *            当前
	 * @param cla
	 *            跳转
	 * @param requestCode
	 *            返回值接收标记
	 * @param data
	 *            数据
	 * @return void 返回类型
	 */
	public static void skipActivityForResult(Fragment fragment, Class<?> cla,
			int requestCode, Bundle data) {
		if (fragment != null) {
			Intent intent = new Intent();
			intent.setClass(fragment.getActivity(), cla);
			if (data != null)
				intent.putExtras(data);
			fragment.startActivityForResult(intent, requestCode);
		}
	}
}
