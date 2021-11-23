package com.startlink.camplus;

import android.app.Application;

import com.startlink.camplus.wifi.BasePreferences;
import com.startlink.camplus.wifi.Constance;
import com.tencent.bugly.crashreport.CrashReport;

public class BaseApplication extends Application {


    private static BaseApplication baseApplication;


    @Override
    public void onCreate() {
        super.onCreate();
        baseApplication = this;

        CrashReport.initCrashReport(this, "efa2e530d3", false);

        BasePreferences.init(this, Constance.SP_FILE_NAME);
    }



    public static BaseApplication getInstance(){
        return baseApplication;
    }



}
