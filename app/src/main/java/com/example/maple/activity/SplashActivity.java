package com.example.maple.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Contacts;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 *
 */
public class SplashActivity extends Activity {

    public final static String tag = "SplashActivity";
    public final static int CONNECT_TIMEOUT =60;
    public final static int READ_TIMEOUT=100;
    public final static int WRITE_TIMEOUT=60;
    private TextView tv_versionName;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*隐藏actionBar的方法其一（这句代码一定要添加到setContentView(R.layout.activity_main); 之前）*/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        /*隐藏状态栏方法二   用getActionBar()得到ActionBar对象，用对象调用hide()方法； */
       /* ActionBar actionBar = getActionBar();
        actionBar.hide();*/

       /*初始化界面*/
       initUI();
        /*初始化数据*/
        initData();
    }

    /*初始化UI*/
    void initUI(){
        tv_versionName = findViewById(R.id.tv_versionName);
        tv_versionName.setText("版本名称"+getVersionName());
    }

    /**
     *
     */
    /*初始化数据*/
    void initData(){
        String url = "http://10.0.2.2:8080/getVersion.json";//http://localhost:8080/getVersion.json";
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT,TimeUnit.SECONDS)
                .connectTimeout(CONNECT_TIMEOUT,TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    String str = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(str);
                        //获取服务器版本号
                        int serverVersionCode = Integer.parseInt(jsonObject.getString("versionCode"));
                        Log.i(tag,str);
                        if (serverVersionCode > getVersionCode()){
                            //回到主线程执行
                            SplashActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SplashActivity.this,"有新的版本可用！",Toast.LENGTH_LONG).show();
                                }
                            });
                        }else{
                            SplashActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(SplashActivity.this,"已经是最新版本！",Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }






    /**
     * @return 版本名称
     */
    String getVersionName(){
        PackageManager packageManager = getPackageManager();
        PackageInfo packInfo = new PackageInfo();
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(),0);
            return packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    int getVersionCode(){
        PackageManager packageManager = getPackageManager();
        PackageInfo packInfo = new PackageInfo();
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(),0);
            return packInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
