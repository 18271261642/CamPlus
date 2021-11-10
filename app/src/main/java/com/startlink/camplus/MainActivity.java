package com.startlink.camplus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class


MainActivity extends AppCompatActivity {


    private final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            handler.removeMessages(0x00);
            startActivity(new Intent(MainActivity.this, com.startlink.camplus.wifi.MainActivity.class));
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        handler.sendEmptyMessageDelayed(0x00,3 * 1000);

    }
}