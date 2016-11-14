package com.etong.android.frame.utils;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.etong.android.frame.BaseApplication;
import com.etong.android.frame.R;

/**
 * @ClassName : CToast
 * @Description : 自定义时长的Toast，已废除，请使用{@link CustomToast}
 * @author : zhouxiqing
 * @date : 2015-11-11 下午2:48:57
 */
@Deprecated
public class CToast {
	private static CToast mCToast;

	/**
	 * @Title : getInstance
	 * @Description : 获取自定义Toast实例
	 * @return CToast 返回类型
	 */
	@Deprecated
	public static CToast getInstance() {
		if (mCToast == null) {
			mCToast = new CToast(BaseApplication.getApplication());
		}
		return mCToast;
	}

	/**
	 * 自定义Toast
	 * 
	 * @param message
	 *            :消息字符串
	 * @param T
	 *            :消息显示时间 0:Toast.LENGTH_SHORT; 1 Toast.LENGTH_LONG;其它值为自定义时长
	 */
	@Deprecated
	public static void toastMessage(String message, int T) {
		CustomToast.showToast(BaseApplication.getApplication(), message,T==0? Toast.LENGTH_SHORT:Toast.LENGTH_LONG);
//		int time = (T == 0) ? CToast.LENGTH_SHORT
//				: ((T == 1) ? CToast.LENGTH_LONG : T);
//		if (null != mCToast) {
//			mCToast.hide();
//		}
//		mCToast = CToast.makeText(BaseApplication.getApplication(), message,
//				time);
//		mCToast.show();
	}

	private static CToast makeText(Context context, CharSequence text,
			int duration) {
		CToast result = new CToast(context);

		// 通过系统提供的实例获得一个LayoutInflater对象
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// 第一个参数为xml文件中view的id，第二个参数为此view的父组件，可以为null，android会自动寻找它是否拥有父组件
		View mLayout = inflater.inflate(R.layout.toast, null);
		TextView tv = (TextView) mLayout
				.findViewById(R.id.dialog_loading_prompt);
		tv.setText(text);
		result.mNextView = mLayout;
		result.mDuration = duration;

		return result;
	}

	private static final int LENGTH_SHORT = 2000;
	private static final int LENGTH_LONG = 3500;

	private final Handler mHandler = new Handler();
	private int mDuration = LENGTH_SHORT;
	private int mGravity = Gravity.CENTER;
	private int mX, mY;
	private float mHorizontalMargin;
	private float mVerticalMargin;
	private View mView;
	private View mNextView;

	private WindowManager mWM;
	private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();

	public CToast(Context context) {
		init(context);
	}

	/**
	 * Set the view to show.
	 * 
	 * @see #getView
	 */
	public void setView(View view) {
		mNextView = view;
	}

	/**
	 * Return the view.
	 * 
	 * @see #setView
	 */
	public View getView() {
		return mNextView;
	}

	/**
	 * Set how long to show the view for.
	 * 
	 * @see #LENGTH_SHORT
	 * @see #LENGTH_LONG
	 */
	public void setDuration(int duration) {
		mDuration = duration;
	}

	/**
	 * Return the duration.
	 * 
	 * @see #setDuration
	 */
	public int getDuration() {
		return mDuration;
	}

	/**
	 * Set the margins of the view.
	 * 
	 * @param horizontalMargin
	 *            The horizontal margin, in percentage of the container width,
	 *            between the container's edges and the notification
	 * @param verticalMargin
	 *            The vertical margin, in percentage of the container height,
	 *            between the container's edges and the notification
	 */
	public void setMargin(float horizontalMargin, float verticalMargin) {
		mHorizontalMargin = horizontalMargin;
		mVerticalMargin = verticalMargin;
	}

	/**
	 * Return the horizontal margin.
	 */
	public float getHorizontalMargin() {
		return mHorizontalMargin;
	}

	/**
	 * Return the vertical margin.
	 */
	public float getVerticalMargin() {
		return mVerticalMargin;
	}

	/**
	 * Set the location at which the notification should appear on the screen.
	 * 
	 * @see android.view.Gravity
	 * @see #getGravity
	 */
	public void setGravity(int gravity, int xOffset, int yOffset) {
		mGravity = gravity;
		mX = xOffset;
		mY = yOffset;
	}

	/**
	 * Get the location at which the notification should appear on the screen.
	 * 
	 * @see android.view.Gravity
	 * @see #getGravity
	 */
	public int getGravity() {
		return mGravity;
	}

	/**
	 * Return the X offset in pixels to apply to the gravity's location.
	 */
	public int getXOffset() {
		return mX;
	}

	/**
	 * Return the Y offset in pixels to apply to the gravity's location.
	 */
	public int getYOffset() {
		return mY;
	}

	/**
	 * schedule handleShow into the right thread
	 */
	public void show() {
		mHandler.post(mShow);

		if (mDuration > 0) {
			mHandler.postDelayed(mHide, mDuration);
		}
	}

	/**
	 * schedule handleHide into the right thread
	 */
	public void hide() {
		mHandler.post(mHide);
	}

	private final Runnable mShow = new Runnable() {
		public void run() {
			handleShow();
		}
	};

	private final Runnable mHide = new Runnable() {
		public void run() {
			handleHide();
		}
	};

	private void init(Context context) {
		final WindowManager.LayoutParams params = mParams;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		params.windowAnimations = android.R.style.Animation_Toast;
		params.type = WindowManager.LayoutParams.TYPE_TOAST;
		params.setTitle("Toast");

		mWM = (WindowManager) context.getApplicationContext().getSystemService(
				Context.WINDOW_SERVICE);
	}

	private void handleShow() {

		if (mView != mNextView) {
			// remove the old view if necessary
			handleHide();
			mView = mNextView;
			// mWM = WindowManagerImpl.getDefault();
			final int gravity = mGravity;
			mParams.gravity = gravity;
			if ((gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.FILL_HORIZONTAL) {
				mParams.horizontalWeight = 1.0f;
			}
			if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.FILL_VERTICAL) {
				mParams.verticalWeight = 1.0f;
			}
			mParams.x = mX;
			mParams.y = mY;
			mParams.verticalMargin = 0.4f;// mVerticalMargin;
			mParams.horizontalMargin = mHorizontalMargin;
			if (mView.getParent() != null) {
				mWM.removeView(mView);
			}
			mWM.addView(mView, mParams);
		}
	}

	private void handleHide() {
		if (mView != null) {
			if (mView.getParent() != null) {
				mWM.removeView(mView);
			}
			mView = null;
		}
	}
}
