package com.etong.android.frame.common;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.etong.android.frame.R;

/**
* @ClassName    : LoadingDialog 
* @Description  : 自定义等待对话框 
* @author       : zhouxiqing
* @date         : 2016-3-21 下午3:26:13 
*
 */
public class LoadingDialog extends Dialog {
	Context context;
	ImageView image; // 圆型进度条
	Animation anim;// 动画
	TextView progress;
	TextView prompt;
	int counter = 5;
	String tip=null;

	/* 定义一个倒计时的内部类 */
	class TimeCounter extends CountDownTimer {

		public TimeCounter(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发
			LoadingDialog.this.hide();
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			long finishSeconds = millisUntilFinished / 1000;

			if (0 == finishSeconds) {
				LoadingDialog.this.prompt.setText("加载数据超时");
			}
			LoadingDialog.this.progress
					.setText(millisUntilFinished / 1000 + "");
		}
	}

	public LoadingDialog(Context context) {
		super(context, R.style.dialog_trans);
		this.context = context;
	}

	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		requestWindowFeature(Window.PROGRESS_VISIBILITY_ON);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_toast);
		setAnimation();

		setCanceledOnTouchOutside(false);
		this.setCancelable(false);
	}

	protected void setAnimation() {
		anim = AnimationUtils.loadAnimation(this.getContext(), R.anim.loading);
		anim.setInterpolator(new LinearInterpolator());

		image = (ImageView) this.findViewById(R.id.dialog_loading_animation);
		progress = (TextView) this.findViewById(R.id.dialog_loading_progress);
		prompt = (TextView) this.findViewById(R.id.dialog_loading_prompt);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 按下了键盘上返回按钮
			// this.hide();
			return true;
		}
		return false;
	}

	public void show() {
		super.show();
		if(tip==null||tip.isEmpty()){
			prompt.setText("数据加载中");
		}else{
			prompt.setText(tip);
		}
		if (counter != 0) {
			progress.setText(counter + "");
			TimeCounter timeCounter = new TimeCounter(counter * 1000, 1 * 1000);
			timeCounter.start();
		}else{
			progress.setText("");
		}
		image.startAnimation(anim);
	}

	/**
	* @Title        : setTip 
	* @Description  : 对显示的加载提示 进行编辑
	* @params 
	*     @param tip	显示文本
	*     @param time	自动消失时间，0表示不自动消失
	* @return 
	*     void    返回类型 
	 */
	public void setTip(String tip, int time) {
		this.tip=tip;
		counter = time;
	}
}
