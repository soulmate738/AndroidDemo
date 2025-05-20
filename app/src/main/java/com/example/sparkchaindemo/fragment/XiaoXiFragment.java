package com.example.sparkchaindemo.fragment;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sparkchaindemo.R;
import com.example.sparkchaindemo.activity.ChatActivity;
import com.example.sparkchaindemo.adapter.ChatListAdapter;
import com.example.sparkchaindemo.entity.ChatItem;

import java.util.ArrayList;
import java.util.List;

public class XiaoXiFragment extends Fragment implements ChatListAdapter.OnItemActionListener {
    private String TAG = "XiaoXiFragment";
    private RecyclerView recyclerView;
    private ChatListAdapter adapter;
    private List<ChatItem> chatItems;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_xiao_xi, container, false);
        initData();
        initViews();
        return view;
    }

    private void initData() {
        chatItems = new ArrayList<>(); // 初始化列表
        chatItems.add(new ChatItem("小聆心", R.mipmap.ic_lxq, "你好", "2025-05-01 10:00"));
        chatItems.add(new ChatItem("小张", R.mipmap.head1, "你好", "2025-05-01 10:00"));

    }

    private void initViews() {
        recyclerView = view.findViewById(R.id.rv_chat_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ChatListAdapter(chatItems, this);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(ChatItem chatItem, int position) {
        // 只传递昵称到聊天界面
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("name", chatItem.getName());
        startActivity(intent);
    }
}