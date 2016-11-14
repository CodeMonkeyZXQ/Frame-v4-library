package com.etong.android.frame.publisher;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.etong.android.frame.event.CommonEvent;
import com.etong.android.frame.utils.logger.Logger;

import org.simple.eventbus.EventBus;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class HttpPublisher extends Publisher {
    /**
     * 访问网络失败
     */
    public static final int NETWORK_ERROR = 0X1101;

    /**
     * Http访问异常
     */
    public static final int HTTP_ERROR = 0X1111;

    /**
     * VOLLEY 异常(已废弃)
     */
    @Deprecated
    public static final int VOLLEY_ERROR = HTTP_ERROR;

    private Context mContext = null;
    //    private static HttpPublisher instance = null;
    private OkHttpClient client = null;
    private Map<String, String> mHttpToken = new HashMap<>();
    private static final String DEFAULT_TOKEN_NAME = "accessToken";
    private static final String TAG = "HttpPubliser";

    public class JsonObjectCallback implements Callback {
        private HttpMethod method;
        private String eventTag;

        public JsonObjectCallback(String tag, HttpMethod method) {
            this.eventTag = tag;
            this.method = method;
        }

        @Override
        public void onFailure(Call call, IOException e) {
            JSONObject data = new JSONObject();
            data.put("errCode", HTTP_ERROR);
            if (call.isCanceled()) {
                data.put("errName", "Http访问被取消");
            } else {
                data.put("errName", "Http访问异常");
            }
            method.put(data);
            Logger.e(e, "HttpPublisher onFailure");
            if (method.getParam() != null && !method.getParam().isEmpty()) {
                Logger.e(method.getParam().toString());
            }
            Logger.json(method.data().toJSONString());
            EventBus.getDefault().post(method, eventTag);
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            JSONObject data = JSON.parseObject(response.body().string());
            EventBus.getDefault().post(method.put(data), eventTag);
            Logger.d(method.getParam().toString());
            Logger.json(method.data().toJSONString());
        }
    }

    private static class Holder {
        private static final HttpPublisher INSTANCE = new HttpPublisher();
    }

    private HttpPublisher() {
    }

    public static final HttpPublisher getInstance() {
        return Holder.INSTANCE;
    }

    public void initialize(Context context, OkHttpClient okHttpClient) {
        mContext = context.getApplicationContext();
        client = okHttpClient;
    }


    /**
     * @Title : setToken @Description :
     * 设置Token，sendRequestWithToken发送请求会带个token @params @param token
     * 设定文件 @return void 返回类型 @throws
     */
    public void setToken(String token) {
        if (null != token && !token.isEmpty())
            mHttpToken.put(DEFAULT_TOKEN_NAME, token);
    }

    public void setToken(String name, String token) {
        if (null != name && null != token && !token.isEmpty() && !name.isEmpty()) {
            mHttpToken.put(name, token);
        }
    }

    public boolean checkNetworkState() {
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
        }
        return false;
    }

    public void sendRequest(final HttpMethod method, final String tag) {
        // 判断网络是否可用
        // 网络不可用时不进行网络连接
        if (!checkNetworkState()) {
            JSONObject data = new JSONObject();
            data.put("errCode", NETWORK_ERROR);
            data.put("errName", "网络异常");
            method.put(data);
            Logger.e(method.getParam().toString());
            Logger.json(data.toJSONString());
            getEventBus().post(method, CommonEvent.HTTP_ERROR);
            getEventBus().post(method, tag);// 添加访问失败时的异常
            return;
        }
        FormBody.Builder formBuilder = new FormBody.Builder();
        Map<String, Object> map = method.getParam();
        if (map != null && !map.isEmpty()) {
            for (String key : map.keySet()) {
                formBuilder.add(key, method.getParam().get(key).toString());
            }
        }
        Request request = new Request.Builder()
                .url(method.getUrl())
                .post(formBuilder.build())
                .tag(tag)
                .build();
        client.newCall(request).enqueue(new JsonObjectCallback(tag, method));
    }

    /**
     * @Title : sendRequestWithToken @Description : 带Token的发送请求 @params @param
     * method @param tag 设定文件 @return void 返回类型 @throws
     */
    public void sendRequestWithToken(HttpMethod method, String tag) {
        for (String key : mHttpToken.keySet()) {
            method.getParam().put(key, mHttpToken.get(key));
        }
        sendRequest(method, tag);
    }

    /**
     * 取消所有
     */
    public void cancelAll() {
        for (Call call : client.dispatcher().queuedCalls()) {
            call.cancel();
        }
        for (Call call : client.dispatcher().runningCalls()) {
            call.cancel();
        }
    }

    /**
     * 根据TAG取消
     *
     * @param tag
     */
    public void cancel(String tag) {

        if (TextUtils.isEmpty(tag)) {
            return;
        }
        for (Call call : client.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : client.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }
}