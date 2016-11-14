package com.etong.android.frame.utils;

import java.io.Serializable;

public class Storages implements Serializable {

	private static final long serialVersionUID = 1L;
	private String path;
	private Long freesize;
	private Long totalsize;

	public Storages() {
		path = null;
		freesize = totalsize = 0l;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path == null ? null : path.trim();
	}

	public Long getFreeSize() {
		return freesize;
	}

	public void setFreeSize(Long freesize) {
		this.freesize = freesize;
	}

	public Long getTotalSize() {
		return totalsize;
	}

	public void setTotalSize(Long totalsize) {
		this.totalsize = totalsize;
	}

}
