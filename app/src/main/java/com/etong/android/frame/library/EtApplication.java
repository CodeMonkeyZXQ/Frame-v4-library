package com.etong.android.frame.library;

import com.etong.android.frame.BaseApplication;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2016/7/19.
 */

public class EtApplication extends BaseApplication {
    public void onCreate(){
        setDebugMode(true);
        super.onCreate();
        InputStream certificates[] = new InputStream[1];
        try {
            certificates[0] = this.getAssets().open("12306.cer");
        } catch (IOException e) {
            e.printStackTrace();
        }
        initSSLParams(certificates);
    }
}
