package com.example.sparkchaindemo.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;


import com.example.sparkchaindemo.R;

public class TermsOfServiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_of_service);

        // 返回按钮
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        // 加载模拟协议内容
        TextView termsText = findViewById(R.id.termsText);
        termsText.setText(getString(R.string.terms_of_service));
    }
}