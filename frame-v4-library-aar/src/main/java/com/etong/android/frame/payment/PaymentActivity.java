package com.etong.android.frame.payment;

import org.simple.eventbus.Subscriber;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.alipay.sdk.app.PayTask;
import com.etong.android.frame.event.CommonEvent;
import com.etong.android.frame.publisher.HttpMethod;
import com.etong.android.frame.publisher.HttpPublisher;
import com.etong.android.frame.subscriber.BaseSubscriberActivity;
import com.etong.android.frame.utils.logger.Logger;
import com.pgyersdk.crash.PgyCrashManager;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.unionpay.UPPayAssistEx;
import com.unionpay.uppay.PayActivity;

/**
 * @ClassName : PaymentActivity
 * @Description 支付的基类<br>
 *              当需要使用微信支付时必须先调用{@link #initWXAPI(String WECHAT_APP_ID)}初始化微信API <br>
 *              并在PackageName.wxapi下添加名为WXPayEntryActivity的Activity, 继承
 *              {@link WXPaymentEntryActivity}
 * @author : zhouxiqing
 * @date : 2015-12-22 上午11:37:52
 */
public abstract class PaymentActivity extends BaseSubscriberActivity {
	private static final String TAG = "PaymentActivity";

	private PaymentProvider mPayProvider;
	private PayInfo payInfo = null;// 支付信息
	private IWXAPI api;// 微信支付
	private long mPayTime = 0;

	@Override
	protected void onInit(Bundle savedInstanceState) {
		mPayProvider = PaymentProvider.getInstance();
		mPayProvider.initialize(HttpPublisher.getInstance(),
				getApplicationContext());
		// 初始化
		initWXAPI();
		onInit();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}

	/**
	 * @Title : initWXAPI
	 * @Description : 初始化微信API
	 * @params
	 * @param WECHAT_APP_ID
	 *            微信APP ID
	 * @return void 返回类型
	 */
	@Deprecated
	public void initWXAPI(String WECHAT_APP_ID) {
		api = WXAPIFactory.createWXAPI(getApplicationContext(), WECHAT_APP_ID,
				true);
		// 将该app注册到微信
		Boolean reg = api.registerApp(WECHAT_APP_ID);
		if (reg) {
			System.out.println("WXAPI register success");
		} else {
			System.out.println("WXAPI register fail");
		}
	}

	protected void initWXAPI() {
		try {
			ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(),
					PackageManager.GET_META_DATA);
			String WECHAT_APP_ID = appInfo.metaData.getString("WECHAT_APPID");
			if (!TextUtils.isEmpty(WECHAT_APP_ID)) {
				api = WXAPIFactory.createWXAPI(getApplicationContext(), WECHAT_APP_ID,
						true);
				// 将该app注册到微信
				Boolean reg = api.registerApp(WECHAT_APP_ID);
				if (reg) {
					System.out.println("WXAPI register success");
				} else {
					System.out.println("WXAPI register fail");
				}
			}
		} catch (PackageManager.NameNotFoundException e) {
			PgyCrashManager.reportCaughtException(this,e);
			e.printStackTrace();
		}
	}

	/**
	 * @Title : setPayInfo
	 * @Description : 调用支付
	 * @params
	 * @param info
	 *            支付信息
	 * @return void 返回类型
	 */
	protected void setPayInfo(PayInfo info) {
		if (!isWXAppInstalledAndSupported() && info.getType() == 0) {
			toastMsg("目前您的微信版本过低或未安装微信，需要安装微信才能使用");
			return;
		}
		if ((System.currentTimeMillis() - mPayTime) > 2000) {
			mPayTime = System.currentTimeMillis();
			payInfo = info;
			mPayProvider.payMent(payInfo);
			loadStart("支付中,请稍后...", 0);
		}
	}

	@Subscriber(tag = CommonEvent.UNION_PAY)
	private void onUnionPayFinish(HttpMethod method) {
		loadFinish();
		String tn = null;
		JSONObject obj = method.data().getJSONObject("entity");
		if (obj != null) {
			payInfo.setPaycode(obj.getString("orderId"));
			tn = obj.getString("tn");
		}
		if (tn != null && !TextUtils.isEmpty(tn))
			UPPayAssistEx.startPayByJAR(this, PayActivity.class, null, null,
					tn, PaymentProvider.PAYMENT_MODE_CODE);
		else
			toastMsg(method.data().getIntValue("errCode"), method.data()
					.getString("errName"));
	}

	@Subscriber(tag = CommonEvent.ALI_PAY)
	private void onAliPayFinish(HttpMethod method) {
		loadFinish();
		JSONObject obj = method.data().getJSONObject("entity");
		if (obj == null) {
			toastMsg("支付失败,请重试!");
			return;
		}
		payInfo.setPaycode(obj.getString("orderId"));
		final String tn = obj.getString("payInfo");
		if (tn != null && !TextUtils.isEmpty(tn)) {
			Runnable payRunnable = new Runnable() {
				@Override
				public void run() {
					// 构造PayTask 对象
					PayTask alipay = new PayTask(PaymentActivity.this);
					// 调用支付接口，获取支付结果
					String result = alipay.pay(tn);
					// 调用支付结果处理方法
					getEventBus().post(result, TAG);
				}
			};
			// 必须异步调用
			Thread payThread = new Thread(payRunnable);
			payThread.start();
		} else
			toastMsg(method.data().getIntValue("errCode"), method.data()
					.getString("errMsg"));
	}

	@Subscriber(tag = CommonEvent.WECHAT_PAY)
	private void onWeChatPayFinish(HttpMethod method) {
		JSONObject obj = method.data().getJSONObject("entity");
		if (obj == null) {
			toastMsg("支付失败,请重试!");
			loadFinish();
			return;
		}
		payInfo.setPaycode(obj.getString("orderId"));
		final JSONObject json = obj.getJSONObject("payInfo");
		if (json != null) {
			PayReq req = new PayReq();
			req.appId = json.getString("appid");// 公众账号ID
			req.partnerId = json.getString("mch_id");// 商户号
			req.prepayId = json.getString("prepay_id");// 预支付交易会话ID
			req.nonceStr = json.getString("nonce_str");// 随机字符串
			req.timeStamp = json.getString("timestamp");// 时间戳
			req.packageValue = json.getString("package");// 扩展字段
			req.sign = json.getString("sign1");// 签名
			req.extData = "app data"; // optional
			Boolean send = api.sendReq(req);
			if (send) {
				System.out.println("WeChat Pay send success!");
			} else {
				System.out.println("WeChat Pay send fail!");
				toastMsg("支付失败");
			}
		} else {
			toastMsg(method.data().getIntValue("errCode"), method.data()
					.getString("errMsg"));
		}
		loadFinish();
	}

	/**
	 * 银联支付返回结果处理
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null) {
			String str = data.getExtras().getString("pay_result");
			if (str != null) {
				if (str.equalsIgnoreCase("success")) {
					// 调用支付成功后的处理方法
					System.out.println("银联支付:支付成功");
					getEventBus().post(payInfo, CommonEvent.PAY_SUCCESS);
					paySuccess(payInfo, 0, "支付成功");
				} else if (str.equalsIgnoreCase("fail")) {
					System.out.println("银联支付:支付失败");
					payFail(payInfo, -2, "支付失败!");
					return;
				} else if (str.equalsIgnoreCase("cancel")) {
					System.out.println("银联支付:支付取消");
					payCancle(payInfo, -1, "支付取消");
					return;
				}
			}
		}
	}

	/**
	 * @Title : onAlipayResult
	 * @Description : 支付宝支付返回结果处理
	 * @params
	 * @param result
	 *            返回结果
	 * @return void 返回类型
	 */
	@Subscriber(tag = TAG)
	private void onAlipayResult(String result) {
		AlipayResult payResult = new AlipayResult(result);
		// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
		String resultStatus = payResult.getResultStatus();

		Integer resultInt = Integer.parseInt(resultStatus);

		switch (resultInt) {
		// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
		case 9000:
			// 调用支付成功后的处理方法
			System.out.println("支付宝支付:支付成功");
			getEventBus().post(payInfo, CommonEvent.PAY_SUCCESS);
			paySuccess(payInfo, 9000, "支付成功");
			break;
		case 8000:
			System.out.println("支付宝支付:正在处理,支付结果以后台异步回调为准");
			payFail(payInfo, 8000, "正在处理,支付结果以后台异步回调为准");
			break;
		case 4000:
			System.out.println("支付宝支付:系统繁忙，请稍后再试");
			payFail(payInfo, 4000, "系统繁忙，请稍后再试");
			break;
		case 6001:
			System.out.println("支付宝支付:支付取消");
			payCancle(payInfo, 6001, "支付取消");
			break;
		case 6002:
			System.out.println("支付宝支付:网络连接出错,请稍后重试!");
			payFail(payInfo, 6002, "网络连接出错,请稍后重试!");
			break;
		}
	}

	/** 微信支付回调 */
	@Subscriber
	public void onResp(BaseResp resp) {
		System.out.println("WeChat onPayFinish, errCode = " + resp.errCode);
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			switch (resp.errCode) {
			case BaseResp.ErrCode.ERR_OK:// 支付成功
				// 调用支付成功后的处理方法
				System.out.println("微信支付:支付成功");
				getEventBus().post(payInfo, CommonEvent.PAY_SUCCESS);
				paySuccess(payInfo, BaseResp.ErrCode.ERR_OK, "支付成功");
				break;
			case BaseResp.ErrCode.ERR_COMM:// 一般错误
				// 可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
				toastMsg("支付失败,请重试!", "errCode=" + resp.errCode + ";errStr="
						+ resp.errStr);
				System.out.println("微信支付:支付失败");
				payFail(payInfo, BaseResp.ErrCode.ERR_COMM, "支付失败!");
				break;
			case BaseResp.ErrCode.ERR_USER_CANCEL:// 用户取消
				System.out.println("微信支付:支付取消");
				payCancle(payInfo, BaseResp.ErrCode.ERR_USER_CANCEL, "支付取消");
				break;
			// case BaseResp.ErrCode.ERR_SENT_FAILED:
			// toastMsg("发送失败");
			// break;
			// case BaseResp.ErrCode.ERR_AUTH_DENIED:
			// toastMsg("认证被否决");
			// break;
			// case BaseResp.ErrCode.ERR_UNSUPPORT:
			// toastMsg("不支持错误");
			// break;
			default:
				break;
			}
		}
	}

	@Subscriber
	public void onReq(BaseReq arg0) {
	}

	/**
	 * @Title : isWXAppInstalledAndSupported
	 * @Description : 判断是否存在微信客户端及微信客户端是否支持支付功能
	 * @params
	 * @return 设定文件
	 * @return boolean 返回类型
	 */
	public boolean isWXAppInstalledAndSupported() {
		boolean sIsWXAppInstalledAndSupported = api.isWXAppInstalled()
				&& api.isWXAppSupportAPI();

		return sIsWXAppInstalledAndSupported;
	}

	/**
	 * @Title : onInit
	 * @Description : 初始化，子类可在该函数中对界面进行初始化
	 * @params 设定文件
	 * @return void 返回类型
	 */
	abstract protected void onInit();

	/**
	 * @Title : paySuccess
	 * @Description : 支付成功,子类可在该函数中对支付成功结果进行处理
	 * @params
	 * @param PayInfo
	 *            支付信息
	 * @param errorCode
	 *            返回代码
	 * @param msg
	 *            返回消息
	 * @return void 返回类型
	 */
	abstract protected void paySuccess(PayInfo info, int errorCode, String msg);

	/**
	 * @Title : payCancle
	 * @Description : 支付取消,子类可在该函数中对支付取消结果进行处理
	 * @params
	 * @param PayInfo
	 *            支付信息
	 * @param errorCode
	 *            返回代码
	 * @param msg
	 *            返回消息
	 * @return void 返回类型
	 */
	abstract protected void payCancle(PayInfo info, int errorCode, String msg);

	/**
	 * @Title : payFail
	 * @Description : 支付失败,子类可在该函数中对支付失败结果进行处理
	 * @params
	 * @param PayInfo
	 *            支付信息
	 * @param errorCode
	 *            返回代码
	 * @param msg
	 *            返回消息
	 * @return void 返回类型
	 */
	abstract protected void payFail(PayInfo info, int errorCode, String msg);
}
