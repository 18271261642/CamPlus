package com.startlink.camplus.wifi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;
import com.startlink.camplus.BaseActivity;
import com.startlink.camplus.MenuSetActivity;
import com.startlink.camplus.R;
import com.startlink.camplus.SharedPreferencesUtils;
import com.startlink.camplus.ShowWebActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeoutException;
import generalplus.com.GPCamLib.CamWrapper;
import generalplus.com.GPCamLib.GPINIReader;

/**
 * 连接wifi页面
 */
public class MainActivity extends BaseActivity {

    private static String TAG = "MainActivity";
    private CamWrapper m_CamWrapper;
    private GPINIReader m_GPINIReader;
    private ProgressDialog m_Dialog;
    private String strSaveDirectory;
    private Context mContext;
    private ImageView imgbtn_connect;
    private int m_iSetTime = 0;
    private int m_inputSelection = 0;

    private ImageView wifiTitleBackImg;

    private ImageView menuRightImg;

    //判断是否已经勾选了
    private CheckBox mainCheckBox;

    private AlertDialog.Builder alert;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getBooleanExtra("EXIT", false)) {
           // exitApp();
            finish();
        }

        setContentView(R.layout.activity_wifi_main);

        Log.e(TAG,"------ext="+getExternalFilesDir(null).getPath()+"\n"+getFilesDir().getPath());


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //getActionBar().setDisplayShowHomeEnabled(false);
        mContext = MainActivity.this;

        initViews();

        initData();

        if (m_CamWrapper == null) {
            m_CamWrapper = new CamWrapper();
        }

        if (m_GPINIReader == null)
            m_GPINIReader = new GPINIReader();


        SharedPreferences preferences = getSharedPreferences("Data", 0);
        m_iSetTime = preferences.getInt("SetTime", 0);

        if (isExternalStorageWritable())
            recordLogCatHandler.postDelayed(runnableRecordLogCat, 0);
        TextView tv_version = (TextView) findViewById(R.id.tv_version);
        tv_version.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                List<String> lunch = new ArrayList<String>();
                lunch.add(getResources().getString(R.string.Set_Wifi_sport_Cam_Time));
                lunch.add(getResources().getString(R.string.Save_Log));
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(getResources().getString(R.string.Setting))
                        .setItems(lunch.toArray(new String[lunch.size()]), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (0 == which) {
                                    showSetTimeDialog();
                                } else {
                                    showSaveLogDialog();
                                }
                                dialog.dismiss();
                            }
                        })
                        .show();
            }

        });
        Log.e(TAG, "Version = " + getResources().getString(R.string.app_version));

        if (Build.VERSION.SDK_INT >= 21) {
            NetworkRequest.Builder builder = new NetworkRequest.Builder();
            // 设置指定的网络传输类型(蜂窝传输) 等于手机网络
            builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);

            final ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkRequest request = builder.build();
            ConnectivityManager.NetworkCallback callback = new ConnectivityManager.NetworkCallback() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onAvailable(Network network) {
                    super.onAvailable(network);
                    Log.i("test", "已根据功能和传输类型找到合适的网络");
                    // 可以通过下面代码将app接下来的请求都绑定到这个网络下请求
                    if (Build.VERSION.SDK_INT >= 23) {
                        connectivityManager.bindProcessToNetwork(network);
                    } else {// 23后这个方法舍弃了
                        ConnectivityManager.setProcessDefaultNetwork(network);
                    }
                    // 也可以在将来某个时间取消这个绑定网络的设置
                    // if (Build.VERSION.SDK_INT >= 23) {
                    //      onnectivityManager.bindProcessToNetwork(null);
                    //} else {
                    //     ConnectivityManager.setProcessDefaultNetwork(null);
                }
            };
            //connMgr.requestNetwork(request, callback);
            if(ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_DENIED)
                return;
            connectivityManager.registerNetworkCallback(request, callback);
        }

    }





    private void showAlert(){
        alert = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("连接设备需要需要打开存储权限,是否允许?")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        openPermission();
                    }
                }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                    }
                });

        AlertDialog at = alert.show();
        Button yesBtn = at.getButton(DialogInterface.BUTTON_POSITIVE);
        Button noBtn = at.getButton(DialogInterface.BUTTON_NEGATIVE);
        yesBtn.setTextColor(ActivityCompat.getColor(MainActivity.this,R.color.black));
        noBtn.setTextColor(ActivityCompat.getColor(MainActivity.this,R.color.black));


    }


    private void openPermission(){

        XXPermissions.with(this).permission(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE).request(new OnPermissionCallback() {
            @Override
            public void onGranted(List<String> permissions, boolean all) {
                crateDirectory();
            }
        });


        requestPermiss();
        if (shouldAskPermission()) {
            int writePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (writePermission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};

                int permsRequestCode = 200;

                ActivityCompat.requestPermissions(this, perms, permsRequestCode);
            } else {
                crateDirectory();
            }
        } else {
            crateDirectory();
        }
    }


    private void initData() {
        boolean isChecked = (boolean) SharedPreferencesUtils.getParam(this,"is_check",false);
        mainCheckBox.setChecked(isChecked);
        boolean isPermiss = ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;


        //判断权限是否已经打开
        if(!isPermiss){
            showAlert();
        }
        crateDirectory();

        imgbtn_connect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!mainCheckBox.isChecked()){
                    Toast.makeText(MainActivity.this,"请同意隐私政策和用户协议！",Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean isGetP = ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

                //判断权限是否已经打开
                if(!isGetP){
                    showAlert();
                    return;
                }

                if (m_connectGPWifiDeviceThread == null) {
                    if (m_Dialog == null) {
                        m_Dialog = new ProgressDialog(mContext);
                        m_Dialog.setCanceledOnTouchOutside(false);
                        m_Dialog.setMessage(getResources().getString(R.string.Connecting));
                    }
                    m_Dialog.show();

                    m_connectGPWifiDeviceThread = new Thread(new ConnectGPWifiDeviceRunnable());
                    m_connectGPWifiDeviceThread.start();
                }
            }

        });
    }


    private void initViews(){
        mainCheckBox = findViewById(R.id.mainCheckBox);
        menuRightImg = findViewById(R.id.menuRightImg);
        menuRightImg.setVisibility(View.VISIBLE);
        wifiTitleBackImg = findViewById(R.id.wifiTitleBackImg);
        wifiTitleBackImg.setVisibility(View.INVISIBLE);
        imgbtn_connect = findViewById(R.id.imgbtn_connect);

        AppCompatTextView titleTv = findViewById(R.id.commTitleTv);
        titleTv.setText("连接设备");


        findViewById(R.id.mainPrivacyTv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAgreementActivity(0,"隐私政策");
            }
        });

        findViewById(R.id.mainUserAgreeTv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAgreementActivity(0,"用户协议");
            }
        });


        mainCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mainCheckBox.setChecked(b);
                SharedPreferencesUtils.setParam(MainActivity.this,"is_check",b);
            }
        });



        menuRightImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MenuSetActivity.class));
            }
        });


    }




    private void showSetTimeDialog() {
        final CharSequence[] items = {getResources().getString(R.string.Disable), getResources().getString(R.string.Enable)};

        // Creating and Building the Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getResources().getString(R.string.Set_Wifi_sport_Cam_Time));

        builder.setSingleChoiceItems(items, m_iSetTime,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        if (m_iSetTime != item) {
                            SharedPreferences preferences = getSharedPreferences("Data", 0);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putInt("SetTime", item);
                            editor.commit();
                            m_iSetTime = item;
                        }

                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    private void crateDirectory() {

        Log.e(TAG,"--------创建文件=-----");
        //strSaveDirectory = Environment.getExternalStorageDirectory().getPath() + "/" + CamWrapper.CamDefaulFolderName;
        strSaveDirectory = getExternalFilesDir(null).getPath()+"/"+ CamWrapper.CamDefaulFolderName;

        Log.e(TAG,"-----strSaveDirectory="+strSaveDirectory);


        File SaveFileDirectory = new File(strSaveDirectory);
        SaveFileDirectory.mkdirs();

        File CameraFileDirectory = new File(Environment.getExternalStorageDirectory().getPath() + CamWrapper.SaveFileToDevicePath);


        if (!CameraFileDirectory.exists())
            CameraFileDirectory.mkdirs();
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        try {
            if (null == grantResults) {
                return;
            }

            if( permissions.length == 0)
                return;

            switch (permsRequestCode) {

                case 200:

                    boolean writeAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (!writeAccepted) {
                        if (shouldAskPermission()) {
                            String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

                            ActivityCompat.requestPermissions(this, perms, permsRequestCode);
                        }
                    } else {
                        crateDirectory();
                    }
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private boolean shouldAskPermission() {

        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);

    }

    private void showSaveLogDialog() {
        final CharSequence[] items = {getResources().getString(R.string.Disable), getResources().getString(R.string.Enable)};

        // Creating and Building the Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getResources().getString(R.string.Save_Log));

        builder.setSingleChoiceItems(items, m_inputSelection,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        m_inputSelection = item;

                        recordLogCatHandler.removeCallbacks(runnableRecordLogCat);
                        bWriteLogCatFile = false;
                        if (1 == item) {
                            if (isExternalStorageWritable()) {
                                bWriteLogCatFile = true;
                                recordLogCatHandler.postDelayed(runnableRecordLogCat, 0);
                            }
                        }
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    private static class Worker extends Thread {
        private final Process process;
        private Integer exit;

        private Worker(Process process) {
            this.process = process;
        }

        public void run() {
            try {
                exit = process.waitFor();
            } catch (InterruptedException ignore) {
                ignore.printStackTrace();
                return;
            }
        }
    }

    public static int executeCommandLine(final String commandLine,
                                         final long timeout)
            throws IOException, InterruptedException, TimeoutException {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(commandLine);

        Worker worker = new Worker(process);
        worker.start();
        try {
            worker.join(timeout);
            if (worker.exit != null)
                return worker.exit;
            else
                throw new TimeoutException();
        } catch (InterruptedException ex) {
            worker.interrupt();
            Thread.currentThread().interrupt();
            throw ex;
        } finally {
            process.destroy();
        }
    }


    private static Thread m_connectGPWifiDeviceThread = null;

    class ConnectGPWifiDeviceRunnable implements Runnable {

        //Check Wifi Status
        private boolean bCheckWifiStatus = false;
        private int i32RetryCheckWifiStatusCount = 100;
        private int i32CheckWifiStatusDelayTime = 200;

        //Check Connect Status;
        private boolean bCheckConnectStatus = false;
        private int i32Status;

        private int i32RetryCount = 20;

        @Override
        public void run() {
            if (bCheckConnectStatus) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            WifiManager wifiManager = (WifiManager) mContext.getSystemService(WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo != null) {
                int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
                if (0 == ipAddress) {
                    bCheckWifiStatus = false;
                } else {
                    // Convert little-endian to big-endianif needed
                    if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
                        ipAddress = Integer.reverseBytes(ipAddress);
                    }
                    byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();
                    CamWrapper.COMMAND_URL = (ipByteArray[0] & 0xFF) + "." + (ipByteArray[1] & 0xFF) + "."
                            + (ipByteArray[2] & 0xFF) + ".1";
                    bCheckWifiStatus = true;

//					try {
//						int returnVal = -1;
//						try {
//							returnVal = executeCommandLine("ping -c 1 " + CamWrapper.COMMAND_URL, 1500);
//						} catch (TimeoutException e) {
//							e.printStackTrace();
//						}
//						bCheckWifiStatus = (returnVal==0);
//					} catch (UnknownHostException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
                }
            }

            if (bCheckWifiStatus) {
                CamWrapper.getComWrapperInstance().GPCamConnectToDevice(CamWrapper.COMMAND_URL, CamWrapper.COMMAN_PORT);
                while (bCheckWifiStatus) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i32Status = CamWrapper.getComWrapperInstance().GPCamGetStatus();
                    switch (i32Status) {
                        case CamWrapper.GPTYPE_ConnectionStatus_Idle:
                        case CamWrapper.GPTYPE_ConnectionStatus_Connecting:
                            bCheckConnectStatus = false;
                            bCheckWifiStatus = true;
                            break;
                        case CamWrapper.GPTYPE_ConnectionStatus_Connected:
                            bCheckConnectStatus = true;
                            bCheckWifiStatus = false;
                            CamWrapper.getComWrapperInstance().SetGPCamSetDownloadPath(strSaveDirectory);
                            //CamWrapper.getComWrapperInstance().SetGPCamSendGetParameterFile(CamWrapper.ParameterFileName);
                            CamWrapper.getComWrapperInstance().GPCamGetStatus();
                            break;
                        case CamWrapper.GPTYPE_ConnectionStatus_DisConnected:
                            i32RetryCount--;
                            if (i32RetryCount == 0) {
                                bCheckConnectStatus = false;
                                bCheckWifiStatus = false;
                                CamWrapper.getComWrapperInstance().GPCamDisconnect();
                                break;
                            }
                            break;
                        case CamWrapper.GPTYPE_ConnectionStatus_SocketClosed:
                            i32RetryCount--;
                            if (i32RetryCount == 0) {
                                bCheckConnectStatus = false;
                                bCheckWifiStatus = false;
                                CamWrapper.getComWrapperInstance().GPCamDisconnect();
                                break;
                            }
                            break;
                    }
                }
            }

            if (m_Dialog != null) {
                if (m_Dialog.isShowing()) {
                    m_Dialog.dismiss();
                    m_Dialog = null;
                }
            }
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (bCheckConnectStatus) {
                        Intent toVlcPlayer = new Intent(MainActivity.this, MainViewController.class);
                        Bundle b = new Bundle();
                        b.putInt("SetTime", m_iSetTime);
                        toVlcPlayer.putExtras(b);
                        startActivity(toVlcPlayer);
                        //finish();
                    } else {
                        Toast.makeText(mContext, getResources().getString(R.string.Please_connect_message), Toast.LENGTH_SHORT).show();
                        try {
                            Intent intent = new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK);
                            startActivityForResult(intent,0x00);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }

            });
            m_connectGPWifiDeviceThread = null;
        }

    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy ...");
        if (m_Dialog != null) {
            if (m_Dialog.isShowing()) {
                m_Dialog.dismiss();
                m_Dialog = null;
            }
        }
        recordLogCatHandler.removeCallbacks(runnableRecordLogCat);
        super.onDestroy();

        if(m_connectGPWifiDeviceThread != null){
            m_connectGPWifiDeviceThread.interrupt();
            m_connectGPWifiDeviceThread = null;
        }
    }

    private boolean bWriteLogCatFile = false;
    private String strFileName = "";
    // Thread for recording LOGCAT
    int MAX_LOGCAT_TIMES = 150;
    int i32RecordLogCatCounter = 0;
    Handler recordLogCatHandler = new Handler();
    Runnable runnableRecordLogCat = new Runnable() {

        @Override
        public void run() {

            if (bWriteLogCatFile) {
                // Save to another file
                i32RecordLogCatCounter++;
                if (i32RecordLogCatCounter >= MAX_LOGCAT_TIMES) {
                    strFileName = "";
                    i32RecordLogCatCounter = 0;
                }

                // Write LogCat to file
//				try {
//					strFileName = GetFileName();
//					//Process process = Runtime.getRuntime().exec("logcat -c");
//					//Runtime.getRuntime().exec("logcat -c");
//					Runtime.getRuntime().exec("logcat -v time -f " + strFileName);
//
//					//Toast.makeText(getApplicationContext(), "Save File Sucess", Toast.LENGTH_SHORT).show();
//
//				} catch (IOException e) {
//
//					strFileName = "";
//					Toast.makeText(getApplicationContext(), "Save File Fail", Toast.LENGTH_SHORT).show();
//					e.printStackTrace();
//				}

                writeLog();
                recordLogCatHandler.postDelayed(this, 2000);
            } else {
                recordLogCatHandler.removeCallbacks(runnableRecordLogCat);
            }

        }
    };

    private FileOutputStream fos;

    private void writeLog() {

        try {
            /*命令的准备*/
            ArrayList<String> getLog = new ArrayList<String>();
            getLog.add("logcat");
            getLog.add("-d");
            getLog.add("-v");
            getLog.add("time");
            ArrayList<String> clearLog = new ArrayList<String>();
            clearLog.add("logcat");
            clearLog.add("-c");

            Process process = Runtime.getRuntime().exec(getLog.toArray(new String[getLog.size()]));//抓取当前的缓存日志

            BufferedReader buffRead = new BufferedReader(new InputStreamReader(process.getInputStream()));//获取输入流
            //Runtime.getRuntime().exec(clearLog.toArray(new String[clearLog.size()]));//清除是为了下次抓取不会从头抓取
            String str = null;

            strFileName = GetFileName();
            File logFile = new File(strFileName);//打开文件
            fos = new FileOutputStream(logFile, false);//true表示在写的时候在文件末尾追加
            String newline = System.getProperty("line.separator");//换行的字符串
            //Date date = new Date(System.currentTimeMillis());
            //String time = format.format(date);


            //Log.i(TAG, "thread");
            while ((str = buffRead.readLine()) != null) {//循环读取每一行
                //Runtime.getRuntime().exec(clearLog.toArray(new String[clearLog.size()]));
                //Log.i(TAG, str);
                fos.write((str).getBytes());//加上年
                fos.write(newline.getBytes());//换行
            }
            fos.close();
            fos = null;
            //Runtime.getRuntime().exec(clearLog.toArray(new String[clearLog.size()]));
        } catch (Exception e) {
            strFileName = "";
            Toast.makeText(getApplicationContext(), "Save File Fail", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private String GetFileName() {
        if (0 == strFileName.compareToIgnoreCase("")) {
            // Get current time
            SimpleDateFormat df = new SimpleDateFormat("yyyy MM dd HH mm ss", Locale.CHINESE);
            Calendar c = Calendar.getInstance();
            String strTimeStamp = df.format(c.getTime());
            String TrimTimeStamp = strTimeStamp.replace(" ", "");

            String strYear = TrimTimeStamp.substring(0, 4);
            String strMonth = TrimTimeStamp.substring(4, 6);
            String strDay = TrimTimeStamp.substring(6, 8);
            String strHour = TrimTimeStamp.substring(8, 10);
            String strMinute = TrimTimeStamp.substring(10, 12);
            String strSecond = TrimTimeStamp.substring(12, 14);

            File logDirectory = new File(Environment.getExternalStorageDirectory() + "/LOGCAT");
            if (!logDirectory.exists()) {        // create log folder
                logDirectory.mkdir();
            }

            strFileName = "/sdcard/LOGCAT/LOGCAT_" + strYear + "_" + strMonth + "_" + strDay + "_" + strHour + "_"
                    + strMinute + "_" + strSecond + "_" + "Log.txt";

            // Rescan File
            File dir = new File(strFileName);
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.parse("file://" + dir.getAbsolutePath())));
        }

        return strFileName;
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG,"-----------requestCode="+requestCode+" "+resultCode);
    }

    private void requestPermiss() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            XXPermissions.with(this).permission(Manifest.permission.MANAGE_EXTERNAL_STORAGE).request(new OnPermissionCallback() {
                @Override
                public void onGranted(List<String> permissions, boolean all) {

                }
            });
        }
    }


    public void startAgreementActivity(int type,String title) {
        Intent intent = new Intent(this, ShowWebActivity.class);
        intent.putExtra("type",type);
        intent.putExtra("title", title);
        startActivity(intent);
    }


}
