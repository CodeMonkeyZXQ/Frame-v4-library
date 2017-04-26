package com.etong.android.frame.library;

import com.etong.android.frame.BaseApplication;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.io.InputStream;

import cn.jiguang.analytics.android.api.JAnalyticsInterface;

/**
 * Created by zhouxiqing on 2016/7/19.
 */

public class EtApplication extends BaseApplication {
    public void onCreate() {
        setDebugMode(true);
        super.onCreate();
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(this);
        InputStream certificates[] = new InputStream[1];
        try {
            certificates[0] = this.getAssets().open("12306.cer");
        } catch (IOException e) {
            e.printStackTrace();
        }
        initSSLParams(certificates, null, null);

        JAnalyticsInterface.init(this);
        JAnalyticsInterface.setDebugMode(isDebug);

        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
    }
}
