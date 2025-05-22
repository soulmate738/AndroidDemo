package com.example.sparkchaindemo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sparkchaindemo.R;
import com.example.sparkchaindemo.adapter.ChatMessageAdapter;
import com.example.sparkchaindemo.base.BaseActivity;
import com.example.sparkchaindemo.entity.ChatItem;
import com.example.sparkchaindemo.entity.ChatMessage;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.ai.ChatMessageListener;


public class ChatActivity extends BaseActivity {

    private RecyclerView rvChatMessages;
    private EditText etMessage;
    private Button btnSend;
    private ChatMessageAdapter adapter;
    private ChatItem chatItem;
    private List<ChatMessage> messages;
    private String partnerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        partnerName = intent.getStringExtra("name");
        initView();
        initData();
        initListener();
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_chat);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(partnerName);
        findViewById(R.id.btn_back).setOnClickListener(v -> onBackPressed());
        rvChatMessages = findViewById(R.id.rv_chat_messages);
        rvChatMessages.setLayoutManager(new LinearLayoutManager(this));
        messages = new ArrayList<>();
        adapter = new ChatMessageAdapter(messages, this);
        rvChatMessages.setAdapter(adapter);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);
    }


    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {
        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        // 将消息发送到聊天界面
        String sendMessage = etMessage.getText().toString().trim();
        if (!TextUtils.isEmpty(sendMessage)) {
            ChatMessage chatMessage = new ChatMessage(sendMessage, ChatMessage.TYPE_TEXT, ChatMessage.TYPE_SENT, 0, null);
            messages.add(chatMessage);
            adapter.notifyItemInserted(messages.size() - 1);
            rvChatMessages.scrollToPosition(messages.size() - 1);
            etMessage.setText("");
        }
        if (partnerName.equals("小聆心")) {
            // 启用ai聊天
            //连接AI服务器（这个代码为了防止AI连接中断，因为可能会存在某些情况下，比如网络切换、中断等，导致心跳连接失败）
            SplashActivity.bmobAI.Connect();
            //发送对话信息
            SplashActivity.bmobAI.Chat(sendMessage, "session_id", new ChatMessageListener() {
                @Override
                public void onMessage(String message) {
                    //消息流的形式返回AI的结果
                    Log.d("Bmob", message);
                }

                @Override
                public void onFinish(String message) {
                    //一次性返回全部结果，这个方法需要等待一段时间，友好性较差
                    if (!TextUtils.isEmpty(message)) {
                        runOnUiThread(() -> {
                            ChatMessage chatMessage = new ChatMessage(message, ChatMessage.TYPE_TEXT, ChatMessage.TYPE_RECEIVED, 0, null);
                            messages.add(chatMessage);
                            adapter.notifyItemInserted(messages.size() - 1);
                            rvChatMessages.scrollToPosition(messages.size() - 1);
                        });
                    }
                    Log.d("Bmob", message);
                }

                @Override
                public void onError(String error) {
                    //OpenAI的密钥错误或者超过OpenAI并发时，会返回这个错误
                    Log.d("Bmob", "连接发生异常了"+error);
                }

                @Override
                public void onClose() {
                    Log.d("Bmob", "连接被关闭了");
                }
            });
        } else {
            ChatMessage message111 = new ChatMessage(sendMessage, ChatMessage.TYPE_TEXT, ChatMessage.TYPE_RECEIVED, 0, null);
            messages.add(message111);
            adapter.notifyItemInserted(messages.size() - 1);
            rvChatMessages.scrollToPosition(messages.size() - 1);
        }
    }
}