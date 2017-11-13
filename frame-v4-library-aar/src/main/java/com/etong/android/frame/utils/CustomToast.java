package com.etong.android.frame.utils;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.IntDef;
import android.text.TextUtils;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by zhouxiqing on 2016/11/7.
 */
public class CustomToast {
    private static Toast mToast;
    private static View layout;
    private static TextView textView;

    /**
     * Show the view or text notification for a short period of time.  This time
     * could be user-definable.  This is the default.
     *
     * @see Toast#LENGTH_SHORT
     */
    public static final int LENGTH_SHORT = Toast.LENGTH_SHORT;

    /**
     * Show the view or text notification for a long period of time.  This time
     * could be user-definable.
     *
     * @see Toast#LENGTH_LONG
     */
    public static final int LENGTH_LONG = Toast.LENGTH_LONG;

    /**
     * @param context
     * @param res     ID for an XML layout resource to load (e.g., R.layout.main_page)，文本显示在第一个TextView中，
     *                未找到TextView控件时使用默认布局（{@link com.android.internal.R.layout#transient_notification}）。
     *                注：深层布局请使用{@link #setLayoutRes(Context, int, int)}方法指定{@link TextView}控件id
     */
    public static void setLayoutRes(Context context, int res) {
        layout = null;
        textView = null;
        LayoutInflater inflate = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        try {
            layout = inflate.inflate(res, null);
        } catch (Resources.NotFoundException e) {
            setLayoutRes(context, Resources.getSystem().getIdentifier("transient_notification", "layout", "android"));
            return;
        }
        if (layout == null) {
            setLayoutRes(context, Resources.getSystem().getIdentifier("transient_notification", "layout", "android"));
            return;
        }
        for (int i = 0; i < ((ViewGroup) layout).getChildCount(); i++) {
            View view = ((ViewGroup) layout).getChildAt(i);
            if (view instanceof TextView) {
                textView = (TextView) view;
                break;
            }
            if (textView != null) {
                break;
            }
        }
        if (textView == null) {
            setLayoutRes(context, Resources.getSystem().getIdentifier("transient_notification", "layout", "android"));
            return;
        }
        if (mToast == null) {
            mToast = new Toast(context);
        }
        mToast.setView(layout);
    }

    /**
     * @param context The context to use.  Usually your {@link android.app.Application}
     *                or {@link android.app.Activity} object.
     * @param res     ID for an XML layout resource to load (e.g., R.layout.main_page)
     * @param id      ID for a TextView
     */
    public static void setLayoutRes(Context context, int res, int id) {
        layout = null;
        textView = null;
        LayoutInflater inflate = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        try {
            layout = inflate.inflate(res, null);
        } catch (InflateException e) {
            setLayoutRes(context, Resources.getSystem().getIdentifier("transient_notification", "layout", "android"));
            return;
        }
        if (layout == null) {
            setLayoutRes(context, Resources.getSystem().getIdentifier("transient_notification", "layout", "android"));
            return;
        }
        textView = (TextView) layout.findViewById(id);
        if (textView == null) {
            setLayoutRes(context, Resources.getSystem().getIdentifier("transient_notification", "layout", "android"));
            return;
        }
        if (mToast == null) {
            mToast = new Toast(context);
        }
        mToast.setView(layout);
    }

    /** @hide */
    @IntDef({Toast.LENGTH_SHORT, Toast.LENGTH_LONG})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Duration {

    }

    /**
     * @param context  The context to use.  Usually your {@link android.app.Application}
     *                 or {@link android.app.Activity} object.
     * @param text     The text to show.  Can be formatted text.
     * @param duration How long to display the message.  Either {@link Toast#LENGTH_SHORT} or
     *                 {@link Toast#LENGTH_LONG}
     */
    public static void showToast(Context context, String text, @Duration int duration) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        if (layout == null || textView == null) {

            Context appContext = context.getApplicationContext();
            setLayoutRes(appContext, Resources.getSystem().getIdentifier("transient_notification", "layout", "android"));
        }
        if (duration != LENGTH_SHORT && duration != LENGTH_LONG) {
            duration = LENGTH_SHORT;
        }
        mToast.setDuration(duration);
        textView.setText(text);
        mToast.show();
    }

    /**
     * @param context  The context to use.  Usually your {@link android.app.Application}
     *                 or {@link android.app.Activity} object.
     * @param resId    The resource id of the string resource to use.  Can be formatted text.
     * @param duration How long to display the message.  Either {@link Toast#LENGTH_SHORT} or
     *                 {@link Toast#LENGTH_LONG}
     * @throws Resources.NotFoundException if the resource can't be found.
     */
    public static void showToast(Context context, int resId, int duration) {
        Context appContext = context.getApplicationContext();
        showToast(appContext, appContext.getResources().getString(resId), duration);
    }

    /**
     * Hide display.
     */
    public static void cancel() {
        if (mToast != null) {
            mToast.cancel();
        }
    }

}
