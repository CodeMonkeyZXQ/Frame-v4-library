package com.etong.android.frame.utils;

import org.simple.eventbus.EventBus;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.media.ThumbnailUtils;
import android.view.View;
import android.widget.ImageView;

import com.etong.android.frame.BaseApplication;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import java.security.PrivateKey;

/**
 * @ClassName : ImageProvider
 * @Description : 图片加载</br> 框架中已在{@link BaseApplication}中进行了初始化</br> 调用
 *              {@link #loadImage(ImageView, String)}，
 *              {@link #loadImage(ImageView, String, String)}方法进行图片加载</br>
 *              图片会显示在传入的ImageView中</br>
 *              当传入Tag参数后，加载完成后会通过EventBus进行结果调用，事件类型分为String及Bitmap两种。
 * @author : zhouxiqing
 * @date : 2016-3-21 下午3:13:24
 * 
 */
public class ImageProvider {
	/** 参数中含有空值 */
	public static final String HAVE_NULL_VALUES = "have a null values";
	/** 加载图片失败 */
	public static final String LOAD_IMAGE_FAIL = "load image fail";
	/** 加载图片取消 */
	public static final String LOAD_IMAGE_CANCEL = "load image cancel";
	/** 加载图片成功 */
	public static final String LOAD_IMAGE_COMPLETE = "load image complete";

	@SuppressWarnings("unused")
	private static Bitmap bmp = null;

	private static class Holder{
		private static final ImageProvider INSTANCE = new ImageProvider();
	}
	private ImageProvider() {
		
	}
	
	static public ImageProvider getInstance() {
		return Holder.INSTANCE;
	}

	private ImageLoader mImageLoader = null;

	public void initialize(Context context) {
		mImageLoader = ImageLoader.getInstance();
		mImageLoader.init(ImageLoaderConfiguration.createDefault(context));
	}

	public ImageLoader getImageLoader() {
		return mImageLoader;
	}

	/**
	 * @Title : loadImage
	 * @Description : 加载图片
	 * @params
	 * @param image
	 *            用来显示图片的ImageView
	 * @param url
	 *            图片地址
	 * @return void 返回类型
	 */
	public void loadImage(ImageView image, String url) {
		loadImage(image, url, null);
	}

	/**
	 * @Title : loadImage
	 * @Description : 加载图片
	 * @params
	 * @param image
	 *            用来显示图片的ImageView
	 * @param url
	 *            图片地址
	 * @param tag
	 *            EventBus结果回标签
	 * @return void 返回类型
	 */
	public void loadImage(ImageView image, String url, final String tag) {
		loadImage(image, url, 0, tag);
	}

	/**
	 * @Title : loadImage
	 * @Description : 加载图片
	 * @params
	 * @param image
	 *            用来显示图片的ImageView
	 * @param url
	 *            图片地址
	 * @param default_image
	 *            默认图片
	 * @return void 返回类型
	 */
	public void loadImage(ImageView image, String url, int default_image) {
		loadImage(image, url, default_image, null);
	}

	/**
	 * @Title : loadImage
	 * @Description : 加载图片
	 * @params
	 * @param image
	 *            用来显示图片的ImageView
	 * @param url
	 *            图片地址
	 * @param default_image
	 *            默认图片
	 * @param tag
	 *            EventBus结果回标签
	 * @return void 返回类型
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public void loadImage(ImageView image, String url, int default_image,
			final String tag) {

		if (null == image || null == url || url.isEmpty()) {
			if (tag != null)
				EventBus.getDefault().post(HAVE_NULL_VALUES, tag);
			return;
		}

		image.setBackgroundDrawable(null);

		DisplayImageOptions options;
		if (default_image != 0) {
			options = new DisplayImageOptions.Builder().cacheInMemory(true)
					.cacheOnDisk(true).considerExifParams(true)
					.showImageForEmptyUri(default_image)
					.showImageOnLoading(default_image)
					.showImageOnFail(default_image).build();
		} else {
			options = new DisplayImageOptions.Builder().cacheInMemory(true)
					.cacheOnDisk(true).considerExifParams(true).build();
		}

		mImageLoader.displayImage(url, image, options,
				new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String imageUri, View view) {
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						if (tag != null)
							EventBus.getDefault()
									.post(LOAD_IMAGE_COMPLETE, tag);
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {
						if (tag != null)
							EventBus.getDefault().post(LOAD_IMAGE_CANCEL, tag);
					}

					@Override
					public void onLoadingFailed(String arg0, View arg1,
							FailReason arg2) {
						if (tag != null)
							EventBus.getDefault().post(LOAD_IMAGE_FAIL, tag);
					}

				}, new ImageLoadingProgressListener() {

					@Override
					public void onProgressUpdate(String imageUri, View view,
							int current, int total) {

					}
				});
	}

	/**
	 * 将彩色图转换为纯黑白二色
	 * 
	 * @param Bitmap 位图
	 * @return 返回转换好的位图
	 */
	public static Bitmap convertToBlackWhite(Bitmap bmp) {
		int width = bmp.getWidth(); // 获取位图的宽
		int height = bmp.getHeight(); // 获取位图的高
		int[] pixels = new int[width * height]; // 通过位图的大小创建像素点数组

		bmp.getPixels(pixels, 0, width, 0, 0, width, height);
		int alpha = 0xFF << 24;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				int grey = pixels[width * i + j];

				// 分离三原色
				int red = ((grey & 0x00FF0000) >> 16);
				int green = ((grey & 0x0000FF00) >> 8);
				int blue = (grey & 0x000000FF);

				// 转化成灰度像素
				grey = (int) (red * 0.3 + green * 0.59 + blue * 0.11);
				grey = alpha | (grey << 16) | (grey << 8) | grey;
				pixels[width * i + j] = grey;
			}
		}
		// 新建图片
		Bitmap newBmp = Bitmap.createBitmap(width, height, Config.RGB_565);
		// 设置图片数据
		newBmp.setPixels(pixels, 0, width, 0, 0, width, height);

		Bitmap resizeBmp = ThumbnailUtils.extractThumbnail(newBmp, 380, 460);
		return resizeBmp;
	}

}
