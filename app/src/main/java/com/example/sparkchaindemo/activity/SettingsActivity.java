package com.example.sparkchaindemo.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import com.example.sparkchaindemo.R;
import com.example.sparkchaindemo.base.BaseActivity;


public class SettingsActivity extends BaseActivity {
    private SharedPreferences preferences;
    private static final String PREFS_NAME = "HearingAidPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_settings);
        // 返回按钮
        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());

        // 隐私政策
        findViewById(R.id.privacySetting).setOnClickListener(v -> {
            // openWebPage("https://example.com/privacy");
            Intent intent = new Intent(this, PrivacyPolicyActivity.class);
            startActivity(intent);
        });


        // 清除缓存
        findViewById(R.id.clearCacheSetting).setOnClickListener(v -> {
            clearAppCache();
            showToast("缓存已清除");
        });

        // 用户协议
        findViewById(R.id.termsSetting).setOnClickListener(v -> {
            Intent intent = new Intent(this, TermsOfServiceActivity.class);
            startActivity(intent);
        });



        // 检查更新
        findViewById(R.id.checkUpdateSetting).setOnClickListener(v -> {
            showToast("检查更新功能将在后续版本中推出");
        });

        // 权限管理
        findViewById(R.id.permissionsSetting).setOnClickListener(v -> {
            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        });
    }
    private void openWebPage(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            showToast("无法打开网页");
        }
    }

    private void clearAppCache() {
        // 实际项目中应实现缓存清除逻辑
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }
}