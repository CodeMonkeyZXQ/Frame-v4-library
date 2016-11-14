package com.etong.android.frame.publisher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;

@SuppressWarnings("rawtypes")
public class HttpMethod {

	public interface EnumHandler<T> {
		public void onEnum(T data);
	}

	private String url = null;
	private Map param = null;
	private JSONObject data = null;
	private VolleyError error;

	public HttpMethod(String url, Map param) {
		this.url = url;
		this.param = param;
	};

	public String getUrl() {
		return url;
	}

	public Map getParam() {
		return param;
	}

	public JSONObject data() {
		return data;
	}

	public HttpMethod put(JSONObject data) {
		this.data = data;
		return this;
	}

	public VolleyError error() {
		return error;
	}

	public HttpMethod error(VolleyError e) {
		this.error = e;
		return this;
	}

	public String toJSONString() {
		return JSON.toJSONString(this);
	}

	public JSONObject getJson(String key) {
		return data().getJSONObject(key);
	}

	public <T> T get(String key, Class<T> clazz) {
		return data().getObject(key, clazz);
	}

	public <T> T get(String key, String key2, Class<T> clazz, T def) {
		JSONObject keyJson = data().getJSONObject(key);
		if (null != keyJson) {
			return keyJson.getObject(key2, clazz);
		}
		return def;
	}

	public JSONArray getArray(String key) {
		return data().getJSONArray(key);
	}

	public <T> List<T> getList(String key, Class<T> clazz) {
		List<T> list = new ArrayList<T>();
		JSONArray array = data().getJSONArray(key);
		if (null != array) {
			for (Object o : array) {
				list.add(JSON.toJavaObject((JSONObject) o, clazz));
			}
		}
		return list;
	}

	public <T> List<T> getList(String key1, String key2, Class<T> clazz) {
		List<T> list = new ArrayList<T>();
		JSONObject dataJson = data().getJSONObject(key1);
		if (null != dataJson) {
			JSONArray array = dataJson.getJSONArray(key2);
			if (null != array) {
				for (Object o : array) {
					list.add(JSON.toJavaObject((JSONObject) o, clazz));
				}
			}
		}
		return list;
	}

	/**
	 * @param
	 * @Title : enumerate
	 * @Description : 枚举，用于将一个JSONArray转换成指定对象通过hanlder回调对调用方
	 * @params 设定文件
	 * @return void 返回类型
	 */
	@SuppressWarnings("unchecked")
	public <T> void enumerate(String key, EnumHandler<T> handler, Class<T> clazz) {
		JSONArray array = data().getJSONArray(key);
		if (null != array && null != handler) {
			for (Object o : array) {
				handler.onEnum((T) o);
			}
		}
	}

	public boolean check(String key, String checked) {
		return checked.equals(data().getString(key));
	}

	public boolean check(String key, Integer checked) {
		return checked.equals(data().getInteger(key));
	}
}