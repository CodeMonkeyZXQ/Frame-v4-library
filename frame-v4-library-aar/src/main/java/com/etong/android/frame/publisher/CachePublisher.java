package com.etong.android.frame.publisher;

import com.alibaba.fastjson.JSONObject;
import com.etong.android.frame.event.CommonEvent;

/**
 * @ClassName : CachePublisher
 * @Description : 缓存事件发布者
 * @author : yuanjie
 * @date : 2015-9-2 上午10:16:15
 * 
 */
public class CachePublisher extends Publisher {
	JSONObject mCacheJson = new JSONObject();

	public class Cache {
		String key;
		Object val;

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public Object getVal() {
			return val;
		}

		public void setVal(Object val) {
			this.val = val;
		}

		public Cache(String key, Object o) {
			this.key = key;
			this.val = o;
		}
	}

	public void put(String key, Object o) {
		this.put(key, o, CommonEvent.CACHE);
	}

	public void put(String key, Object o, String tag) {
		mCacheJson.put(key, o);
		getEventBus().post(new Cache(key, o), tag);
	}

	public Object getCache(String key) {
		return mCacheJson.get(key);
	}

	public <T> T getCache(String key, Class<T> clazz) {
		return mCacheJson.getObject(key, clazz);
	}
}
