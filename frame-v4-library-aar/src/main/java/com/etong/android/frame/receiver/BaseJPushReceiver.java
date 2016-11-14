package com.etong.android.frame.receiver;

import org.simple.eventbus.EventBus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import cn.jpush.android.api.JPushInterface;

import com.etong.android.frame.utils.logger.Logger;

/**
 * @ClassName : BaseJPushReceiver
 * @Description : 自定义极光推送消息接收器<br>
 *              如果不定义这个 Receiver，则： 1) 默认用户会打开主界面 2) 接收不到自定义消息<br>
 *              实现方法<br>
 *              {@link #handleCustomMessage(Context, Bundle)} 处理自定义消息<br>
 *              {@link #handNotification(Context, Bundle)} 处理通知<br>
 *              {@link #openNotification(Context, Bundle)} 处理通知点击打开后的操作<br>
 *              {@link #openRichPush(Context, Bundle)} 处理富文本消息点击打开后的操作<br>
 * @author : zhouxiqing
 * @date : 2016-3-4 下午2:16:39
 * 
 */
abstract public class BaseJPushReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		EventBus.getDefault().register(context);
		Bundle bundle = intent.getExtras();
		Logger.d("onReceive - " + intent.getAction() + ", extras: "
				+ printBundle(bundle));

		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
			saveExtraRegistrationId(bundle);
		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent
				.getAction())) {
			// 在这里处理自定义消息
			handleCustomMessage(context, bundle);
		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent
				.getAction())) {
			// 在这里处理通知
			handNotification(context, bundle);
		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent
				.getAction())) {
			// 在这里添加通知打开后的操作
			Logger.d("用户点击打开了通知");
			openNotification(context, bundle);
		} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent
				.getAction())) {
			String extraExtra = bundle.getString(JPushInterface.EXTRA_EXTRA);
			Logger.d("用户收到到RICH PUSH: " + extraExtra);
			// 在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity，
			// 打开一个网页等..
			openRichPush(context,bundle);
		} else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent
				.getAction())) {
			boolean connected = intent.getBooleanExtra(
					JPushInterface.EXTRA_CONNECTION_CHANGE, false);
			Logger.w("[MyReceiver]" + intent.getAction()
					+ " connected state change to " + connected);
		} else {
			Logger.d("Unhandled intent - " + intent.getAction());
		}
		EventBus.getDefault().unregister(context);
	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			} else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}

	/**
	 * @Title : handleCustomMessage
	 * @Description : 处理自定义消息
	 * @params
	 * @param context
	 * @param bundle
	 *            推送数据
	 * @return void 返回类型
	 */
	abstract public void handleCustomMessage(Context context, Bundle bundle);

	/**
	 * @Title : handNotification
	 * @Description : 处理通知
	 * @params
	 * @param context
	 * @param bundle
	 *            推送数据
	 * @return void 返回类型
	 */
	abstract public void handNotification(Context context, Bundle bundle);

	/**
	 * @Title : openNotification
	 * @Description : 通知点击后的操作
	 * @params
	 * @param context
	 * @param bundle
	 *            推送数据
	 * @return void 返回类型
	 */
	abstract public void openNotification(Context context, Bundle bundle);

	/**
	 * @Title : openRichPush
	 * @Description : 富文本消息点击后的操作
	 * @params
	 * @param context
	 * @param bundle
	 *            推送数据
	 * @return void 返回类型
	 */
	abstract public void openRichPush(Context context, Bundle bundle);

	private void saveExtraRegistrationId(Bundle bundle) {
		String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
		Logger.d("接收Registration Id : " + regId);
	}
}
