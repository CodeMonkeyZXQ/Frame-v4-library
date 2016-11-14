package com.etong.android.frame.share;

import android.graphics.Bitmap;

/**
 * @ClassName : ShareInfo
 * @Description : 微信分享
 * @author : zhouxiqing
 * @date : 2016-3-16 上午10:45:44
 */
public class ShareInfo {
	String title;// 标题
	String text;// 文本内容
	String imagePath;// 图片路径
	Bitmap imageBitmap;// 图片
	String imageUrl;// 图片地址
	String url;// 网页地址
	Long Id;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public Bitmap getImageBitmap() {
		return imageBitmap;
	}

	public void setImageBitmap(Bitmap imageData) {
		this.imageBitmap = imageData;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Long getId() {
		return Id;
	}

	public void setId(Long Id) {
		this.Id = Id;
	}
}
