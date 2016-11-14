package com.etong.android.frame.publisher;

import org.simple.eventbus.EventBus;

/**
 * @ClassName : Publisher
 * @Description : ��Ϣ�����ߣ�������Ϣ�ṩ����Ӧ�ü̳д��࣬��Ϣ֪ͨ��Ϣ�Żᱻsubscriber���յ�
 * @author : yuanjie
 * @date : 2015-9-1 ����4:34:45
 * 
 */
abstract public class Publisher {
	protected EventBus mEventBus = EventBus.getDefault();

	public EventBus getEventBus() {
		return mEventBus;
	}
}
