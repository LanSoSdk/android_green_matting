package com.example.greenmatting;

import android.app.Application;

import com.lansosdk.videoeditor.LanSoEditor;

/**
 * Description : 杭州蓝松科技有限公司
 *
 * @author guozhijun
 * @date 7/23/21
 */
public class MyApplication  extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        LanSoEditor.initSDK(getApplicationContext(), null);
    }
}
