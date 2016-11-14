package com.etong.android.frame.common;

/**
 * @ClassName : BaseHttpUri
 * @Description : 框架基本服务地址<br>
 *              已包以下接口地址<br>
 *              {@link #HTTP_SERVER_UNIFIED} 支付等统一接口的服务器地址<br>
 *              {@link #URI_UNION_PAY}银联支付接口地址<br>
 *              {@link #URI_ALIPAY_PAY}支付宝支付接口地址<br>
 *              {@link #URI_WECHAT_PAY} 微信支付接口地址<br>
 *              {@link #URL_UPDATE}自动更新接口地址<br>
 *              {@link #URL_OCR} OCR接口的服务器地址<br>
 * @author : zhouxiqing
 * @date : 2016-3-4 上午10:50:02
 * 
 */
public class BaseHttpUri {
	/** 支付等统一接口的服务器地址 */
	public static String HTTP_SERVER_UNIFIED = "http://payment.suiyizuche.com:8080";// 正式环境
	/** 银联支付接口地址 */
	public static String URI_UNION_PAY = HTTP_SERVER_UNIFIED
			+ "/pay/unionpay/wap";
	/** 支付宝支付接口地址 */
	public static String URI_ALIPAY_PAY = HTTP_SERVER_UNIFIED
			+ "/pay/alipay/getAlipayInfo";
	/** 微信支付接口地址 */
	public static String URI_WECHAT_PAY = HTTP_SERVER_UNIFIED
			+ "/pay/weiXin/getWeixinInfo";
	/** 自动更新接口地址 */
	public static String URL_UPDATE = HTTP_SERVER_UNIFIED
			+ "/version/app/download/info";// 外网正式
	// public static String URL_UPDATE =
	// "http://113.247.237.98:45277/version/app/download/info";// 外网测试
	/** OCR接口的服务器地址 */
	public static String URL_OCR = "http://apis.baidu.com/apistore/idlocr/ocr?apikey=92fad5bcb12b2b3846ee878cc2e1bd3e";
}
