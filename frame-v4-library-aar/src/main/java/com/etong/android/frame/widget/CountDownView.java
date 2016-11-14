package com.etong.android.frame.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.etong.android.frame.R;

/**
 * @ClassName : CountDownView
 * @Description : 自定义倒计时View,带背景
 * @author : zhouxiqing
 * @date : 2015-11-27 下午4:08:42
 * 
 */
@SuppressLint("CutPasteId")
public class CountDownView extends FrameLayout {
	public CountDownTimer timer = null;
	private TextView[] mViews = new TextView[8];
	public static final int DAY = 86400;
	public static final int HOUR = 3600;
	public static final int MIN = 60;

	public CountDownView(Context context) {
		super(context);

	}

	public CountDownView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 在构造函数中将Xml中定义的布局解析出来。
		LayoutInflater.from(context).inflate(R.layout.layout_countdown_time,
				this, true);
		mViews[0] = (TextView) findViewById(R.id.time_day1);
		mViews[1] = (TextView) findViewById(R.id.time_day2);
		mViews[2] = (TextView) findViewById(R.id.time_hour1);
		mViews[3] = (TextView) findViewById(R.id.time_hour2);
		mViews[4] = (TextView) findViewById(R.id.time_min1);
		mViews[5] = (TextView) findViewById(R.id.time_min2);
	}

	/**
	 * @Title : setCountDown
	 * @Description : 设置倒计时数据并开始计时
	 * @params
	 * @param millisInFuture
	 *            倒计时时长总毫秒数
	 * @param countDownInterval
	 *            时间间隔毫秒数
	 * @return void 返回类型
	 */
	public void startCountDown(long millisInFuture, long countDownInterval) {
		start(millisInFuture, countDownInterval);
	}

	private void start(long millisInFuture, long countDownInterval) {
		if (timer != null) {
			timer.cancel();
		}
		timer = new CountDownTimer(millisInFuture, countDownInterval) {

			// millisUntilFinished 倒计时剩余时间
			@Override
			public void onTick(long millisUntilFinished) {
				setView(millisUntilFinished);
			}

			@Override
			public void onFinish() {
				setView(0);
			}
		};
		timer.start();
	}

	protected void setView(long millisUntilFinished) {
		int secons = Integer.valueOf("" + millisUntilFinished / 1000);
		int day = Integer.valueOf(secons / DAY + "");
		int hour = Integer.valueOf(secons % DAY / HOUR + "");
		int min = Integer.valueOf(secons % HOUR / MIN + "");

		mViews[0].setText(day + "");
		mViews[1].setText("天");
		mViews[2].setText(hour / 10 + "");
		mViews[3].setText(hour % 10 + "");
		mViews[4].setText(min / 10 + "");
		mViews[5].setText(min % 10 + "");
	}
}
