package com.etong.android.frame.widget;

import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.widget.TextView;

import com.etong.android.frame.utils.logger.Logger;

/**
 * @ClassName : TimerTextView
 * @Description : 自定义倒计时TextView
 * @author : zhouxiqing
 * @date : 2015-11-27 下午4:07:50
 * 
 */
@SuppressLint({ "CutPasteId", "SimpleDateFormat" })
public class TimerTextView extends TextView {
	private static SimpleDateFormat format_dhms = new SimpleDateFormat(
			"dd天HH时mm分ss秒");
	private CountDownTimer timer = null;
	private long millisInFuture;
	private String formatString = "dd天HH时mm分ss秒";
	private static String EndString = "00天00时00分00秒";
	public static final int DAY = 86400;
	public static final int HOUR = 3600;
	public static final int MIN = 60;

	public TimerTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @Title : startCountDown
	 * @Description : 设置倒计时数据并开始计时,显示为默认格式"dd天HH小时mm分ss秒"
	 * @params
	 * @param millisInFuture
	 *            倒计时时长总毫秒数
	 * @return void 返回类型
	 */
	public void startCountDown(long millisInFuture) {
		startCountDown(millisInFuture, "dd天HH小时mm分ss秒", "00天00小时00分00秒");
	}

	/**
	 * @Title : setCountDown
	 * @Description : 设置倒计时数据并开始计时
	 * @params
	 * @param millisInFuture
	 *            倒计时时长总毫秒数
	 * @param format
	 *            显示格式,默认为"dd天HH小时mm分ss秒"
	 * @return void 返回类型
	 */
	public void startCountDown(long millisInFuture, String format, String end) {
		EndString = end;
		this.millisInFuture = millisInFuture;
		try {
			SimpleDateFormat f = new SimpleDateFormat(format);
			format_dhms = f;
			formatString = format;
		} catch (Exception e) {
			Logger.e(e,
					"TimerTextView error:format string is null or not considered to be usable");
		}
		start();
	}

	private void start() {
		if (millisInFuture <= 0) {
			setView();
			return;
		}
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		timer = new CountDownTimer(millisInFuture, 1000) {

			@Override
			public void onTick(long millisUntilFinished) {
				setView();
			}

			@Override
			public void onFinish() {
				setView();
			}
		};
		timer.start();
	}

	protected void setView() {
		millisInFuture--;
		if (millisInFuture <= 0) {
			this.setText(EndString);
		} else {
			int secons = Integer.valueOf("" + millisInFuture / 1000);
			int day = Integer.valueOf(secons / DAY + "");
			int hour = Integer.valueOf(secons % DAY / HOUR + "");
			int min = Integer.valueOf(secons % HOUR / MIN + "");
			int sec = Integer.valueOf(secons % MIN + "");

			String text = format_dhms.toPattern();
			text = text.replaceAll("dd", "d");
			text = text.replaceAll("HH", "H");
			text = text.replaceAll("mm", "m");
			text = text.replaceAll("ss", "s");
			if (!formatString.contains("d")) {
				hour += day * 24;
			}
			text = text.replaceAll("d", day + "");
			text = text.replaceAll("H", (hour) + "");
			text = text.replaceAll("m", min + "");
			text = text.replaceAll("s", sec + "");
			this.setText(text);
		}
	}
}
