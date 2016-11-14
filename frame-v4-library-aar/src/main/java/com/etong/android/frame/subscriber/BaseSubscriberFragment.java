package com.etong.android.frame.subscriber;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.etong.android.frame.BaseApplication;
import com.etong.android.frame.common.LoadingDialog;
import com.etong.android.frame.permissions.PermissionsManager;
import com.etong.android.frame.utils.CustomToast;

import org.simple.eventbus.EventBus;

/**
 * @ClassName : BaseSubscriberFragment
 * @Description : Fragment抽象基类</br> 初始化EventBus,LoadingDialog，自定义消息显示等
 * @author : zhouxiqing
 * @date : 2016-3-21 下午3:32:31
 */
public abstract class BaseSubscriberFragment extends Fragment {

	protected EventBus mEventBus = EventBus.getDefault();
	protected String mClassName;
	private LoadingDialog mLoading = null;

	private OnClickListener mClickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			BaseSubscriberFragment.this.onClick(arg0);
		}
	};

	/**
	 * @Title : getEventBus
	 * @Description : 获取EventBus实例
	 * @return EventBus EventBus实例
	 */
	protected EventBus getEventBus() {
		return mEventBus;
	}

	protected void onClick(View arg0) {
	}

	/**
	 * @Title : onInit
	 * @Description : 初始化
	 * @params
	 * @param inflater
	 * @param container
	 * @param savedInstanceState
	 * @return 设定文件
	 * @return View 返回类型
	 * @throws
	 */
	protected abstract View onInit(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mEventBus.register(this);
		super.onCreate(savedInstanceState);
		mLoading = new LoadingDialog(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return onInit(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		mEventBus.unregister(this);
		super.onDestroy();
		mLoading.dismiss();
	}

	/**
	 * @Title : addClickListener
	 * @Description : 添加点击事件
	 * @params
	 * @param view
	 *            View
	 * @return void 返回类型
	 */
	protected void addClickListener(View view) {
		if (null != view)
			view.setOnClickListener(mClickListener);
	}

	/**
	 * @Title : addClickListener
	 * @Description : 添加点击事件
	 * @params
	 * @param parent
	 *            View
	 * @param id
	 *            id
	 * @return 设定文件
	 * @return View 返回类型
	 */
	protected View addClickListener(View parent, int id) {
		View view = parent.findViewById(id);
		addClickListener(view);
		return view;
	}

	/**
	 * @Title : addClickListener
	 * @Description : 添加点击事件
	 * @params
	 * @param parent
	 *            View
	 * @param views
	 *            View's
	 * @return void 返回类型
	 */
	protected void addClickListener(View parent, View[] views) {
		for (View view : views) {
			addClickListener(view);
		}
	}

	/**
	 * @Title : findViewById
	 * @Description : 在指定View中查找子View
	 * @params
	 * @param parent
	 * @param viewId
	 * @param clazz
	 * @return 设定文件
	 * @return T 返回类型
	 */
	@SuppressWarnings("unchecked")
	protected <T> T findViewById(View parent, int viewId, Class<T> clazz) {
		return (T) parent.findViewById(viewId);
	}

	/**
	 * @Title : findViewById
	 * @Description : 在activity中查找view
	 * @params
	 * @param viewId
	 * @param clazz
	 * @return 设定文件
	 * @return T 返回类型
	 */
	@SuppressWarnings("unchecked")
	protected <T> T findViewById(int viewId, Class<T> clazz) {
		return (T) getActivity().findViewById(viewId);
	}

	/**
	 * @Title : loadStart
	 * @Description : 显示加载开始的dialog
	 * @params 设定文件
	 * @return void 返回类型
	 */
	protected void loadStart() {
		mLoading.show();
	}

	/**
	 * @Title : loadStart
	 * @Description : 显示加载Dialog
	 * @params
	 * @param tip
	 *            显示的标题(null为使用默认标题)
	 * @param time
	 *            显示倒计时时间(秒)(0为不显示倒计时)
	 * @return void 返回类型
	 */
	protected void loadStart(String tip, int time) {
		mLoading.setTip(tip, time);
		mLoading.show();
	}

	/**
	 * @Title : loadFinish
	 * @Description : 隐藏加载Dialog
	 * @params 设定文件
	 * @return void 返回类型
	 */
	protected void loadFinish() {
		mLoading.hide();
	}

	/**
	 * @Title : toastMsg
	 * @Description : 自定义消息显示
	 * @params
	 * @param msg
	 *            消息内容
	 * @return void 返回类型
	 */
	protected void toastMsg(final String msg) {
		CustomToast.showToast(this.getContext(),msg, CustomToast.LENGTH_SHORT);
	}

	/**
	 * @Title : toastMsg
	 * @Description : 自定义消息显示
	 * @params
	 * @param msg
	 *            消息内容
	 * @param errno
	 *            错误内容，当{@link BaseApplication#isDebug}为true时会显示在消息中
	 * @return void 返回类型
	 */
	protected void toastMsg(String msg, String errno) {
		if (BaseApplication.isDebug)
			toastMsg(msg + ":" + errno);
		else
			toastMsg(msg);
	}

	/**
	 * @param err 错误代码，当{@link BaseApplication#isDebug}为true时会显示在消息中
	 * @param msg 消息内容
	 * @return void 返回类型
	 * @Title : toastMsg
	 * @Description : 自定义消息显示
	 * @params
	 */
	protected void toastMsg(int err, String msg) {
		if (BaseApplication.isDebug)
			toastMsg(msg + "(" + err + ")");
		else
			toastMsg(msg);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
			@NonNull String[] permissions, @NonNull int[] grantResults) {
		PermissionsManager.getInstance().notifyPermissionsChange(permissions,
				grantResults);
	}
}
