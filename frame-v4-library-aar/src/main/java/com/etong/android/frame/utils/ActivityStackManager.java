package com.etong.android.frame.utils;

import java.util.Stack;

import com.etong.android.frame.subscriber.BaseSubscriberActivity;

import android.app.Activity;
import android.content.Context;

/**
 * @ClassName : ActivityStackManager
 * @Description : TODO(应用程序Activity管理类)
 * @author : zhouxiqing
 * @date : 2015-10-15 下午1:44:36
 * 
 */
public class ActivityStackManager {
	 private static Stack<BaseSubscriberActivity> activityStack;
	 private static final ActivityStackManager instance = new ActivityStackManager();
	 
	 private ActivityStackManager() {}

	 public static ActivityStackManager create() {
	      return instance;
	 }

	    /**
	     * 获取当前Activity栈中元素个数
	     */
	    public int getCount() {
	        return activityStack.size();
	    }

	    /**
	     * 添加Activity到栈
	     */
	    public void addActivity(BaseSubscriberActivity activity) {
	        if (activityStack == null) {
	            activityStack = new Stack<BaseSubscriberActivity>();
	        }
	        activityStack.add(activity);
	    }

	    /**
	     * 获取当前Activity（栈顶Activity）
	     */
	    public Activity topActivity() {
	    	if (activityStack == null||activityStack.isEmpty()) {
	    		return null;
	        }
	        BaseSubscriberActivity activity = activityStack.lastElement();
	        return (Activity) activity;
	    }

	    /**
	     * 获取当前Activity（栈顶Activity） 没有找到则返回null
	     */
	    public Activity findActivity(Class<?> cls) {
	        BaseSubscriberActivity activity = null;
	    	if (activityStack == null||activityStack.isEmpty()) {
	    		return null;
	        }
	        for (BaseSubscriberActivity aty : activityStack) {
	            if (aty.getClass().equals(cls)) {
	                activity = aty;
	                break;
	            }
	        }
	        return (Activity) activity;
	    }

	    /**
	     * 结束当前Activity（栈顶Activity）
	     */
	    public void finishActivity() {
	    	if (activityStack == null||activityStack.isEmpty()) {
	    		return;
	        }
	        BaseSubscriberActivity activity = activityStack.lastElement();
	        finishActivity((Activity) activity);
	    }

	    /**
	     * 结束指定的Activity(重载)
	     */
	    public void finishActivity(Activity activity) {
	        if (activity != null) {
	            activityStack.remove(activity);
	            //activity.finish();//此处不用finish
	            activity = null;
	        }
	    }

	    /**
	     * 结束指定的Activity(重载)
	     */
	    public void finishActivity(Class<?> cls) {
	    	if (activityStack == null||activityStack.isEmpty()) {
	    		return;
	        }
	        for (BaseSubscriberActivity activity : activityStack) {
	            if (activity.getClass().equals(cls)) {
	                finishActivity((Activity) activity);
	            }
	        }
	    }

	    /**
	     * 关闭除了指定activity以外的全部activity 如果cls不存在于栈中，则栈全部清空
	     * 
	     * @param cls
	     */
	    public void finishOthersActivity(Class<?> cls) {
	    	if (activityStack == null||activityStack.isEmpty()) {
	    		return;
	        }
	        for (BaseSubscriberActivity activity : activityStack) {
	            if (!(activity.getClass().equals(cls))) {
	                finishActivity((Activity) activity);
	            }
	        }
	    }

	    /**
	     * 结束所有Activity
	     */
	    public void finishAllActivity() {
	    	if (activityStack == null||activityStack.isEmpty()) {
	    		return;
	        }
	        for (int i = 0, size = activityStack.size(); i < size; i++) {
	            if (null != activityStack.get(i)) {
	                ((Activity) activityStack.get(i)).finish();
	            }
	        }
	        activityStack.clear();
	    }

	    /**
	     * 应用程序退出
	     * 
	     */
	    public void AppExit(Context context) {
	        try {
	            finishAllActivity();
	            Runtime.getRuntime().exit(0);
	        } catch (Exception e) {
	            Runtime.getRuntime().exit(-1);
	        }
	    }

}
