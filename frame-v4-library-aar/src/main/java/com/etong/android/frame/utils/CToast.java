package com.etong.android.frame.utils;

import android.content.Context;
import android.widget.Toast;

import com.etong.android.frame.BaseApplication;

/**
 * @ClassName : CToast
 * @Description : 自定义时长的Toast，已废除，请使用{@link CustomToast}
 * @author : zhouxiqing
 * @date : 2015-11-11 下午2:48:57
 */
@Deprecated
public class CToast {
	private static CToast mCToast;

	@Deprecated
	public CToast(Context application) {
	}

	/**
	 * @Title : getInstance
	 * @Description : 获取自定义Toast实例
	 * @return CToast 返回类型
	 */
	@Deprecated
	public static CToast getInstance() {
		if (mCToast == null) {
			mCToast = new CToast(BaseApplication.getApplication());
		}
		return mCToast;
	}

	/**
	 * 自定义Toast
	 * 
	 * @param message
	 *            :消息字符串
	 * @param T
	 *            :消息显示时间 0:Toast.LENGTH_SHORT; 1 Toast.LENGTH_LONG;其它值为自定义时长
	 */
	@Deprecated
	public static void toastMessage(String message, int T) {
		CustomToast.showToast(BaseApplication.getApplication(), message,T==0? Toast.LENGTH_SHORT:Toast.LENGTH_LONG);
	}
}
