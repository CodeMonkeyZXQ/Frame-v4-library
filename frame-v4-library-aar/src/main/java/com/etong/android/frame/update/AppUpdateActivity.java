package com.etong.android.frame.update;

import android.Manifest;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.etong.android.frame.R;
import com.etong.android.frame.permissions.PermissionsManager;
import com.etong.android.frame.permissions.PermissionsResultAction;
import com.etong.android.frame.subscriber.BaseSubscriberActivity;
import com.etong.android.frame.utils.ActivityStackManager;
import com.etong.android.frame.utils.ApkUtils;
import com.etong.android.frame.utils.SignUtils;
import com.etong.android.frame.utils.StoragePathUtils;
import com.etong.android.frame.utils.logger.Logger;

import org.simple.eventbus.Subscriber;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author : zhouxiqing
 * @ClassName : AppUpdateActivity
 * @Description : 自动更新页面
 * @date : 2015-11-3 下午2:42:39
 */
@SuppressWarnings("deprecation")
public class AppUpdateActivity extends BaseSubscriberActivity {
    private final static String TAG = "AppUpdateActivity";
    private LinearLayout mPromptedUpdate;
    private RelativeLayout mProgressUpdate;
    private TextView mUpdateTip;
    private ProgressBar mDownloadProgress;
    private TextView mDownloadProgressValue;
    private Button mBtnUpdataCancle;
    private Button mBtnCancle;

    private AppUpdate info = null;// 更新信息
    private File load_file = null;// 下载的文件
    private String FileName;// 下载的文件名
    private String FilePath = null;// 下载的文件路径
    private boolean iscancle = false;// 是否可以取消
    private OkHttpClient client = null;
    private Request downloadCall = null;// 下载文件
    private AppUpdateResultAction action;

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        setFinishOnTouchOutside(false);// 设置外部点击不消失
        this.setContentView(R.layout.activity_app_update);
        registerSticky();
        initViews();
        this.action = AppUpdateProvider.action;
    }

    public void initViews() {
        mPromptedUpdate = findViewById(R.id.prompted_update,
                LinearLayout.class);
        mProgressUpdate = findViewById(R.id.progress_update,
                RelativeLayout.class);
        mUpdateTip = findViewById(R.id.update_tip, TextView.class);
        mDownloadProgress = findViewById(R.id.download_progress,
                ProgressBar.class);
        mDownloadProgressValue = findViewById(R.id.download_progress_value,
                TextView.class);
        mBtnUpdataCancle = findViewById(R.id.update_cancel, Button.class);
        mBtnCancle = findViewById(R.id.download_cancle, Button.class);

        addClickListener(R.id.update_cancel);
        addClickListener(R.id.update_ok);
        addClickListener(R.id.download_cancle);

        mProgressUpdate.setVisibility(View.GONE);
    }

    /**
     * @param data 更新信息
     * @return void 返回类型
     * @Title : initData
     * @Description : 初始化数据
     * @params
     */
    @Subscriber(tag = AppUpdateProvider.TAG)
    public void initData(AppUpdate data) {
        client = new OkHttpClient();
        // 初始化文件路径
        FilePath = StoragePathUtils.getStoragePaths(this)[0] + File.separator
                + "Download" + File.separator;
        info = data;
        mEventBus.removeStickyEvent(AppUpdate.class);

        // 没有升级信息时默认进入app
        if (info == null) {
            action.fail(AppUpdateProvider.ERR_NULL, "更新内容为空");
            this.finish();
            return;
        }

        // 本地apk包无法使用或差分合成so无法加载时设为完全更新
        if (TextUtils.isEmpty(info.getOldApkSource()) || !PatchUtils.canPatch()) {
            info.setIspatch(false);
        }

        // 初始化文件名
        FileName = info.getAppName();
        String tip = info.getTitle();

        // 下载文件大小大于零且为差分更新时显示下载文件大小
        if (info.getSize() > 0 && info.isIspatch()) {
            tip += "     " + info.getSize() + "MB\n";
        }
        tip += "更新内容\n";
        tip += info.getComments();
        if (!TextUtils.isEmpty(info.getOldMd5())) {// 旧MD5不为空
            if (!SignUtils.checkMd5(info.getOldApkSource(), info.getOldMd5())) {// 本地安装的apkMD5错误
                tip = "注意！注意！注意！\n您安装的是盗版应用，经过非法修改的应用可能会损害您的权益，请到各应用市场下载正版！";
                info.setInstall(true);// 设置更新过程不可跳过
                mUpdateTip.setTextColor(Color.RED);
                mBtnCancle.setClickable(false);
            }
        }
        // 显示提示信息
        mUpdateTip.setText(tip);

        if (info.isInstall()) {
            mBtnUpdataCancle.setText("退出程序");
            mBtnCancle.setText("取消下载并退出");

        }
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.update_cancel) {// 稍后更新
            if (info.isInstall()) {
                ActivityStackManager.create().AppExit(this);
            }
            action.fail(AppUpdateProvider.ERR_LATER, "稍后更新");
            this.finish();
            return;
        }

        if (view.getId() == R.id.update_ok) {// 现在更新
            mPromptedUpdate.setVisibility(View.GONE);
            mProgressUpdate.setVisibility(View.VISIBLE);
            startDownload();
            return;
        }

        if (view.getId() == R.id.download_cancle) {// 取消下载
            if (info.isInstall()) {
                ActivityStackManager.create().AppExit(this);
                return;
            }
            if (iscancle) {// 可取消
                downloadCancle();
                downloadCall = null;
                load_file.delete();
                action.fail(AppUpdateProvider.ERR_CANCLE, "更新取消");
                this.finish();
            } else {
                // 重试下载
                startDownload();
                mBtnCancle.setText("取消下载");
            }
            return;
        }

    }

    private void downloadFile(String url) {
        String fileFullName = FileName;
        if (info.isIspatch()) {
            fileFullName += ".patch";
        } else {
            fileFullName += ".apk";
        }
        String AbsolutePath = FilePath + fileFullName;
        load_file = new File(AbsolutePath);
        if (load_file.exists()) {
            load_file.delete();
        }

        if (downloadCall == null) {
            downloadCall = new Request.Builder().get().url(url).tag(TAG).build();
        }

        client.newCall(downloadCall).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (call.isCanceled()) {
                    System.out.println("下载取消");
                } else {
                    System.out.println("下载失败");
                    toastMsg("下载失败，请检查网络后重试！");
                    downloadFail();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream inputStream = response.body().byteStream();
                FileOutputStream fileOutputStream = null;
                float count = response.body().contentLength();
                float download = 0;
                try {
                    fileOutputStream = new FileOutputStream(new File(load_file.getAbsolutePath()));
                    byte[] buffer = new byte[2048];
                    int len = 0;
                    while ((len = inputStream.read(buffer)) != -1) {
                        download += len;
                        getEventBus().post(download / count, "downloadProgress");
                        fileOutputStream.write(buffer, 0, len);
                    }
                    fileOutputStream.flush();
                } catch (IOException e) {
                    if (!call.isCanceled()) {
                        Logger.e(e, "IOException");
                        downloadFail();
                    }
                }

                if (call.isCanceled()) {
                    System.out.println("下载取消");
                } else {
                    // 下载成功
                    System.out.println("下载成功");
                    downloadSuccess(load_file);
                    mBtnCancle.setText("下载完成");
                }
            }
        });
    }

    /**
     * @return void 返回类型
     * @Title : startDownload
     * @Description : 检查授权情况并开始下载
     * @params 设定文件
     */
    private void startDownload() {
        PermissionsManager
                .getInstance()
                .requestPermissionsIfNecessaryForResult(
                        this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        new PermissionsResultAction() {

                            @Override
                            public void onGranted() {
                                if (info.isIspatch()) {// 差分升级，下载差分包
                                    downloadFile(info.getUrl());
                                } else {// 完整升级，下载完整包
                                    downloadFile(info.getCompleteUrl());
                                }
                                iscancle = true;
                            }

                            @Override
                            public void onDenied(String permission) {
                                toastMsg("授权失败，更新无法完成！");
                                onClick(findViewById(R.id.update_cancel));
                            }
                        });
    }

    @Subscriber(tag = "downloadProgress")
    private void downloadProgress(float progress) {
        mDownloadProgressValue.setText(((int) (100 * progress)) + "%");
        mDownloadProgress.setProgress((int) (100 * progress));
    }

    private void downloadSuccess(File file) {
        if (file != null) {
            String apkPath = file.getAbsolutePath();
            // 差分时进行合成apk
            if (info.isIspatch()) {// 差分安装，合成apk
                apkPath = FilePath + FileName + ".apk";
                int code = PatchUtils.bspatch(info.getOldApkSource(), apkPath,
                        file.getAbsolutePath());
                switch (code) {
                    case PatchUtils.PATCH_SUCCESS:
                        load_file.delete();
                        ApkUtils.installApk(this, apkPath);
                        ActivityStackManager.create().AppExit(this);
                        break;
                    case PatchUtils.PATCH_FAIL:
                        toastMsg("更新失败，进行完整更新！");
                        mBtnCancle.setText("完整更新");
                        info.setIspatch(false);
                        info.setInstall(false);
                        iscancle = false;
                        break;
                }
            } else {
                ApkUtils.installApk(this, apkPath);
                ActivityStackManager.create().AppExit(this);
            }
        }
    }

    private void downloadFail() {
        toastMsg("下载失败，请稍后重试！");
        mBtnCancle.setText("点击重试");
        iscancle = false;
    }

    private void downloadCancle() {
        for (Call call : client.dispatcher().runningCalls()) {
            if (TAG.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : client.dispatcher().queuedCalls()) {
            if (TAG.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
