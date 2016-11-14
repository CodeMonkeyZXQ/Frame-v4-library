package com.etong.android.frame.publisher;

import com.etong.android.frame.event.CommonEvent;

/**
 * @ClassName : CookiePublisher
 * @Description : Cookie事件发布者
 * @author : yuanjie
 * @date : 2015-9-2 上午10:16:40
 * 
 */
public class CookiePublisher extends Publisher {
	String mCookie = "";

	public void setCookie(String cookie) {
		mCookie = cookie;
		getEventBus().post(mCookie, CommonEvent.COOKIE);
	}
}
