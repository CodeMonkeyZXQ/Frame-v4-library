package com.etong.android.frame.payment;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.etong.android.frame.common.BaseHttpUri;
import com.etong.android.frame.event.CommonEvent;
import com.etong.android.frame.publisher.HttpMethod;
import com.etong.android.frame.publisher.HttpPublisher;
import com.tencent.mm.sdk.openapi.IWXAPI;

public class PaymentProvider {
	// “00” – 银联正式环境
	// “01” – 银联测试环境，该环境中不发生真实交易
	public static final String PAYMENT_MODE_CODE = "00";

	// 初始化微信支付
	public static IWXAPI api = null;
	private HttpPublisher mHttpPublisher;
	@SuppressWarnings("unused")
	private static String TAG = "PaymentProvider";

	private static class Holder{
		private static final PaymentProvider INSTANCE = new PaymentProvider();
	}
	private PaymentProvider() {
		
	}

	public static PaymentProvider getInstance() {
		return Holder.INSTANCE;
	}

	public void initialize(HttpPublisher httpPublisher, Context context) {
		this.mHttpPublisher = httpPublisher;
	}

	public void payMent(PayInfo info) {

		Map<String, String> map = new HashMap<String, String>();

		map.put("param", JSON.toJSONString(info));
		String url = "", tag = "";
		switch (info.getType()) {
		case 0:// 0：微信支付;
			url = BaseHttpUri.URI_WECHAT_PAY;
			tag = CommonEvent.WECHAT_PAY;
			break;
		case 1:// 1：支付宝;
			url = BaseHttpUri.URI_ALIPAY_PAY;
			tag = CommonEvent.ALI_PAY;
			break;
		case 2:// 2：财付通;
			url = "";
			tag = "";
			break;
		case 3:// 3：信用卡;
			url = "";
			tag = "";
			break;
		case 4:// 4：储蓄卡;
			url = BaseHttpUri.URI_UNION_PAY;
			tag = CommonEvent.UNION_PAY;
			break;
		}
		HttpMethod method = new HttpMethod(url, map);

		mHttpPublisher.sendRequest(method, tag);
	}
}