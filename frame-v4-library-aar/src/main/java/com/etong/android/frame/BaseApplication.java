package com.etong.android.frame;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.telephony.TelephonyManager;

import com.alibaba.fastjson.JSON;
import com.etong.android.frame.okhttp.SSLParamsUtils;
import com.etong.android.frame.okhttp.cookie.CookieJarImpl;
import com.etong.android.frame.okhttp.cookie.store.PersistentCookieStore;
import com.etong.android.frame.publisher.HttpPublisher;
import com.etong.android.frame.publisher.SharedPublisher;
import com.etong.android.frame.utils.ConfigInfo;
import com.etong.android.frame.utils.CrashHandler;
import com.etong.android.frame.utils.ImageProvider;
import com.etong.android.frame.utils.UploadImageProvider;
import com.etong.android.frame.utils.logger.LogLevel;
import com.etong.android.frame.utils.logger.Logger;
import com.pgyersdk.crash.PgyCrashManager;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;
import okhttp3.OkHttpClient;

public class BaseApplication extends Application {
	protected static final String TAG = "EtongApplication";
	protected CrashHandler mCrashHandler = CrashHandler.getInstance();
	protected static BaseApplication application;
	protected ConfigInfo mConfigInfo;
	protected SharedPublisher mSharedPublisher;
	protected HttpPublisher mHttpPublisher;
	protected ImageProvider mImageProvider;
	protected final String USER_SHARED = "userShared";
	protected final String USER_CONFIG = "userConfig";
	/** 标注是否为Debug模式 */
	public static Boolean isDebug = false;
	public static Boolean pgyUpdate = false;

	static public BaseApplication getApplication() {
		return application;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		application = this;

		// 注册CrashHandler
		if (isDebug) {
			Logger.init(this.getString(R.string.app_name));
			//蒲公英测试版更新服务
			pgyUpdate = true;
		} else {
			mCrashHandler.init(getApplicationContext());
			Logger.init(this.getString(R.string.app_name)).logLevel(
					LogLevel.NONE);
		}
		// 初始化JPush SDK
		JPushInterface.setDebugMode(isDebug);
		JPushInterface.init(getApplicationContext());
		JPushInterface.setLatestNotificationNumber(getApplicationContext(), 1);

		//注册蒲公英异常上报功能
		PgyCrashManager.register(this);

		// 初始化缓存
		mSharedPublisher = SharedPublisher.getInstance();
		mSharedPublisher.initialize(getApplicationContext());

		// 初始化Http请求
		SSLParamsUtils.SSLParams sslParams = SSLParamsUtils.getSslSocketFactory(null, null, null);
		CookieJarImpl cookieJarl = new CookieJarImpl(new PersistentCookieStore(getApplicationContext()));
		OkHttpClient okHttpClient = new OkHttpClient.Builder()
				.connectTimeout(20000L, TimeUnit.MILLISECONDS)
				.readTimeout(20000L, TimeUnit.MILLISECONDS)
				.cookieJar(cookieJarl)
//				.hostnameVerifier(new HostnameVerifier()
//				{
//					@Override
//					public boolean verify(String hostname, SSLSession session)
//					{
//						return true;
//					}
//				})
//				.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
				.build();
		mHttpPublisher = HttpPublisher.getInstance();
		mHttpPublisher.initialize(getApplicationContext(),okHttpClient);

		UploadImageProvider.getInstance().initialize(getApplicationContext(),okHttpClient);

		mImageProvider = ImageProvider.getInstance();
		mImageProvider.initialize(getApplicationContext());
	}

	/**
	 * 标注是否为Debug模式，默认为false <br>
	 * 必须在{@code super.onCreate()}之前进行设置
	 */
	public void setDebugMode(Boolean isDebug) {
		BaseApplication.isDebug = isDebug;
	}

	/**
	 * @Title : getConfigInfo
	 * @Description : 获取配置信息
	 * @params
	 * @return 设定文件
	 * @return ConfigInfo 配置信息
	 */
	public ConfigInfo getConfigInfo() {
		if (null == mConfigInfo) {
			String userConfig = mSharedPublisher.getString(USER_CONFIG);
			if (null != userConfig && !userConfig.isEmpty())
				mConfigInfo = JSON.parseObject(userConfig, ConfigInfo.class);
		}
		return mConfigInfo;
	}

	/**
	 * @Title : setConfigInfo
	 * @Description : 设置配置信息
	 * @params
	 * @param configInfo
	 *            配置信息
	 * @return void 返回类型
	 * @throws
	 */
	public void setConfigInfo(ConfigInfo configInfo) {
		this.mConfigInfo = configInfo;
		mSharedPublisher.put(USER_CONFIG, JSON.toJSONString(configInfo));
	}

	/**
	 * 获取App安装包信息
	 * 
	 * @return
	 */
	public PackageInfo getPackageInfo() {
		PackageInfo info = null;
		try {
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			Logger.e(e, "getPackageInfo NameNotFoundException");
		}
		if (info == null)
			info = new PackageInfo();
		return info;
	}

	public NotificationManager getNotificationManager() {
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		return mNotificationManager;
	}

	public String getUniqueId() {
		final TelephonyManager tm = (TelephonyManager) getBaseContext()
				.getSystemService(Context.TELEPHONY_SERVICE);

		@SuppressWarnings("unused")
		final String tmDevice, tmSerial, tmPhone, androidId;
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = ""
				+ android.provider.Settings.Secure.getString(
						getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);

		UUID deviceUuid = new UUID(androidId.hashCode(),
				((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
		String uniqueId = deviceUuid.toString();
		return uniqueId;
	}

}