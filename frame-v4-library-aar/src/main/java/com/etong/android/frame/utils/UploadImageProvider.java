package com.etong.android.frame.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.etong.android.frame.publisher.HttpPublisher;
import com.etong.android.frame.utils.logger.Logger;

import org.simple.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

/**
 * 图片上传
 * 支持文件及Bitmap
 * Created by zhouxiqing on 2016/9/21.
 */
public class UploadImageProvider {
    /**
     * 访问网络失败
     */
    public static final int NETWORK_ERROR = HttpPublisher.NETWORK_ERROR;

    /**
     * Http访问异常或访问被取消
     */
    public static final int HTTP_ERROR = HttpPublisher.HTTP_ERROR;
    /**
     * 数据异常
     */
    public static final int DATA_ERROR = 0x1100;
    private static String IMAGE_UPDATE_ADDR = "http://113.247.237.98:10002/upload";
    private OkHttpClient client = null;
    private Context mContext = null;
    private static Callback callback = null;
    public static int queueSize = 0;

    private UploadImageProvider() {
//        callback = new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                queueSize--;
//                HashMap tag = (HashMap) call.request().tag();
//                JSONObject data = new JSONObject();
//                data.put("errCode", HTTP_ERROR);
//                if (call.isCanceled()) {
//                    data.put("errName", "Http访问被取消");
//                } else {
//                    data.put("errName", "Http访问异常");
//                }
//                data.put("id", tag.get("id"));
//                EventBus.getDefault().post(data, tag.get("tag").toString());
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                queueSize--;
//                HashMap tag = (HashMap) call.request().tag();
//                JSONObject data = JSON.parseObject(response.body().string());
//                if (data == null) {
//                    data = new JSONObject();
//                    data.put("errCode", DATA_ERROR);
//                    data.put("errName", "返回数据为空");
//                }
//                data.put("id", tag.get("id"));
//                EventBus.getDefault().post(data, tag.get("tag").toString());
//            }
//        };
    }

    private static class Holder{
        private static final UploadImageProvider INSTANCE = new UploadImageProvider();
        private static final Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                queueSize--;
                HashMap tag = (HashMap) call.request().tag();
                JSONObject data = new JSONObject();
                data.put("errCode", HTTP_ERROR);
                if (call.isCanceled()) {
                    data.put("errName", "Http访问被取消");
                } else {
                    data.put("errName", "Http访问异常");
                }
                data.put("id", tag.get("id"));
                EventBus.getDefault().post(data, tag.get("tag").toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                queueSize--;
                HashMap tag = (HashMap) call.request().tag();
                JSONObject data = JSON.parseObject(response.body().string());
                if (data == null) {
                    data = new JSONObject();
                    data.put("errCode", DATA_ERROR);
                    data.put("errName", "返回数据为空");
                }
                data.put("id", tag.get("id"));
                EventBus.getDefault().post(data, tag.get("tag").toString());
            }
        };
    }

    public static UploadImageProvider getInstance() {
        callback = Holder.callback;
        return Holder.INSTANCE;
    }

    public void initialize(Context context, OkHttpClient okHttpClient) {
        mContext = context.getApplicationContext();
        this.client = okHttpClient;
    }

    /**
     * 设置图片服务器地址，默认：http://113.247.237.98:10002/upload
     *
     * @param url 服务器地址
     */
    public void setHttpUrl(String url) {
        if (!TextUtils.isEmpty(url))
            IMAGE_UPDATE_ADDR = url;
    }

    /**
     * @param bitmap 将上传的图片
     * @param tag    事件回调标签
     * @param id     标识
     */
    public void uploadImage(final Bitmap bitmap, String tag, String id) {
        // 判断网络是否可用
        // 网络不可用时不进行网络连接
        if (!checkNetworkState()) {
            JSONObject data = new JSONObject();
            data.put("errCode", NETWORK_ERROR);
            data.put("errName", "网络异常");
            EventBus.getDefault().post(data, tag);
            return;
        }

        if (bitmap == null) {
            JSONObject data = new JSONObject();
            data.put("errCode", DATA_ERROR);
            data.put("errName", "数据异常，Bitmap 为空");
            EventBus.getDefault().post(data, tag);
            return;
        }
        final String filename = System.currentTimeMillis() + ".jpg";
        HashMap map = new HashMap();
        map.put("tag", tag);
        map.put("id", id);

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        RequestBody fileBody = new RequestBody() {

            @Override
            public MediaType contentType() {
                return MediaType.parse(guessMimeType(filename));
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                sink.write(baos.toByteArray());
                try {
                    if (baos != null)
                        baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        builder.addFormDataPart("dir", filename, fileBody);
        Request request = new Request.Builder()
                .url(IMAGE_UPDATE_ADDR)
                .post(builder.build())
                .tag(map)
                .build();
        queueSize++;
        client.newCall(request).enqueue(callback);
    }

    /**
     * @param AbsolutePath 图片路径
     * @param tag          回调标签
     * @param id           标识
     */
    public void uploadImage(final String AbsolutePath, String tag, final String id) {
        File file = new File(AbsolutePath);
        if (!file.exists()) {
            Logger.e("文件不存在");
            return;
        }
        String filename = System.currentTimeMillis() + "." + getExtensionName(file.getName());
        HashMap map = new HashMap();
        map.put("tag", tag);
        map.put("id", id);

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        RequestBody fileBody = RequestBody.create(MediaType.parse(guessMimeType(filename)), new File(AbsolutePath));
        builder.addFormDataPart("dir", filename, fileBody);
        Request request = new Request.Builder()
                .url(IMAGE_UPDATE_ADDR)
                .post(builder.build())
                .tag(map)
                .build();
        queueSize++;
        client.newCall(request).enqueue(callback);
    }

    private static String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = null;
        try {
            contentTypeFor = fileNameMap.getContentTypeFor(URLEncoder.encode(path, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    private boolean checkNetworkState() {
        ConnectivityManager connectivity = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
/*            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }*/
        }
        return false;
    }

    /**
     * 文件操作 获取文件扩展名
     */
    private static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    /**
     * 根据回调标签取消图片上传
     *
     * @param tag 将取消的回调标签
     */
    public void cancleByTag(String tag) {
        if (TextUtils.isEmpty(tag)) {
            return;
        }
        for (Call call : client.dispatcher().queuedCalls()) {
            if (tag.equals(((HashMap) call.request().tag()).get("tag").toString())) {
                call.cancel();
                queueSize--;
            }
        }
        for (Call call : client.dispatcher().runningCalls()) {
            if (tag.equals(((HashMap) call.request().tag()).get("tag").toString())) {
                call.cancel();
                queueSize--;
            }
        }
    }

    /**
     * 获取当前上传队列中的请求数
     *
     * @return
     */
    public int getQueueSize() {
        return queueSize;
    }
}
