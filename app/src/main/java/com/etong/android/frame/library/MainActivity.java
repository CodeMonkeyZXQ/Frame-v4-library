package com.etong.android.frame.library;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.etong.android.frame.library.search.SubscriberActivity;
import com.etong.android.frame.publisher.HttpMethod;
import com.etong.android.frame.publisher.HttpPublisher;
import com.etong.android.frame.update.AppUpdateProvider;
import com.etong.android.frame.update.AppUpdateResultAction;
import com.etong.android.frame.utils.CustomToast;
import com.etong.android.frame.utils.logger.Logger;
import com.tendcloud.tenddata.TCAgent;
import com.umeng.analytics.MobclickAgent;

import org.simple.eventbus.Subscriber;

import java.io.File;
import java.util.HashMap;

import cn.jiguang.analytics.android.api.CountEvent;
import cn.jiguang.analytics.android.api.JAnalyticsInterface;
import cn.jpush.android.api.JPushInterface;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class MainActivity extends SubscriberActivity{
    private int[] mBgColors;

    private static int mBgIndex = 0;

    private String mKeyTrackingMode;

    private SwipeBackLayout mSwipeBackLayout;
    int i = 0;

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onInit(savedInstanceState);

        mKeyTrackingMode = "key_tracking_mode";
        mSwipeBackLayout = getSwipeBackLayout();
        AppUpdateProvider.getInstance().initialize(HttpPublisher.getInstance(), "1014");

        this.findViewById(R.id.hello, TextView.class).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppUpdateProvider.getInstance().getUpdateInfo("http://payment.suiyizuche.com:8080/version/app/1014", new AppUpdateResultAction() {
                    @Override
                    public void noUpdate() {
                        toastMsg("暂无更新");
                    }

                    @Override
                    public void fail(final int errCode, final String errStr) {
                        TCAgent.onEvent(MainActivity.this, "应用更新", "未更新", new HashMap<String, String>() {{
                            put("errCode", errCode + "");
                            put("errStr", errStr);
                        }});
                        switch (errCode) {
                            case AppUpdateProvider.ERR_LATER:// 稍后更新
                                JAnalyticsInterface.onEvent(MainActivity.this, new CountEvent("更新取消"));
                                MobclickAgent.onEvent(MainActivity.this, "更新取消");
                            case AppUpdateProvider.ERR_NULL:// 返回更新内容为空
                            case AppUpdateProvider.ERR_NETWORK:// 网络异常
                            case AppUpdateProvider.ERR_CANCLE:// 取消更新
                                break;
                        }
                    }

                    @Override
                    public void haveUpdate() {
                                            }
                });
 /*                if(i%2==0){
                    CustomToast.setLayoutRes(MainActivity.this,R.layout.layout_loading_view);
                }else
                if(i%3==0){
                    CustomToast.setLayoutRes(MainActivity.this,R.layout.toast);
                }else
                if(i%5==0){
                    CustomToast.setLayoutRes(MainActivity.this,R.layout.jpush_webview_layout,R.id.tvRichpushTitle);
                }else
                if(i%7==0){
                    CustomToast.setLayoutRes(MainActivity.this,R.layout.activity_app_update,R.id.update_tip);
                    i=0;
                }else{
                    CustomToast.setLayoutRes(MainActivity.this,0);
                }
                toastMsg("测试~~~"+i);
                i++;
               int edgeFlag;
                switch (i) {
                    case 0:
                        edgeFlag = SwipeBackLayout.EDGE_LEFT;
                        i++;
                        break;
                    case 1:
                        edgeFlag = SwipeBackLayout.EDGE_RIGHT;
                        i++;
                        break;
                    case 2:
                        edgeFlag = SwipeBackLayout.EDGE_BOTTOM;
                        i++;
                        break;
                    default:
                        edgeFlag = SwipeBackLayout.EDGE_ALL;
                        i=0;
                }
                mSwipeBackLayout.setEdgeTrackingEnabled(edgeFlag);*/
//                PhotoUtils.startPhotoUtils(MainActivity.this, "photo", false);
            }
        });


        HttpPublisher.getInstance().sendRequest(new HttpMethod("https://kyfw.12306.cn/otn/",null),"test https");
    }

    @Subscriber(tag = "photo")
    public void onPhoto(Bitmap map) {
//        UploadImageProvider.getInstance().uploadImage(map, "uploadImg", "55");
//        UploadImageProvider.getInstance().cancleByTag("uploadImg");
//        UploadImageProvider.getInstance().uploadImage(map, "uploadImg", "55");
    }

    @Subscriber(tag = "photo")
    public void onPhoto(final String img) {
        final File oldFile = new File(img);
        Luban.get(this)
                .load(oldFile)                     //传人要压缩的图片
                .putGear(Luban.THIRD_GEAR)      //设定压缩档次，默认三挡
                .setCompressListener(new OnCompressListener() { //设置回调

                    @Override
                    public void onStart() {
                        // TODO 压缩开始前调用，可以在方法内启动 loading UI
                        loadStart("图片压缩中。。。",0);
                    }
                    @Override
                    public void onSuccess(File file) {
                        // TODO 压缩成功后调用，返回压缩后的图片文件
                        Logger.i("path", file.getAbsolutePath());
                        Logger.i("old file path:" + oldFile.getAbsolutePath() + "\n old file size:" + oldFile.length() / 1024 + "k\n"
                                 + "new file path:" + file.getAbsolutePath() + "\n new file size:" + file.length() / 1024 + "k\n" );
                        toastMsg("图片压缩完成"+ file.getAbsolutePath());
                        loadFinish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        // TODO 当压缩过去出现问题时调用
                        toastMsg("压缩过程异常");
                    }
                }).launch();
//        UploadImageProvider.getInstance().uploadImage(img, "uploadImg", "66");
//        UploadImageProvider.getInstance().cancleByTag("uploadImg");
//        UploadImageProvider.getInstance().uploadImage(img, "uploadImg", "66");

       /*Observable.just(img).subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .map(new Function<String, Bitmap>() {
                    @Override
                    public Bitmap apply(String s) throws Exception {
                        return BitmapFactory.decodeFile(img);
                    }
                })
        .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Bitmap>() {
                    @Override
                    public void accept(Bitmap bitmap) throws Exception {
                        UploadImageProvider.getInstance().uploadImage(bitmap, "uploadImg", "66");
                    }
                });
        Observable.just(img).subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .map(new Func1<String, Bitmap>() {
                    @Override
                    public Bitmap call(String s) {
                        Logger.d("call:thread id "+Thread.currentThread().getName());
                        return BitmapFactory.decodeFile(s);
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new rx.Subscriber<Bitmap>() {
                    @Override
                    public void onCompleted() {
                        Logger.d("onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Logger.d("onError");
                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        Logger.d("onNext:thread id "+Thread.currentThread().getName());
                        UploadImageProvider.getInstance().uploadImage(bitmap,"uploadImg", "66");
                    }
                });*/
    }

    @Subscriber(tag = "uploadImg")
    public void onPhoto(JSONObject data) {
        Logger.json(data.toJSONString());
    }

    @Subscriber(tag = "test https")
    public void testHttps(HttpMethod method) {
        Logger.json(method.toJSONString());
    }

    @Override
    protected void back() {
        super.back();
        CustomToast.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
        JPushInterface.setAlias(this, "frame", null);
        JAnalyticsInterface.onPageStart(this, "首页");
        MobclickAgent.onPageStart("首页");
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
        JAnalyticsInterface.onPageEnd(this, "首页");
        MobclickAgent.onPageEnd("首页");
    }
}
