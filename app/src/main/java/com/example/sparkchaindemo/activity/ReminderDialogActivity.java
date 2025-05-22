package com.example.sparkchaindemo.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.sparkchaindemo.R;

public class ReminderDialogActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_dialog);

        // // 设置为对话框样式
        // getWindow().setFlags(
        //         WindowManager.LayoutParams.FLAG_FULLSCREEN |
        //                 WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
        //                 WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
        //                 WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
        //         WindowManager.LayoutParams.FLAG_FULLSCREEN |
        //                 WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
        //                 WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
        //                 WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        // );

        String name = getIntent().getStringExtra("name");
        TextView titleTextView = findViewById(R.id.dialog_title);
        titleTextView.setText("日程提醒");

        TextView messageTextView = findViewById(R.id.dialog_message);
        messageTextView.setText("您有一个日程: " + name);

        Button dismissButton = findViewById(R.id.dialog_dismiss);
        dismissButton.setOnClickListener(v -> finish());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 发送广播通知服务弹窗已关闭
        Intent intent = new Intent("ACTION_DIALOG_DISMISSED");
        sendBroadcast(intent);
    }
}