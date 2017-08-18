package com.etong.android.frame.common;

import java.util.HashMap;

/**
 * Created by zhouxiqing on 2017/5/23.
 */

public class ErrCodeMap extends HashMap<String,String>{

    public ErrCodeMap() {
        /**
         * token不存在
         */
        this.put("USER_NO_LOGIN","1000");
        /**
         * token过期
         */
        this.put("USER_TOKEN_EXPIRED","1001");
        /**
         * token刷新
         */
        this.put("USER_TOKEN_REFRESH","1002");
        /**
         * token为空
         */
        this.put("USER_TOKEN_EMPTY","1003");
        /**
         * token不存在
         */
        this.put("USER_TOKEN_NONE","1004");

    }

    public ErrCodeMap(String key, String value) {
        this();
        this.put(key, value);
    }

    public ErrCodeMap(String key, int value) {
        this();
        this.put(key, value + "");
    }

    public void put(String key, int value) {
        this.put(key, value + "");
    }

    public void put(String key, long value) {
        this.put(key, value + "");
    }

    public void put(String key, double value) {
        this.put(key, value + "");
    }
}
