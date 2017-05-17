package com.bapm.bzys.newBzys_store.base;


import android.app.Application;

import com.zhy.autolayout.config.AutoLayoutConifg;

/**
 * Created by fs-ljh on 2017/4/24.
 */

public class UseDeviceSizeApplication extends Application {
    @Override
    public void onCreate()
    {
        super.onCreate();
        AutoLayoutConifg.getInstance().useDeviceSize();
    }
}
