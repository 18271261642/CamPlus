package com.startlink.camplus;

import android.app.Application;

import com.tencent.bugly.crashreport.CrashReport;

public class BaseApplication extends Application {


    private static BaseApplication baseApplication;


    @Override
    public void onCreate() {
        super.onCreate();
        baseApplication = this;

        CrashReport.initCrashReport(this, "efa2e530d3", false);
    }



    public static BaseApplication getInstance(){
        return baseApplication;
    }



}
