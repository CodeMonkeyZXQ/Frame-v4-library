package com.etong.android.frame.payment;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;

import com.etong.android.frame.subscriber.BaseSubscriberActivity;
import com.pgyersdk.crash.PgyCrashManager;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * @author : zhouxiqing
 * @ClassName : WXPaymentEntryActivity
 * @Description: <p>
 * 微信支付回调接收基类
 * </p>
 * <p>
 * 当使用微信支付时须在PackageName.wxapi下添加名为WXPayEntryActivity的Activity,
 * 继承此类<br>
 * 且先调用{@link #initWXAPI(String WECHAT_APP_ID)}初始化微信API
 * </p>
 * <p>
 * Manifest文件中设置android:exported="true"
 * </p>
 * @date : 2016-3-2 上午11:23:31
 */
public abstract class WXPaymentEntryActivity extends BaseSubscriberActivity
        implements IWXAPIEventHandler {

    private static final String TAG = ".WXPayEntryActivity";

    private IWXAPI api;

    @Override
    protected void onInit(Bundle savedInstanceState) {
        // 初始化
        initWXAPI();
        onInit();
    }


    /**
     * @param WECHAT_APP_ID 微信APP ID
     * @return void 返回类型
     * @Title : initWXAPI
     * @Description : 初始化微信API
     * @params
     */
    @Deprecated
    public void initWXAPI(String WECHAT_APP_ID) {
        api = WXAPIFactory.createWXAPI(this, WECHAT_APP_ID);
        api.handleIntent(getIntent(), this);
    }

    protected void initWXAPI() {
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(),
                    PackageManager.GET_META_DATA);
            String WECHAT_APP_ID = appInfo.metaData.getString("WECHAT_APPID");
            if (!TextUtils.isEmpty(WECHAT_APP_ID)) {
                api = WXAPIFactory.createWXAPI(this, WECHAT_APP_ID);
                api.handleIntent(getIntent(), this);
            }
        } catch (PackageManager.NameNotFoundException e) {
            PgyCrashManager.reportCaughtException(this,e);
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
        System.out.println(TAG + ".onReq(BaseReq req)" + req.toString());
        mEventBus.post(req);
        this.finish();
    }

    @Override
    public void onResp(BaseResp resp) {
        System.out.println(TAG + ".onResp(BaseResp resp)" + resp.toString());
        mEventBus.post(resp);
        this.finish();
    }

    /**
     * @return void 返回类型
     * @Title : onInit
     * @Description : 初始化，子类可在该函数中对界面进行初始化(界面并不会显示)
     * @params 设定文件
     */
    abstract protected void onInit();
}