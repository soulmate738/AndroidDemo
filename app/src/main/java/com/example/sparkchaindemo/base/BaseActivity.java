package com.example.sparkchaindemo.base;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sparkchaindemo.net.NetBroadcastReceiver;
import com.example.sparkchaindemo.utils.ActivityUtil;


/**
 * @author anjia
 */
public abstract class BaseActivity extends AppCompatActivity implements NetBroadcastReceiver.NetChangeListener {
    public static NetBroadcastReceiver.NetChangeListener netEvent;// 网络状态改变监听事件
    protected final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle
                                    savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
        initListener();
        // 隐藏标题栏
        // if (getSupportActionBar() != null)
        //     getSupportActionBar().hide();
        // 沉浸效果
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        //     // 透明状态栏
        //     getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //     // 透明导航栏
        //     getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        // }

        // 添加到Activity工具类
        ActivityUtil.getInstance().addActivity(this);

        // 初始化netEvent
        netEvent = this;

    }

    protected abstract void initView();
    protected abstract void initData();
    protected abstract void initListener();



    protected void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "--------onRestart()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "--------onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "--------onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "--------onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "--------onStop()");
    }

    @Override
    protected void onDestroy() {
        // Activity销毁时，提示系统回收
        // System.gc();
        netEvent = null;
        // 移除Activity
        ActivityUtil.getInstance().removeActivity(this);
        super.onDestroy();
        Log.d(TAG, "--------onDestroy()");
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 点击手机上的返回键，返回上一层
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 移除Activity
            ActivityUtil.getInstance().removeActivity(this);
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    /**
     * 网络状态改变时间监听
     *
     * @param netWorkState true有网络，false无网络
     */
    @Override
    public void onNetChange(boolean netWorkState) {
    }
}
