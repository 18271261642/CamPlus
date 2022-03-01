package com.startlink.camplus;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class ShowWebActivity extends BaseActivity{

    private WebView webView;

    private AppCompatTextView commTitleTv;

    String url;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_web_layout);



        initViews();

        initData();




    }

    private void initData() {
        String title = getIntent().getStringExtra("title");
        commTitleTv.setText(title == null ?"隐私政策" :title);
        int type = getIntent().getIntExtra("type",0);
        if(type == 0){
            url = "file:///android_asset/campro_privacy.html";
        }else{
            url = "file:///android_asset/campro_user_agreement.html";
        }


        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return true;
            }
        });
        webView.loadUrl(url);
    }

    private void initViews() {
        webView = findViewById(R.id.showWebView);
        commTitleTv= findViewById(R.id.commTitleTv);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(false);
        webSettings.setSupportZoom(false);

        findViewById(R.id.wifiTitleBackImg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
