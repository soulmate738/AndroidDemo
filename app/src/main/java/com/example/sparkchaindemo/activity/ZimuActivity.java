package com.example.sparkchaindemo.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;

import com.example.sparkchaindemo.R;
import com.example.sparkchaindemo.base.BaseActivity;
import com.example.sparkchaindemo.services.FloatingWindowService;
import com.example.sparkchaindemo.ui.FlowLayout;

public class ZimuActivity extends BaseActivity {
    private String TAG = "ZimuActivity";
    private ImageView mBack,mClose;
    private CardView mStart;
    private TextView mConnect;
    private WindowManager windowManager;
    private FlowLayout mFlowLayout;
    private static final int REQUEST_CODE = 1001;
    private static final int OVERLAY_PERMISSION_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void initView() {
        setContentView(R.layout.activity_zimu);
        mBack = findViewById(R.id.iv_back);
        mClose = findViewById(R.id.iv_close);
        mStart = findViewById(R.id.start_zimu);
        mConnect = findViewById(R.id.tv_zimu_content);
        // mFlowLayout = findViewById(R.id.layout_flow);
    }

    @Override
    protected void initData() {
        // 检查悬浮窗权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 1);
        } else {
            // startFloatingService();
        }
    }

    @Override
    protected void initListener() {
        mBack.setOnClickListener(v -> finish());
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // initFloatWindow();
                startFloatingService();

            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
                startFloatingService();
            } else {
                Toast.makeText(this, "需要悬浮窗权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startFloatingService() {
        Intent intent = new Intent(this, FloatingWindowService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }
}
