package com.etong.android.frame.volley.request;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.etong.android.frame.utils.logger.Logger;

public class CookieRequest extends Request<JSONObject> {
	private Map<String, String> headers = new HashMap<String, String>(1);
	private Map<String, String> params = new HashMap<String, String>(1);
	private final Listener<JSONObject> listener;
	private static int SOCKET_TIMEOUT = 10000;
	private static String TAG = "CookieRequest";

	public CookieRequest(int method, String url, Listener<JSONObject> listener,
			ErrorListener eListener) {
		super(method, url, eListener);
		this.listener = listener;
		this.setRetryPolicy(new DefaultRetryPolicy(SOCKET_TIMEOUT,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
	}

	public void setCookie(String cookie) {
		headers.put("Cookie", cookie);
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	@Override
	public Map<String, String> getParams() throws AuthFailureError {
		return params;
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		return headers;
	}

	public void addHeader(String key, String value) {
		headers.put(key, value);
	}

	@Override
	protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
		try {
			String jsonString = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));

			String responseHeader = response.headers.toString();
			String cookieFromResponse = "";
//			Logger.t("LOG").d("Get headers in parseNetworkResponse "
//					+ response.headers.toString());
			// 使用正则表达式从reponse的头中提取cookie内容的子�?
			Pattern pattern = Pattern.compile("Set-Cookie.*?;");
			Matcher m = pattern.matcher(responseHeader);
			if (m.find()) {
				cookieFromResponse = m.group();
				Logger.t(TAG).w("cookie from server " + cookieFromResponse);
			}

			JSONObject dataJson = JSON.parseObject(jsonString);
			if (cookieFromResponse.length() > 2) {
				// 去掉cookie末尾
				cookieFromResponse = cookieFromResponse.substring(11,
						cookieFromResponse.length() - 1);
				Logger.t(TAG).w( "cookie substring " + cookieFromResponse);
				// 将cookie字符串添加到jsonObject中，该jsonObject会被deliverResponse递交，调用请求时则能在onResponse中得�?

				dataJson.put("Cookie", cookieFromResponse);
			}

//			Logger.t(TAG).json( dataJson.toJSONString());
			return Response.success(dataJson,
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JSONException je) {
			return Response.error(new ParseError(je));
		}
	}

	@Override
	protected void deliverResponse(JSONObject response) {
		listener.onResponse(response);
	}
}
