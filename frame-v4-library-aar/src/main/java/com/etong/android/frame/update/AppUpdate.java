package com.etong.android.frame.update;

public class AppUpdate {
	private String title;
	private String appName;
	private Float versionCode;
	private String versionName;
	private String url;
	private String completeUrl;
	private String comments;
	private Double size = 0.0;
	private String oldMd5;
	private String newMd5;
	private boolean install = false;
	private boolean ispatch = false;

	private String oldApkSource;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public Float getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(Float versionCode) {
		this.versionCode = versionCode;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCompleteUrl() {
		return completeUrl==null?url:completeUrl;
	}

	public void setCompleteUrl(String completeUrl) {
		this.completeUrl = completeUrl;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Double getSize() {
		return size;
	}

	public void setSize(Double size) {
		this.size = size;
	}

	public String getOldMd5() {
		return oldMd5;
	}

	public void setOldMd5(String oldMd5) {
		this.oldMd5 = oldMd5;
	}

	public String getNewMd5() {
		return newMd5;
	}

	public void setNewMd5(String newMd5) {
		this.newMd5 = newMd5;
	}

	public boolean isInstall() {
		return install;
	}

	public void setInstall(boolean install) {
		this.install = install;
	}

	public boolean isIspatch() {
		return ispatch;
	}

	public void setIspatch(boolean ispatch) {
		this.ispatch = ispatch;
	}

	public String getOldApkSource() {
		return oldApkSource;
	}

	public void setOldApkSource(String oldApkSource) {
		this.oldApkSource = oldApkSource;
	}

}
