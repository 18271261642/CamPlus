package com.startlink.camplus.wifi;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;

import com.github.chrisbanes.photoview.PhotoView;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;
import com.startlink.camplus.R;

import java.io.File;
import java.util.List;

/**
 * 本地图片查看
 * Created by Admin
 * Date 2021/9/28
 */
public class LocalPictureActivity extends AppCompatActivity {

    private static final String TAG = "LocalPictureActivity";

    private PhotoView wifiPhotoView;

    private String pictureUrl;

    private ImageView wifiTitleBackImg;
    private AppCompatTextView itemTitleTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_wifi_picture_layout);


        initViews();

        pictureUrl = getIntent().getStringExtra("local_picture");
        if(pictureUrl == null)
            return;
        Log.e(TAG,"---pictureUrl="+pictureUrl);

        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            File file = new File(pictureUrl);
            Uri uri = Uri.fromFile(file);
            wifiPhotoView.setImageURI(uri);
        }

    }

    private void initViews() {
        wifiTitleBackImg = findViewById(R.id.wifiTitleBackImg);
        itemTitleTv = findViewById(R.id.commTitleTv);

        itemTitleTv.setText("图片查看");
        wifiTitleBackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        wifiPhotoView = findViewById(R.id.wifiPhotoView);

        XXPermissions.with(this).permission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}).request(new OnPermissionCallback() {
            @Override
            public void onGranted(List<String> permissions, boolean all) {

            }
        });

    }
}
