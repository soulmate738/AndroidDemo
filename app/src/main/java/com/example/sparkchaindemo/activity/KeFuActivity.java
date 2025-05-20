package com.example.sparkchaindemo.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.sparkchaindemo.R;
import com.example.sparkchaindemo.base.BaseActivity;

public class KeFuActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFaqItems();

    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_ke_fu);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {
// 返回按钮逻辑
        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());
        Button callButton = findViewById(R.id.callButton);
        callButton.setOnClickListener(v -> {
            // 拨打电话
            String phoneNumber = "4008008888"; // 客服电话号码
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
        });
    }
    private void setupFaqItems() {
        LinearLayout faqContainer = findViewById(R.id.faqContainer);

        // 添加常见问题
        addFaqItem(faqContainer, "如何使用语音转文字功能？",
                "1. 打开主界面\n2. 点击底部的\"语音\"按钮\n3. 说话时系统会自动将语音转换为文字\n4. 长按文字可复制或分享");

        addFaqItem(faqContainer, "听不到震动提醒怎么办？",
                "1. 确保手机已开启震动模式\n2. 在应用设置中检查\"震动提醒\"是否已开启\n3. 调高手机的震动强度\n4. 重启应用和手机");

        addFaqItem(faqContainer, "手势识别不准确如何解决？",
                "1. 在光线充足的环境中使用\n2. 确保手势清晰、缓慢地完成\n3. 到\"我的-手势管理\"中重新训练常用手势\n4. 更新应用到最新版本");

        addFaqItem(faqContainer, "如何设置紧急联系人？",
                "1. 打开\"我的\"页面\n2. 点击\"紧急联系人\"选项\n3. 添加联系人并设置优先级\n4. 可设置最多5个紧急联系人");
    }

    private void addFaqItem(LinearLayout container, String question, String answer) {
        View faqItem = getLayoutInflater().inflate(R.layout.item_faq, container, false);

        TextView questionText = faqItem.findViewById(R.id.questionText);
        TextView answerText = faqItem.findViewById(R.id.answerText);
        LinearLayout answerContainer = faqItem.findViewById(R.id.answerContainer);

        questionText.setText(question);
        answerText.setText(answer);

        // 点击问题展开/收起答案
        questionText.setOnClickListener(v -> {
            if (answerContainer.getVisibility() == View.GONE) {
                answerContainer.setVisibility(View.VISIBLE);
                questionText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expanded, 0);
            } else {
                answerContainer.setVisibility(View.GONE);
                questionText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_collapsed, 0);
            }
        });

        container.addView(faqItem);
    }
}