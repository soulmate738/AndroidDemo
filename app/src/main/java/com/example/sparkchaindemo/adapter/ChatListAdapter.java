package com.example.sparkchaindemo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.sparkchaindemo.R;
import com.example.sparkchaindemo.entity.ChatItem;

import java.util.ArrayList;
import java.util.List;


public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {

    private List<ChatItem> chatItems;
    private ChatListAdapter.OnItemActionListener listener;
    public ChatListAdapter(List<ChatItem> chatItems,  ChatListAdapter.OnItemActionListener listener) {
        this.listener = listener;
        this.chatItems = chatItems != null ? chatItems : new ArrayList<>(); // 确保不为 null
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_list, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatItem item = chatItems.get(position);
        holder.avatar.setImageResource(item.getAvatarResId());
        holder.name.setText(item.getName());
        holder.message.setText(item.getLastMessage());
        holder.time.setText(item.getTime());
        holder.layout.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(item, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatItems.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView name;
        TextView message;
        TextView time;
        LinearLayout layout;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            name = itemView.findViewById(R.id.name);
            message = itemView.findViewById(R.id.message);
            time = itemView.findViewById(R.id.time);
            layout = itemView.findViewById(R.id.item_chat_list);
        }
    }

    public interface OnItemActionListener {
        void onClick(ChatItem chatItem, int position);
    }
}