//package com.etong.android.frame.publisher;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.Volley;
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.GlideBuilder;
//import com.bumptech.glide.integration.volley.VolleyUrlLoader;
//import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
//import com.bumptech.glide.load.model.GlideUrl;
//import com.bumptech.glide.load.model.ModelLoaderFactory;
//import com.etong.android.frame.R;
//import com.etong.android.frame.event.CommonEvent;
//import com.etong.android.frame.utils.logger.Logger;
//
//import java.io.BufferedOutputStream;
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.InputStream;
//
//import okio.BufferedSink;
//
///**
// * @ClassName : FilePublisher
// * @Description : 图片事件发布，包括图片上传，下载
// * @author : yuanjie
// * @date : 2015-9-2 上午10:15:17
// *
// */
//public class FilePublisher extends Publisher {
//	private RequestQueue mFileQueue = null;
//	private String mHttpTag = "volley";
//	private Glide mGlide = null;
//	private Context mContext = null;
//	public static final String diskCache = "image_cache_dir";
//	private static final int diskCacheSize = 10 * 1024 * 1024;
//	private static FilePublisher instance = null;
//	private static boolean isInitialize = false;
//
//	private class DataListener implements Response.Listener<String> {
//		private String mTag = "";
//		private FileMethod mMethod;
//
//		DataListener(String tag, FileMethod method) {
//			mTag = tag;
//			mMethod = method;
//		}
//
//		@Override
//		public void onResponse(String data) {
//			Logger.t(mTag).json(data);
//			JSONObject dataJson = JSON.parseObject(data);
//			getEventBus().post(mMethod.put(dataJson), mTag);
//		}
//
//	}
//
//	public class FileMethod {
//		private String url = null;
//		private MutipartParams param = null;
//		private JSONObject data = null;
//		private VolleyError error;
//
//		public FileMethod(String url, MutipartParams param) {
//			this.url = url;
//			this.param = param;
//		};
//
//		public String getUrl() {
//			return url;
//		}
//
//		public MutipartParams getParam() {
//			return param;
//		}
//
//		public JSONObject data() {
//			return data;
//		}
//
//		public <T> T data(Class<T> clazz) {
//			return JSON.toJavaObject(data, clazz);
//		}
//
//		public FileMethod put(JSONObject data) {
//			this.data = data;
//			return this;
//		}
//
//		public VolleyError error() {
//			return error;
//		}
//
//		public FileMethod error(VolleyError e) {
//			this.error = e;
//			return this;
//		}
//	}
//
//	private FilePublisher() {
//	}
//
//	public static FilePublisher getInstance() {
//		if (instance == null) {
//			instance = new FilePublisher();
//		}
//		return instance;
//	}
//
//	@SuppressWarnings("deprecation")
//	public void initialize(Context context) {
//		synchronized (this) {
//			if (!isInitialize) {
//				mContext = context.getApplicationContext();
//				mFileQueue = Volley.newRequestQueue(mContext);
//				GlideBuilder glideBuilder = new GlideBuilder(mContext);
//				glideBuilder.setDiskCache(new ExternalCacheDiskCacheFactory(
//						context, diskCache, diskCacheSize));
//				Glide.setup(glideBuilder);
//				mGlide = Glide.get(context);
//				ModelLoaderFactory<GlideUrl, InputStream> factory = new VolleyUrlLoader.Factory(
//						this.mFileQueue);
//				mGlide.register(GlideUrl.class, InputStream.class, factory);
//				isInitialize = true;
//			}
//		}
//	}
//
//	public void destroy() {
//		mFileQueue.cancelAll(new RequestQueue.RequestFilter() {
//			@Override
//			public boolean apply(Request<?> request) {
//				// do I have to cancel this?
//				return true; // -> always yes
//			}
//		});
//	}
//
//	public void cancel(String tag) {
//		mFileQueue.cancelAll(tag);
//	}
//
//	public void trimMemory(int level) {
//		mGlide.trimMemory(level);
//	}
//
//	public void clearMemeory() {
//		mGlide.clearMemory();
//	}
//
//	public void setHttpTag(String tag) {
//		mHttpTag = tag;
//	}
//
//	@SuppressWarnings("unused")
//	private void checkNetworkState() {
//		ConnectivityManager connectivity = (ConnectivityManager) mContext
//				.getSystemService(Context.CONNECTIVITY_SERVICE);
//		if (connectivity != null) {
//			NetworkInfo info = connectivity.getActiveNetworkInfo();
//			if (info != null && info.isConnected()) {
//				// 当前网络是连接的
//				if (info.getState() == NetworkInfo.State.CONNECTED) {
//					return;
//				}
//			}
//		}
//		// TODO : 需要添加事件通知
//	}
//
//	private void sendRequest(final FileMethod method, final String tag) {
//
//		Response.Listener<String> dataListener = new DataListener(tag, method);
//
//		Response.ErrorListener errListener = new Response.ErrorListener() {
//			@Override
//			public void onErrorResponse(VolleyError e) {
//				Logger.t("VOLLEY").e( JSON.toJSONString(e));
//				getEventBus().post(method.error(e), CommonEvent.EVOLLEY);
//			}
//		};
//
//		// 上传文件
//		MutipartRequest mutiRequest = new MutipartRequest(Request.Method.POST,
//				method.getUrl(), dataListener, errListener, method.getParam());
//		mutiRequest.setTag(mHttpTag);
//
//		mFileQueue.add(mutiRequest);
//	}
//
//	public void upload(String filePath, String tag) {
//		MutipartParams map = new MutipartParams();
//		map.put("file", new File(filePath));
//		String url = "http://113.247.237.98:10002/upload?dir=image";
//		FileMethod method = new FileMethod(url, map);
//		sendRequest(method, tag);
//	}
//}
