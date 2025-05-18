package com.example.sparkchaindemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;


import com.example.sparkchaindemo.R;
import com.example.sparkchaindemo.base.BaseActivity;

import cn.bmob.v3.Bmob;
import cn.leancloud.LCLogger;
import cn.leancloud.LCUser;
import cn.leancloud.LeanCloud;
import cn.leancloud.im.LCIMOptions;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // bmob和leancloud初始化
        Bmob.initialize(this, "313ec8bfa0fb3fbc37986bec8dde3149");
        // 开启调试日志
        LeanCloud.setLogLevel(LCLogger.Level.DEBUG);
        // 初始化
        LeanCloud.initialize(this,"sj3RjQUCn3sY2JAsFGhPU1ug-MdYXbMMI", "W15suVMgq3oBPJVjPOI38NKM");
        // 不使用推送功能
        LCIMOptions.getGlobalOptions().setDisableAutoLogin4Push(true);
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(2000);
                    // 判断用户是否登录
                    LCUser currentUser = LCUser.getCurrentUser();
                    if (currentUser == null) {
                        // 没有登录，进入登录界面
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        // 已经登录，进入主界面
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    protected void initView() {
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        //     // 透明状态栏
        //     getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //     // 透明导航栏
        //     getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        // }
        // 设置全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }
}