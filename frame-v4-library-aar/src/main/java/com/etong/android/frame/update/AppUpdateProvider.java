package com.etong.android.frame.update;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.etong.android.frame.BaseApplication;
import com.etong.android.frame.common.BaseHttpUri;
import com.etong.android.frame.event.CommonEvent;
import com.etong.android.frame.publisher.HttpMethod;
import com.etong.android.frame.publisher.HttpPublisher;
import com.etong.android.frame.utils.ActivitySkipUtil;
import com.etong.android.frame.utils.ActivityStackManager;
import com.etong.android.frame.utils.ApkUtils;
import com.etong.android.frame.utils.logger.Logger;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : zhouxiqing
 * @ClassName : AppUpdateProvider
 * @Description : 自动更新,采用差分升级，需要加入libApkPatch.so</br> 调用
 * {@link #getInstance()}获取自动更新实例</br> 调用
 * {@link #initialize(HttpPublisher, String)}初始化自动更新实例</br> 调用
 * {@link #getUpdateInfo(AppUpdateResultAction)}或
 * {@link #getUpdateInfo(String, AppUpdateResultAction)}
 * 开始自动更新, 自动更新最好是在启动LaunchActivity中进行，并在{@link AppUpdateResultAction}回调中进行后续操作。</br>
 * 也可以使用过期方法{@link #getUpdateInfo()},{@link #getUpdateInfo(String)}开始更新，但要调用{@link #CanStart()}
 * 判断是否进入应用程序
 * @date : 2015-11-3 下午1:42:00
 */
public class AppUpdateProvider {

    private HttpPublisher mHttpPublisher;
    public static final String TAG = "AppUpdateProvider";

    /**
     * 返回更新内容为空
     */
    public static final int ERR_NULL = -1;
    /**
     * 网络异常
     */
    public static final int ERR_NETWORK = -2;
    /**
     * 取消更新
     */
    public static final int ERR_CANCLE = -3;
    /**
     * 稍后更新
     */
    public static final int ERR_LATER = -4;
    /**
     * 更新回调接口
     */
    public AppUpdateResultAction action;
    @Deprecated
    private static Boolean CAN_START = false;
    /**
     * 易通平台ID
     */
    private String app_key = null;
    private int versionCode = 0;// 当前应该版本
    private String versionName = "当前版本";// 当前应用名
    private String oldApkSource = null;

    private static class Holder {
        private static final AppUpdateProvider INSTANCE = new AppUpdateProvider();
    }

    private AppUpdateProvider() {
    }

    /**
     * @return AppUpdateProvider 自动更新实例
     * @Title : getInstance
     * @Description : 获取自动更新实例
     * @params
     */
    public static AppUpdateProvider getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * @param httpPublisher
     * @param app_key       易通自动更新key
     * @return void 返回类型
     * @Title : initialize
     * @Description : 初始化自动更新实例
     * @params
     */
    public void initialize(HttpPublisher httpPublisher, String app_key) {
        this.mHttpPublisher = httpPublisher;
        this.app_key = app_key;
        this.versionCode = BaseApplication.getApplication().getPackageInfo().versionCode;
        this.versionName = BaseApplication.getApplication().getPackageInfo().versionName;
        String packageName = BaseApplication.getApplication().getPackageName();
        oldApkSource = ApkUtils.getSourceApkPath(
                BaseApplication.getApplication(), packageName);
    }

    /**
     * @return Boolean 返回类型
     * @Title : CanStart
     * @Description : 是否进入程序
     * @params
     */
    @Deprecated
    public Boolean CanStart() {
        return CAN_START;
    }

    /**
     * @return void 返回类型
     * @Title : getUpdateInfo
     * @Description : 获取更新信息
     * @params 设定文件
     */
    @Deprecated
    public void getUpdateInfo() {
        getUpdateInfo(BaseHttpUri.URL_UPDATE, null);
    }

    /**
     * @param url 接口地址
     * @return void 返回类型
     * @Title : getUpdateInfo
     * @Description : 获取更新信息
     * @params 设定文件
     */
    @Deprecated
    public void getUpdateInfo(String url) {
        getUpdateInfo(url, null);
    }

    /**
     * @param action 回调
     * @return void 返回类型
     * @Title : getUpdateInfo
     * @Description : 获取更新信息
     * @params 设定文件
     */
    public void getUpdateInfo(AppUpdateResultAction action) {
        getUpdateInfo(BaseHttpUri.URL_UPDATE, action);
    }

    /**
     * @param url    接口地址
     * @param action 回调
     * @return void 返回类型
     * @Title : getUpdateInfo
     * @Description : 获取更新信息
     */
    public void getUpdateInfo(String url, AppUpdateResultAction action) {
        if (action != null) {
            this.action = action;
        } else {
            this.action = new AppUpdateResultAction() {

                @Override
                public void noUpdate() {
                    CAN_START = true;
                }

                @Override
                public void fail(int errCode, String errStr) {
                    CAN_START = true;
                }
            };
        }
        UpDateTask mTask = new UpDateTask();
        mTask.execute();
        Map<String, String> map = new HashMap<String, String>();
        map.put("appId", app_key);
        map.put("versionCode", versionCode + "");

        HttpMethod method = new HttpMethod(url, map);
        mHttpPublisher.sendRequest(method, CommonEvent.UPDATE);
    }

    // 查找更新任务
    private class UpDateTask extends AsyncTask<Void, Integer, Void> {
        Boolean run = true;
        AppUpdate info = null;
        HttpMethod method = null;

        // onPreExecute方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
            EventBus.getDefault().register(this);
        }

        // doInBackground方法内部执行后台任务,不可在此方法内修改UI
        @Override
        protected Void doInBackground(Void... path) {
            /*
			 * 死循环 等待EventBus访问网络成功过后 返回 在改变状态
			 */
            int i = 30;
            while (true) {
                if (!run || i == 0)
                    break;
                publishProgress((int) i);
                Logger.t(TAG).w("等待更新信息");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i--;
            }

            return null;
        }

        @Subscriber(tag = CommonEvent.UPDATE)
        public void onUpdateConfigFinish(HttpMethod method) {
            this.method = method;
            run = false;
        }

        // onProgressUpdate方法用于更新进度信息
        @Override
        protected void onProgressUpdate(Integer... progresses) {
        }

        // onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        @Override
        protected void onPostExecute(Void arg0) {
            int errCode = method.data().getIntValue("errCode");
            if (errCode == HttpPublisher.NETWORK_ERROR) {
                action.fail(ERR_NETWORK, "访问网络失败");
                return;
            }
            if (errCode == 0) {
                info = method.data().getObject("entity", AppUpdate.class);
            }
            if (info != null) {
                info.setOldApkSource(oldApkSource);
                String com = info.getComments();
                String[] reslut = com.split(";");
                StringBuilder strbuffer = null;
				/*
				 * 防止更新内容message过长 这里面进行分行处理
				 */
                if (reslut != null) {
                    strbuffer = new StringBuilder();
                    int i = 0, size = reslut.length;
                    for (; i < size; i++) {
                        strbuffer.append((i + 1) + "." + reslut[i] + "\n");
                    }
                    info.setComments(strbuffer.toString());
                }
                info.setTitle(versionName + "—>" + info.getVersionName());
                if (info.getVersionCode() != null
                        && !TextUtils.isEmpty(info.getUrl())
                        && strbuffer != null) {
                    if (versionCode < info.getVersionCode()) {// 当前版本小于更新版本
                        info.setAction(action);
                        EventBus.getDefault().postSticky(info, TAG);
                        ActivitySkipUtil.skipActivity(ActivityStackManager
                                        .create().topActivity(),
                                AppUpdateActivity.class);
                    } else {
                        action.noUpdate();
                    }
                }
            } else {
                action.fail(ERR_NULL, "更新内容为空");
            }
            EventBus.getDefault().unregister(this);
        }

        // onCancelled方法用于在取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {
        }
    }
}