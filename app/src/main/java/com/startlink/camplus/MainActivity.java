package com.startlink.camplus;

import androidx.annotation.NonNull;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class MainActivity extends BaseActivity {

    private  ShowPrivacyDialogView showPrivacyView;

    private final Handler handler = new Handler(Looper.getMainLooper()) {
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



        //是否已经同意了隐私政策
        boolean isAgree = (boolean) SharedPreferencesUtils.getParam(this,"is_agree",false);
        if(isAgree){
            handler.sendEmptyMessageDelayed(0x00, 3 * 1000);
        }else{
            showView();
        }

    }

    private void showView(){
        if(showPrivacyView == null){
            showPrivacyView = new ShowPrivacyDialogView(this);
        }
        showPrivacyView.show();
        showPrivacyView.setCancelable(false);
        showPrivacyView.setOnPrivacyClickListener(new ShowPrivacyDialogView.OnPrivacyClickListener() {
            @Override
            public void onCancelClick() {
                showPrivacyView.dismiss();
                finish();
            }

            @Override
            public void onConfirmClick() {
                showPrivacyView.dismiss();
                SharedPreferencesUtils.setParam(MainActivity.this,"is_agree",true);
                handler.sendEmptyMessageDelayed(0x00, 3 * 1000);
            }
        });
    }
}