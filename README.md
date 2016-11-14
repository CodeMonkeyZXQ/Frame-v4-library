# Frame-v4-library

# Android开发框架Frame-v4-library

## 一、在你的项目中导入框架的aar包
### 不知道导入的请移步http://jingyan.baidu.com/article/2a13832890d08f074a134ff0.html

## 二、配置Gradle

```java
apply plugin: 'com.android.application'

android {
    compileSdkVersion 24
    buildToolsVersion "24"

    defaultConfig {
        applicationId "com.etong.android.frame.library"
        minSdkVersion 11
        targetSdkVersion 24
        versionCode 1
        versionName "1.0"
        multiDexEnabled true

        ndk {
            //选择要添加的对应cpu类型的.so库:'armeabi', 'armeabi-v7a', 'armeabi-v8a', 'x86', 'x86_64', 'mips', 'mips64'
            abiFilters 'armeabi', 'armeabi-v7a', 'armeabi-v8a', 'x86', 'x86_64', 'mips', 'mips64'
        }

        manifestPlaceholders = [
                JPUSH_PKGNAME: "com.etong.android.frame.library",//JPush上注册的包名
                JPUSH_APPKEY : "809e7c2add6f66268b11440a", //JPush上注册的包名对应的appkey.
                JPUSH_CHANNEL: "developer-default", //JPush.暂时填写默认值即可.

                PGYER_APPID  : "701e5d51de0a931dd3f19abc7f168f3d",//蒲公英appkey
                WECHAT_APPID : "00000000000000000000000000000000000000000",//微信分享及支付appkey
        ]

    }

    signingConfigs {
        release {
            keyAlias "dashenggouche"//签名的别名
            keyPassword "etong123456"//密码
            storeFile file('E:/Etongzuche/trunk/app/android/frame-v2/APK/dashenggouche.keystore')//签名文件的路径
            storePassword "etong123456"//签名密码
//            storeType ""//类型
        }
    }

    buildTypes {
        debug {
            manifestPlaceholders = [
                    JPUSH_PKGNAME: "com.etong.android.frame.library",//JPush上注册的包名
                    JPUSH_APPKEY : "809e7c2add6f66268b11440a", //JPush上注册的包名对应的appkey.
                    JPUSH_CHANNEL: "developer-default", //JPush.暂时填写默认值即可.

                    PGYER_APPID  : "701e5d51de0a931dd3f19abc7f168f3d",//蒲公英appkey
                    WECHAT_APPID : "00000000000000000000000000000000000000000",//微信分享及支付appkey
            ]
        }
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
            signingConfig signingConfigs.release

            manifestPlaceholders = [
                    JPUSH_PKGNAME: "com.etong.android.frame.library",//JPush上注册的包名
                    JPUSH_APPKEY : "809e7c2add6f66268b11440a", //JPush上注册的包名对应的appkey.
                    JPUSH_CHANNEL: "developer-default", //JPush.暂时填写默认值即可.

                    PGYER_APPID  : "701e5d51de0a931dd3f19abc7f168f3d",//蒲公英appkey
                    WECHAT_APPID : "00000000000000000000000000000000000000000",//微信分享及支付appkey
            ]
        }
    }

    productFlavors {//多渠道打包
        etong {
            manifestPlaceholders = [CHANNEL_VALUE: "etong"]
        }
        qh360 {
            manifestPlaceholders = [CHANNEL_VALUE: "qh360"]
        }
        baidu {
            manifestPlaceholders = [CHANNEL_VALUE: "baidu"]
        }
        anzhi {
            manifestPlaceholders = [CHANNEL_VALUE: "anzhi"]
        }
        tencent {
            manifestPlaceholders = [CHANNEL_VALUE: "tencent"]
        }
}
}

repositories {
    jcenter()
    mavenLocal()
    mavenCentral()
    flatDir {
        dirs 'libs'
    }
}

dependencies {
    compile fileTree(include: ['*.jar'], dir: 'libs')
    compile(name:'frame-v4-library', ext:'aar')
//    compile project(':frame-v4-library-aar')
    compile 'com.android.support:appcompat-v7:24.+'
    compile 'com.squareup.okhttp3:okhttp:3.3.1'
    testCompile 'junit:junit:4.12'
    compile 'cn.jiguang:jpush:2.1.9'//极光推送
}
```

## 三、清单文件中（AndroidManifest.xml）需要添加的部分
请替换PackageName字段    
请替换PackageName字段    
请替换PackageName字段

要使用微信分享及支付时添加
```java
        <!-- 微信分享回调 -->
    <activity
        android:name="PackageName.wxapi.WXEntryActivity"
        android:configChanges="keyboardHidden|orientation|screenSize"
        android:exported="true"
        android:screenOrientation="portrait"
        android:theme="@android:style/Theme.Translucent.NoTitleBar"></activity>
    <!-- 微信支付回调 -->
    <activity
        android:name="${package}.wxapi.WXPayEntryActivity"
        android:exported="true"
        android:launchMode="singleTop"
        android:screenOrientation="portrait"
        android:theme="@android:style/Theme.NoDisplay" />
```

要使用JPush推送功能时添加
```xml
    <!-- User defined.  For test only  用户自定义的广播接收器 -->
    <receiver
        android:name="PackageName.receiver.JPushReceiver"
        android:enabled="true"
        android:exported="false">
        <intent-filter>
            <!-- Required  用户注册SDK的intent -->
            <action android:name="cn.jpush.android.intent.REGISTRATION" />
            <action android:name="cn.jpush.android.intent.UNREGISTRATION" />
            <!-- Required  用户接收SDK消息的intent -->
            <action android:name="cn.jpush.android.intent.MESSAGE_RECEIVED" />
            <!-- Required  用户接收SDK通知栏信息的intent -->
            <action android:name="cn.jpush.android.intent.NOTIFICATION_RECEIVED" />
            <!-- Required  用户打开自定义通知栏的intent -->
            <action android:name="cn.jpush.android.intent.NOTIFICATION_OPENED" />
            <!-- Optional 用户接受Rich Push Javascript 回调函数的intent -->
            <action android:name="cn.jpush.android.intent.ACTION_RICHPUSH_CALLBACK" />
            <!-- 接收网络变化 连接/断开 since 1.6.3 -->
            <action android:name="cn.jpush.android.intent.CONNECTION" />
            <category android:name="PackageName" />
        </intent-filter>
    </receiver>
```

## 四、添加代码
### 1、添加自定义Application，并继承BaseApplication类并在清单文件中（AndroidManifest.xml）中设置
eg:
```java
public class EtApplication extends BaseApplication {
	protected static final String TAG = "EtApplication";
	protected static EtApplication application;

	static public EtApplication getApplication() {
		return application;
	}

	@Override
	public void onCreate() {
		setDebugMode(true);
		super.onCreate();

		application = this;
	}
}
```
eg:
```xml
  <application
        android:name=".EtApplication"<!-- 设置自定义Application-->
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:supportsRtl="true"
        android:theme="@style/AppTheme" >
        <activity/>
           ......
    </application>
```

### 2、添加Activity的基类PackgeName/SubscriberActivity.java
所有的activity都继承这个基类，你可以在这里进行一些定制化的方法。
```java
public abstract class SubscriberActivity extends BaseSubscriberActivity {
	// TODO 这里可以写一些自定义的方法
}
```
### 3、添加Fragment的基类PackgeName/SubscriberFragment .java
所有的Fragment都继承这个基类，你可以在这里进行一些定制化的方法。
```java
public abstract class SubscriberFragment extends BaseSubscriberFragment {
	// TODO 这里可以写一些自定义的方法
}
```
### 4、当要使用推送功能时，请添加PackgeName/receiver/JPushReceiver.java
```java
public class JPushReceiver extends BaseJPushReceiver{

	@Override
	public void handleCustomMessage(Context context, Bundle bundle) {
		// TODO 处理自定义消息
	}

	@Override
	public void handNotification(Context context, Bundle bundle) {
		// TODO 处理通知
	}

	@Override
	public void openNotification(Context context, Bundle bundle) {
		// TODO 处理通知点击后的操作
	}
}
```
### 5、当要使用微信分享功能时，请添加PackgeName/wxapi/WXEntryActivity.java

微信客户端回调activity示例
```java
public class WXEntryActivity extends WXShareEntryActivity {

	@Override
	protected void onInit() {
		initWXAPI(MarkUtils.WECHAT_APP_ID);
	}

	@Override
	protected void shareSuccess(ShareInfo info, int errorCode, String msg,
			String tag) {
			toastMsg("微信分享成功！");
	}

	@Override
	protected void shareCancle(ShareInfo info, int errorCode, String msg,
			String tag) {
		toastMsg("微信分享取消！");
	}

	@Override
	protected void shareFail(ShareInfo info, int errorCode, String msg,
			String tag) {
			toastMsg("微信分享失败！", errorCode + ":" + msg);
	}

}
```
### 6、当要使用微信支付功能时，请添加PackgeName/wxapi/WXPayEntryActivity.java
```java
public class WXPayEntryActivity extends WXPaymentEntryActivity{

	@Override
	protected void onInit() {
		initWXAPI(MarkUtils.WECHAT_APP_ID);
	}
}
```
### 7、当要使用支付功能时，请使支付Activity继承PaymentActivity
```java
public class OrderInfoActivity extends PaymentActivity {
	@Override
	protected void onInit() {
		setContentView(R.layout.activity_order_info);
		initWXAPI(MarkUtils.WECHAT_APP_ID);
	}

	@Override
	protected void paySuccess(PayInfo pay, int errorCode, String msg) {
		if (pay.getOrderId().equals(info.getF_ofid() + "")) {
        // TODO 支付成功，最好是与后台进行支付结果确认
		}

	}

	@Override
	protected void payCancle(PayInfo pay, int errorCode, String msg) {
		// TODO 支付取消

	}

	@Override
	protected void payFail(PayInfo pay, int errorCode, String msg) {
		// TODO 支付失败
	}
}
```
### 8、当要使用自动更新功能时，请在进行更新的页面初始化并启动自动更新
```java
// 初始化自动更新
AppUpdateProvider.getInstance().initialize(mHttpPublisher,
			MarkUtils.ETONG_APPKEY);
// 自动更新
AppUpdateProvider.getInstance().getUpdateInfo(url,
		new AppUpdateResultAction() {

			@Override
			public void noUpdate() {
				//TODO 无更新处理
			}

			@Override
			public void fail(int errCode, String errStr) {
				switch (errCode) {
				case AppUpdateProvider.ERR_NULL:// 返回更新内容为空
				case AppUpdateProvider.ERR_NETWORK:// 网络异常
				case AppUpdateProvider.ERR_CANCLE:// 取消更新
				case AppUpdateProvider.ERR_LATER:// 稍后更新
						//TODO 更新异常处理
					break;
				}

			}
		});
```
