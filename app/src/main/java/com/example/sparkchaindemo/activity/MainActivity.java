package com.example.sparkchaindemo.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.sparkchaindemo.R;
import com.example.sparkchaindemo.adapter.MyFragmentAdapter;
import com.example.sparkchaindemo.base.BaseActivity;
import com.example.sparkchaindemo.ui.CustomViewPager;
import com.example.sparkchaindemo.utils.FileUtils;
import com.google.android.material.tabs.TabLayout;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.XXPermissions;
import com.iflytek.sparkchain.core.SparkChain;
import com.iflytek.sparkchain.core.SparkChainConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.leancloud.LCLogger;
import cn.leancloud.LeanCloud;
import cn.leancloud.im.LCIMOptions;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    private TabLayout mTabLayout;
    private CustomViewPager mCustomViewPager;
    private MyFragmentAdapter myFragmentAdapter;
    private TabLayout.Tab one;
    private TabLayout.Tab two;
    private TabLayout.Tab three;
    private TabLayout.Tab four;
    private boolean isAuth = false;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("WrongViewCast")
    @Override
    protected void initView() {
        setContentView(R.layout.activity_main);
        //使用适配器将ViewPager与Fragment绑定在一起
        mCustomViewPager= (CustomViewPager) findViewById(R.id.viewPager);
        myFragmentAdapter = new MyFragmentAdapter(getSupportFragmentManager());
        mCustomViewPager.setAdapter(myFragmentAdapter);
        mCustomViewPager.setScanScroll(false);
        //将TabLayout与ViewPager绑定在一起
        mTabLayout =  findViewById(R.id.tabLayout);
        mTabLayout.setupWithViewPager(mCustomViewPager);

        //指定Tab的位置
        one = mTabLayout.getTabAt(0);
        two = mTabLayout.getTabAt(1);
        three = mTabLayout.getTabAt(2);
        four = mTabLayout.getTabAt(3);

        //设置Tab的图标，假如不需要则把下面的代码删去
        one.setIcon(R.drawable.select_home);
        two.setIcon(R.drawable.select_faxian);
        three.setIcon(R.drawable.select_xiaoxi);
        four.setIcon(R.drawable.select_mine);

    }

    @Override
    protected void initData() {

        getPermission();
        // 检查并请求权限，创建语音条文件夹
        checkStoragePermission();
    }

    @Override
    protected void initListener() {

    }
    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // 如果没有权限，请求权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            // 已有权限，直接创建文件夹
            createDirectoryIfNotExists();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限被授予，创建文件夹
                createDirectoryIfNotExists();
            } else {
                // 权限被拒绝
                Toast.makeText(this, "存储权限被拒绝，无法创建文件夹", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createDirectoryIfNotExists() {
        String path = "/sdcard/LxqVoice";
        File folder = new File(path);

    // 检查文件夹是否存在
        if (!folder.exists()) {
            // 文件夹不存在，创建它
            boolean success = folder.mkdirs();
            if (success) {
                // 文件夹创建成功
                try {
                    Log.d(TAG, "文件夹创建成功");
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "文件拷贝失败，请检查是否有sdcard读写权限", Toast.LENGTH_LONG).show();
                }
            } else {
                // 文件夹创建失败
                Toast.makeText(getApplicationContext(), "文件拷贝失败，请检查是否有sdcard读写权限", Toast.LENGTH_LONG).show();
            }
        } else {
            // 文件夹已存在，直接复制文件
            Log.d(TAG, "文件夹已存在");
        }
    }

    private void getPermission(){
        XXPermissions.with(this).permission("android.permission.WRITE_EXTERNAL_STORAGE"
                , "android.permission.READ_EXTERNAL_STORAGE"
                , "android.permission.INTERNET"
                , "android.permission.MANAGE_EXTERNAL_STORAGE").request(new OnPermission() {
            @Override
            public void hasPermission(List<String> granted, boolean all) {
                Log.d(TAG,"SDK获取系统权限成功:"+all);
                for(int i=0;i<granted.size();i++){
                    Log.d(TAG,"获取到的权限有："+granted.get(i));
                }
                if(all){
                    createWorkDir();
                    SDKInit();
                }
            }

            @Override
            public void noPermission(List<String> denied, boolean quick) {
                if(quick){
                    Log.e(TAG,"onDenied:被永久拒绝授权，请手动授予权限");
                    XXPermissions.startPermissionActivity(MainActivity.this,denied);
                }else{
                    Log.e(TAG,"onDenied:权限获取失败");
                }
            }
        });
    }
    private void createWorkDir()  {
        String path = "/sdcard/iflytek/asr";
        FileUtils.deleteDirectory(path);
        File folder = new File(path);
        boolean success = folder.mkdirs();
        if (success) {
            // 文件夹创建成功
            try {
                copyFilesFromAssets();
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"在线识别音频文件拷贝失败，请检查是否有sdcard读写权限",Toast.LENGTH_LONG).show();
            }
        } else {
            // 文件夹创建失败
            Toast.makeText(getApplicationContext(),"在线识别音频文件拷贝失败，请检查是否有sdcard读写权限",Toast.LENGTH_LONG).show();
        }
    }
    private void copyFilesFromAssets() throws IOException {
        String[] fileNames = getAssets().list("");
        if (fileNames != null && fileNames.length > 0) {
            for (String fileName : fileNames) {
                if (fileName.endsWith(".pcm")) {
                    try {
                        InputStream inputStream = getAssets().open(fileName);
                        OutputStream outputStream = new FileOutputStream("/sdcard/iflytek/asr/" + fileName);
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, length);
                        }
                        inputStream.close();
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    private void SDKInit(){
        Log.d(TAG,"initSDK");
        // 初始化SDK，Appid等信息在清单中配置
        SparkChainConfig sparkChainConfig = SparkChainConfig.builder();
        sparkChainConfig.appID(getResources().getString(R.string.appid))
                .apiKey(getResources().getString(R.string.apikey))
                .apiSecret(getResources().getString(R.string.apiSecret))//应用申请的appid三元组
                //   .uid("")
                .logPath("/sdcard/iflytek/AEELog.txt")
                .logLevel(666);

        int ret = SparkChain.getInst().init(getApplicationContext(),sparkChainConfig);
        String result;
        if(ret == 0){
            result = "SDK初始化成功,请选择相应的功能点击体验。";
            isAuth = true;
        }else{
            result = "SDK初始化失败,错误码:" + ret;
            isAuth = false;
        }
        Log.d(TAG,result);
        showToast(result);
    }
}