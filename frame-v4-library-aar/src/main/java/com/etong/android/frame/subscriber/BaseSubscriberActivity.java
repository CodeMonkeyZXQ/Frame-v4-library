package com.etong.android.frame.subscriber;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;

import com.etong.android.frame.BaseApplication;
import com.etong.android.frame.common.LoadingDialog;
import com.etong.android.frame.permissions.PermissionsManager;
import com.etong.android.frame.utils.ActivityStackManager;
import com.etong.android.frame.utils.CustomToast;
import com.pgyersdk.feedback.PgyFeedbackShakeManager;
import com.pgyersdk.update.PgyUpdateManager;

import org.simple.eventbus.EventBus;

import java.util.List;

/**
 * @author : zhouxiqing
 * @ClassName : BaseSubscriberActivity
 * @Description : Activity抽象基类</br> 初始化EventBus,LoadingDialog，自定义消息显示等
 * @date : 2016-3-21 下午3:32:31
 */
abstract public class BaseSubscriberActivity extends AppCompatActivity {
    protected EventBus mEventBus = EventBus.getDefault();
    private LoadingDialog mLoading = null;

    protected float mDensity;
    protected int mWidth;
    protected int mHeight;

    private OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View arg0) {
            BaseSubscriberActivity.this.onClick(arg0);
        }
    };

    /**
     * @return EventBus EventBus实例
     * @Title : getEventBus
     * @Description : 获取EventBus实例
     */
    protected EventBus getEventBus() {
        return mEventBus;
    }

    /**
     * @return void 返回类型
     * @Title : registerSticky
     * @Description : 静态注册EventBus
     * @params 设定文件
     */
    protected void registerSticky() {
        mEventBus.registerSticky(this);
    }

    /**
     * @return void 返回类型
     * @Title : back
     * @Description : back ,finish this activity
     * @params 设定文件
     */
    protected void back() {
        finish();
    }

    /**
     * @param msg 消息内容
     * @return void 返回类型
     * @Title : toastMsg
     * @Description : 自定义消息显示
     * @params
     */
    protected void toastMsg(final String msg) {
        CustomToast.showToast(this, msg, CustomToast.LENGTH_SHORT);
    }

    /**
     * @param msg   消息内容
     * @param errno 错误内容，当{@link BaseApplication#isDebug}为true时会显示在消息中
     * @return void 返回类型
     * @Title : toastMsg
     * @Description : 自定义消息显示
     * @params
     */
    protected void toastMsg(String msg, String errno) {
        if (BaseApplication.isDebug)
            toastMsg(msg + ":" + errno);
        else
            toastMsg(msg);
    }

    /**
     * @param err 错误代码，当{@link BaseApplication#isDebug}为true时会显示在消息中
     * @param msg 消息内容
     * @return void 返回类型
     * @Title : toastMsg
     * @Description : 自定义消息显示
     * @params
     */
    protected void toastMsg(int err, String msg) {
        if (BaseApplication.isDebug) {
            toastMsg(msg + "(" + err + ")");
        } else {
            toastMsg(msg);
        }
    }

    /**
     * @param view View
     * @return void 返回类型
     * @Title : addClickListener
     * @Description : 添加点击事件
     * @params
     */
    protected void addClickListener(View view) {
        if (null != view)
            view.setOnClickListener(mClickListener);
    }

    /**
     * @param id id
     * @return View 返回类型
     * @Title : addClickListener
     * @Description : 添加点击事件
     * @params
     */
    protected View addClickListener(int id) {
        View view = findViewById(id);
        addClickListener(view);
        return view;
    }

    /**
     * @param views View's
     * @return void 返回类型
     * @Title : addClickListener
     * @Description : 添加点击事件
     * @params
     */
    protected void addClickListener(View[] views) {
        for (View view : views) {
            addClickListener(view);
        }
    }

    /**
     * @param views View's
     * @return void 返回类型
     * @Title : addClickListener
     * @Description : 添加点击事件
     * @params
     */
    protected void addClickListener(List<View> views) {
        for (View view : views) {
            addClickListener(view);
        }
    }

    @SuppressWarnings("unchecked")
    protected <T> T findViewById(int viewId, Class<T> clazz) {
        return (T) super.findViewById(viewId);
    }

    @SuppressWarnings("unchecked")
    protected <T> T findViewById(View parent, int viewId, Class<T> clazz) {
        return (T) parent.findViewById(viewId);
    }

    @Override
    final protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActivityStackManager.create().addActivity(this);
        mEventBus.register(this);

//		this.requestWindowFeature(Window.FEATURE_NO_TITLE);//不适用于AppCompatActivity

//		ActionBar actionBar = this.getSupportActionBar();
//		if (actionBar!=null){
//			actionBar.hide();
//		}

        mLoading = new LoadingDialog(this);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mDensity = dm.density;
        mWidth = dm.widthPixels;
        mHeight = dm.heightPixels;

        onInit(savedInstanceState);
    }

    protected void loadStart() {
        mLoading.show();
    }

    /**
     * @param tip  显示的标题
     *             (null为使用默认标题)
     * @param time 显示倒计时时间(秒)(0为不显示倒计时)
     * @return void 返回类型
     * @Title loadStart
     * @Description 显示加载Dialog
     * @params
     */
    protected void loadStart(String tip, int time) {
        mLoading.setTip(tip, time);
        mLoading.show();
    }

    /**
     * @return void 返回类型
     * @Title : loadFinish
     * @Description : 隐藏加载Dialog
     * @params 设定文件
     */
    protected void loadFinish() {
        mLoading.hide();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (BaseApplication.isDebug) {
            //蒲公英-摇一摇反馈功能注销
            PgyFeedbackShakeManager.unregister();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (BaseApplication.isDebug) {
            //蒲公英-摇一摇反馈功能注册
            // 自定义摇一摇的灵敏度，默认为950，数值越小灵敏度越高。
            PgyFeedbackShakeManager.setShakingThreshold(1000);
            // 以对话框的形式弹出
            PgyFeedbackShakeManager.register(this);
            if (BaseApplication.pgyUpdate) {
                PgyUpdateManager.register(this);
                BaseApplication.pgyUpdate = false;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        back();
        ActivityStackManager.create().finishActivity(this);
        mEventBus.unregister(this);
        mLoading.dismiss();
    }

    protected void onClick(View view) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions,
                grantResults);
    }

    abstract protected void onInit(@Nullable Bundle savedInstanceState);
}
