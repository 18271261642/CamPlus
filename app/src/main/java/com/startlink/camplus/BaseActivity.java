package com.startlink.camplus;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    private ActivityManager activityManager = ActivityManager.getActivityManager();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityManager.putActivity(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityManager.removeActivity(this);
    }

    public void exitApp(){
        activityManager.exit();
        System.exit(0);
    }

}
