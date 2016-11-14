package com.etong.android.frame.subscriber;

import org.simple.eventbus.EventBus;

public class Subscriber {
	protected EventBus mEventBus = EventBus.getDefault();
	protected String mClassName;

	protected EventBus getEventBus() {
		return mEventBus;
	}
}
