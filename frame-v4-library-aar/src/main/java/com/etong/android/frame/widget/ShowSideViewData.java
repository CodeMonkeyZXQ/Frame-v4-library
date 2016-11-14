package com.etong.android.frame.widget;

public class ShowSideViewData {
	Object data;
	String url;

	public ShowSideViewData() {
	}

	public ShowSideViewData(Object data, String url) {
		this.setData(data);
		this.setUrl(url);
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Object getData() {
		return data;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}
}
