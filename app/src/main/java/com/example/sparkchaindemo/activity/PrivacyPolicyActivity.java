package com.example.sparkchaindemo.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import com.example.sparkchaindemo.R;

public class PrivacyPolicyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        // 返回按钮
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        // 加载模拟隐私政策内容
        TextView privacyText = findViewById(R.id.privacyText);
        privacyText.setText(getString(R.string.privacy_policy));
    }
}
