package com.etong.android.frame.share;

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.simple.eventbus.EventBus;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.TextUtils;

import com.etong.android.frame.BaseApplication;
import com.etong.android.frame.utils.CToast;
import com.etong.android.frame.utils.logger.Logger;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * @ClassName : ShareProvider
 * @Description : 微信分享
 *              <p>
 *              调用{@link #getInstance()} 方法获得工具实例</br> 调用
 *              {@link #initialize(Context, String)}初始化微信API</br> 调用
 *              {@link #SendTextToWX(ShareInfo, boolean, String)} 方法进行文本分享</br>
 *              调用{@link #SendImageToWX(ShareInfo, boolean, String)}
 *              方法进行图片分享</br> 调用{@link #SendWebToWX(ShareInfo, boolean, String)}
 *              方法进行网页分享</br> 在PackageName.wxapi下添加名为WXEntryActivity的Activity,
 *              继承 {@link WXShareEntryActivity}进行分享结果处理
 * @author : zhouxiqing
 * @date : 2016-3-16 上午10:56:57
 * 
 */
public class ShareProvider {
	protected EventBus mEventBus = EventBus.getDefault();

	// 初始化微信支付
	public static IWXAPI api = null;
	private static ShareInfo info;
	private boolean isWXAppInstalled = false;

	private static class Holder {
		private static final ShareProvider INSTANCE =  new ShareProvider();
		private static final IWXAPI api = WXAPIFactory.createWXAPI(BaseApplication.getApplication()
				.getApplicationContext(), null);
	}
	
	private ShareProvider() {
		
	}

	public static ShareProvider getInstance() {
		api = Holder.api;
		return Holder.INSTANCE;
	}

	/**
	 * @Title : initialize
	 * @Description : 初始化微信分享
	 * @params
	 * @param WECHAT_APP_ID
	 *            微信APP ID
	 * @return void 返回类型
	 */
	public void initialize(Context context, String WECHAT_APP_ID) {
		api = WXAPIFactory.createWXAPI(context, WECHAT_APP_ID, true);
		// 将该app注册到微信
		Boolean reg = api.registerApp(WECHAT_APP_ID);
		if (reg) {
			System.out.println("WXAPI register success");
		} else {
			System.out.println("WXAPI register fail");
		}

		isWXAppInstalled = api.isWXAppInstalled();
	}

	public ShareInfo getInfo() {
		return info;
	}

	/**
	 * @Title : SendTextToWX
	 * @Description : 分享文本到微信
	 * @params
	 * @param info
	 *            分享内容</br>文本（{@link ShareInfo#setText(String)}  必选）
	 * @param type
	 *            true:发送到朋友圈 ;false:发送到聊天界面
	 * @param tag
	 *            请求标识
	 * @return boolean</br>true:请求已发送</br>false:请求发送失败
	 */
	public boolean SendTextToWX(ShareInfo info, boolean type, String tag) {
		ShareProvider.info = info;
		if (TextUtils.isEmpty(info.text)) {
			return false;
		}

		// 初始化一个WXTextObject对象
		WXTextObject textObj = new WXTextObject();
		textObj.text = info.text;

		// 用WXTextObject对象初始化一个WXMediaMessage对象
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = textObj;
		// 发送文本类型的消息时，title字段不起作用
		msg.description = info.text;

		// 构造一个Req
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("text"); // transaction字段用于唯一标识一个请求
		req.message = msg;
		req.scene = type ? SendMessageToWX.Req.WXSceneTimeline
				: SendMessageToWX.Req.WXSceneSession;
		req.openId = tag;

		if (!isWXAppInstalled) {
			CToast.toastMessage("目前您的微信版本过低或未安装微信，需要安装微信才能使用", 0);
			return false;
		}
		// 调用api接口发送数据到微信
		if (!api.sendReq(req)) {
			CToast.toastMessage("分享失败！", 0);
			Logger.t(tag).e("IWXAPI sendReq fail ");
			return false;
		} else {
			return true;
		}
	}

	/**
	 * @Title : SendImageToWX
	 * @Description : 分享图片到微信
	 * @params
	 * @param info
	 *            分享内容</br>图片（{@link ShareInfo#setImageBitmap(Bitmap)} /
	 *            {@link ShareInfo#setImagePath(String)}  必选），图片地址（
	 *            {@link ShareInfo#setImageUrl(String)} 可选)
	 * @param type
	 *            true:发送到朋友圈 ;false:发送到聊天界面
	 * @param tag
	 *            请求标识
	 * @return boolean</br>true:请求已发送</br>false:请求发送失败
	 */
	public boolean SendImageToWX(ShareInfo info, boolean type, String tag) {
		ShareProvider.info = info;

		WXImageObject imgObj = null;
		if (!TextUtils.isEmpty(info.imageUrl)) {// 传入图片地址
			imgObj = new WXImageObject();
			imgObj.imageUrl = info.imageUrl;
		} else if (!TextUtils.isEmpty(info.imagePath)) {// 传入图片路径
			imgObj = new WXImageObject();
			imgObj.setImagePath(info.imagePath);
		} else if (info.imageBitmap != null) {// 传入Bitmap图片
			imgObj = new WXImageObject(info.imageBitmap);
		} else {
			CToast.toastMessage("微信分享失败!", 0);
			Logger.i( "微信分享失败,参数异常！");
			return false;
		}

		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = imgObj;

		msg.thumbData = getByteArray(info);
		if (msg.thumbData == null) {
			return false;
		}

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("img");
		req.message = msg;
		req.scene = type ? SendMessageToWX.Req.WXSceneTimeline
				: SendMessageToWX.Req.WXSceneSession;
		req.openId = tag;

		if (!isWXAppInstalled) {
			CToast.toastMessage("目前您的微信版本过低或未安装微信，需要安装微信才能使用", 0);
			return false;
		}
		// 调用api接口发送数据到微信
		if (!api.sendReq(req)) {
			CToast.toastMessage("发送失败！", 0);
			Logger.t(tag).e("IWXAPI sendReq fail ");
			return false;
		} else {
			return true;
		}
	}

	/**
	 * @Title : SendWebToWX
	 * @Description : 分享网页到微信
	 * @params
	 * @param info
	 *            分享内容</br>标题（{@link ShareInfo#setTitle(String)} 必选），文本（
	 *            {@link ShareInfo#setText(String)} 必选），网址（{@link ShareInfo#setUrl(String)} 必选），图片（
	 *            {@link ShareInfo#setImageBitmap(Bitmap)}/{@link ShareInfo#setImagePath(String)} 可选）
	 * @param type
	 *            true:发送到朋友圈 ;false:发送到聊天界面
	 * @param tag
	 *            请求标识
	 * @return boolean</br>true:请求已发送</br>false:请求发送失败
	 */
	public boolean SendWebToWX(ShareInfo info, boolean type, String tag) {
		ShareProvider.info = info;
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = info.getUrl();
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = info.title;
		msg.description = info.text;

		// 设置图片
		msg.thumbData = getByteArray(info);
		if (msg.thumbData == null) {
			return false;
		}

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = buildTransaction("webpage");
		req.message = msg;
		req.scene = type ? SendMessageToWX.Req.WXSceneTimeline
				: SendMessageToWX.Req.WXSceneSession;
		req.openId = tag;

		if (!isWXAppInstalled) {
			CToast.toastMessage("目前您的微信版本过低或未安装微信，需要安装微信才能使用", 0);
			return false;
		}
		// 调用api接口发送数据到微信
		if (!api.sendReq(req)) {
			CToast.toastMessage("发送失败！", 0);
			Logger.t(tag).e("IWXAPI sendReq fail ");
			return false;
		} else {
			return true;
		}
	}

	@SuppressLint("NewApi")
	private byte[] getByteArray(ShareInfo info) {
		Bitmap bmp = null;
		if (info.imageBitmap != null) {// 传入Bitmap图片
			bmp = info.getImageBitmap();
		} else if (!TextUtils.isEmpty(info.imagePath)) {// 传入图片路径
			File file = new File(info.getImagePath());
			if (!file.exists()) {
				CToast.toastMessage("微信分享失败!", 0);
				return null;
			}
			bmp = BitmapFactory.decodeFile(info.imagePath);
		} else {
			CToast.toastMessage("微信分享失败!", 0);
			Logger.i("微信分享失败,参数异常！");
			return null;
		}

		Bitmap map = Bitmap.createScaledBitmap(bmp, 150, 150, true);

		if (!chechSize(map)) {
			map = Bitmap.createScaledBitmap(bmp, 100, 100, true);
		}

		if (!chechSize(map)) {
			map = Bitmap.createScaledBitmap(bmp, 50, 50, true);
		}

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		map.compress(CompressFormat.PNG, 100, output);
		map.recycle();

		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis())
				: type + System.currentTimeMillis();
	}

	private long maxSize = 32768;// 缩略图最大值，32K

	/**
	 * @Title : chechSize
	 * @Description : 检查缩略图是否小于32k
	 * @params
	 * @param map
	 * @return boolean </br>true:缩略图小于32k</br>false:缩略图大于32k或为null
	 */
	@SuppressLint("NewApi")
	private boolean chechSize(Bitmap map) {
		if (map == null) {
			return false;
		}

		long mapSize;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
			mapSize = map.getByteCount();
		} else {
			mapSize = map.getRowBytes() * map.getHeight();
		}
		if (mapSize > maxSize) {
			return false;
		} else {
			return true;
		}
	}

	// /**
	// * @Title: shareWechat
	// * @Description: 分享到微信好友
	// * <p>
	// * @param info
	// * 分享内容
	// * @param tag
	// * 需要回调的标签
	// */
	// public void shareWechat(ShareInfo info, String tag) {
	// ShareProvider.tag = tag;
	// ShareProvider.info = info;
	// // 初始化EventBus
	// mEventBus.register(this);
	// ShareParams sp = new ShareParams();
	// // 设置分享类型
	// sp.setShareType(Platform.SHARE_WEBPAGE);
	// // 设置分享标题及文本
	// sp.setTitle("微信分享");
	// if (info.title != null && !TextUtils.isEmpty(info.title))
	// sp.setTitle(info.title);
	// if (info.text != null && !TextUtils.isEmpty(info.text))
	// sp.setText(info.text);
	// // 设置图片
	// if (info.imageData != null)
	// sp.setImageData(info.imageData);
	// else if (info.imagePath != null && !TextUtils.isEmpty(info.imagePath))
	// sp.setImagePath(info.imagePath);
	// else if (info.imageUrl != null && !TextUtils.isEmpty(info.imageUrl))
	// sp.setImageUrl(info.imageUrl);
	// // 设置视频地址
	// if (info.url != null && !TextUtils.isEmpty(info.url))
	// sp.setUrl(info.url);
	// else {
	// CToast.toastMessage("分享失败,请重试!", 0);
	// return;
	// }
	//
	// Platform wecat = ShareSDK.getPlatform(WechatMoments.NAME);// 分享到微信朋友圈
	// wecat.setPlatformActionListener(pListener); // 设置分享事件回调
	// wecat.share(sp);// 执行分享
	// }
	//
	// static PlatformActionListener pListener = new PlatformActionListener() {
	//
	// @Override
	// public void onError(Platform arg0, int arg1, Throwable arg2) {
	// // 操作失败的处理代码
	// EventBus.getDefault().post(arg2, SHARE_FAIL);
	// info.setState(SHARE_FAIL);
	// EventBus.getDefault().post(info, tag);
	// }
	//
	// @Override
	// public void onComplete(Platform arg0, int arg1,
	// HashMap<String, Object> arg2) {
	// // 操作成功的处理代码
	// EventBus.getDefault().post(arg0, SHARE_SUCCESS);
	// info.setState(SHARE_SUCCESS);
	// EventBus.getDefault().post(info, tag);
	// }
	//
	// @Override
	// public void onCancel(Platform arg0, int arg1) {
	// // 操作取消的处理代码
	// EventBus.getDefault().post(arg0, SHARE_CANCEL);
	// info.setState(SHARE_CANCEL);
	// EventBus.getDefault().post(info, tag);
	// }
	// };
	//
	// @Subscriber(tag = SHARE_FAIL)
	// private void onShareError(Throwable arg0) {
	// // 微信版本过低或未安装
	// if (arg0 instanceof WechatClientNotExistException) {
	// CToast.toastMessage("目前您的微信版本过低或未安装微信，需要安装微信才能使用", 0);
	// } // 微信版本过低或未安装
	// else if (arg0 instanceof WechatTimelineNotSupportedException) {
	// CToast.toastMessage("目前您的微信版本过低或未安装微信，需要安装微信才能使用", 0);
	// } else
	// CToast.toastMessage("分享失败", 0);
	// }
	//
	// @Subscriber(tag = SHARE_SUCCESS)
	// private void onShareComplete(Platform arg0) {
	// CToast.toastMessage("分享成功!", 0);
	// }
	//
	// @Subscriber(tag = SHARE_CANCEL)
	// private void onShareCancel(Platform arg0) {
	// CToast.toastMessage("分享已取消!", 0);
	// }
	//
	// @Override
	// protected void finalize() throws Throwable {
	// super.finalize();
	// mEventBus.unregister(this);
	// }
}