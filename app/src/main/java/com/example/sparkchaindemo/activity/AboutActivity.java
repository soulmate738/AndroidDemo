package com.example.sparkchaindemo.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sparkchaindemo.R;
import com.example.sparkchaindemo.base.BaseActivity;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_about);
            // 设置返回按钮
            ImageView backButton = findViewById(R.id.backButton);
            backButton.setOnClickListener(v -> onBackPressed());

            // 设置版本信息
            TextView versionText = findViewById(R.id.versionText);
            try {
                String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                versionText.setText("版本号: " + versionName);
            } catch (Exception e) {
                versionText.setText("版本号: 1.0.0");
            }

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }
}