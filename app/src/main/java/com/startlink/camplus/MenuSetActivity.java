package com.startlink.camplus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MenuSetActivity extends BaseActivity{


    private TextView versionTv;
    private ConstraintLayout privacyLayout,userAgreeLayout;
    private AppCompatTextView titleTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_set_layout);


        findViews();

    }

    private void findViews() {
        titleTv = findViewById(R.id.commTitleTv);
        versionTv = findViewById(R.id.appVersionTxt);
        privacyLayout = findViewById(R.id.setPrivacyLayout);
        userAgreeLayout = findViewById(R.id.setUserAgreeLayout);

        titleTv.setText("设置");

        privacyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAgreementActivity(0,"隐私政策");
            }
        });

        userAgreeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAgreementActivity(0,"用户协议");
            }
        });

        findViewById(R.id.wifiTitleBackImg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    public void startAgreementActivity(int type,String title) {
        Intent intent = new Intent(this, ShowWebActivity.class);
        intent.putExtra("type",type);
        intent.putExtra("title", title);
        startActivity(intent);
    }
}
