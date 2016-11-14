package com.etong.android.frame.library;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.etong.android.frame.library.search.SubscriberActivity;
import com.etong.android.frame.utils.CustomToast;
import com.etong.android.frame.utils.UploadImageProvider;
import com.etong.android.frame.utils.logger.Logger;

import org.simple.eventbus.Subscriber;

import cn.jpush.android.api.JPushInterface;
import me.imid.swipebacklayout.lib.SwipeBackLayout;

public class MainActivity extends SubscriberActivity {
    private RadarView mRadarView;

    private int[] mBgColors;

    private static int mBgIndex = 0;

    private String mKeyTrackingMode;

    private SwipeBackLayout mSwipeBackLayout;
    int i = 0;

    @Override
    protected void onInit(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onInit(savedInstanceState);
        mRadarView = (RadarView) findViewById(R.id.radar_view);
        mRadarView.setSearching(true);
        mRadarView.setVisibility(View.GONE);
//        mRadarView.addPoint();
//        mRadarView.addPoint();

        mKeyTrackingMode = "key_tracking_mode";
        mSwipeBackLayout = getSwipeBackLayout();

        this.findViewById(R.id.hello, TextView.class).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(i%2==0){
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
/*                int edgeFlag;
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
/*                Logger.d(PermissionsManager.getInstance().hasPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ? "已授权！" : "未授权！");
                PermissionsManager
                        .getInstance()
                        .requestPermissionsIfNecessaryForResult(
                                MainActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                new PermissionsResultAction() {

                                    @Override
                                    public void onGranted() {
                                        toastMsg("授权完成！");
                                        Logger.d("授权完成！");
                                    }

                                    @Override
                                    public void onDenied(String permission) {
                                        toastMsg("授权失败！");
                                        Logger.d("授权失败！" + permission);
                                    }
                                });*/
            }
        });
/*        AppUpdateProvider.getInstance().initialize(HttpPublisher.getInstance(), "1003");
        AppUpdateProvider.getInstance().getUpdateInfo("http://payment.suiyizuche.com:8080/version/app/1003", new AppUpdateResultAction() {
            @Override
            public void noUpdate() {
                toastMsg("暂无更新");
            }

            @Override
            public void fail(int errCode, String errStr) {
                switch (errCode) {
                    case AppUpdateProvider.ERR_NULL:// 返回更新内容为空
                    case AppUpdateProvider.ERR_NETWORK:// 网络异常
                    case AppUpdateProvider.ERR_CANCLE:// 取消更新
                    case AppUpdateProvider.ERR_LATER:// 稍后更新
                        toastMsg(errStr);
                        break;
                }
            }
        });*/

//        this.getFragmentManager().beginTransaction()
//                .replace(R.id.frame_layout, Camera2Fragment.newInstance())
//                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
        JPushInterface.setAlias(this, "xiaohong", null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    @Subscriber(tag = "photo")
    public void onPhoto(Bitmap map) {
        UploadImageProvider.getInstance().uploadImage(map, "uploadImg", "55");
        UploadImageProvider.getInstance().cancleByTag("uploadImg");
        UploadImageProvider.getInstance().uploadImage(map, "uploadImg", "55");
    }

    @Subscriber(tag = "photo")
    public void onPhoto(final String img) {
//        UploadImageProvider.getInstance().uploadImage(img, "uploadImg", "66");
//        UploadImageProvider.getInstance().cancleByTag("uploadImg");
//        UploadImageProvider.getInstance().uploadImage(img, "uploadImg", "66");

/*        Observable.just(img).subscribeOn(Schedulers.newThread())
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
                });*/
/*        Observable.just(img).subscribeOn(Schedulers.newThread())
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

    @Override
    protected void back() {
        super.back();
        CustomToast.cancel();
    }
}
