package com.etong.android.frame.utils;

public class ConfigInfo {
	Double earnest = 499.00;// 订金
	Double firstDerate=500.00;//活动,分享减价金额

	/** 获取订金金额 */
	public Double getEarnest() {
		return earnest;
	}

	/** 设置订金金额 */
	public void setEarnest(Double earnest) {
		this.earnest = earnest;
	}
	
	/** 获取活动,分享减价金额*/
	public Double getFirstDerate() {
		return firstDerate;
	}

	/** 设置活动,分享减价金额 */
	public void setFirstDerate(Double firstDerate) {
		this.firstDerate = firstDerate;
	}
}
