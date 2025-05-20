package com.example.sparkchaindemo.adapter;

/**
 * @author anjia
 */

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sparkchaindemo.R;
import com.example.sparkchaindemo.entity.ChatMessage;

import java.util.ArrayList;
import java.util.List;


/**
 * @author anjia
 * @date 2024/11/1 15:43
 */
public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ViewHolder> {
    private List<ChatMessage> chatMessageList ;


    public ChatMessageAdapter(List<ChatMessage> list, Activity context) {
        this.chatMessageList = list != null ? list : new ArrayList<>(); // 确保不为 null

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftlayout;
        LinearLayout rightlayout;
        TextView leftMsg;
        TextView rightMsg;
        public ViewHolder(View view) {
            super(view);
            leftlayout = (LinearLayout) view.findViewById(R.id.left_layout);
            rightlayout = (LinearLayout) view.findViewById(R.id.right_layout);
            leftMsg = (TextView) view.findViewById(R.id.left_msg);
            rightMsg = (TextView) view.findViewById(R.id.right_msg);
        }

    }


    @NonNull
    @Override
    public ChatMessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate
                (R.layout.item_chat_message_send, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessageList.get(position);
        if (chatMessage.getType() == ChatMessage.TYPE_RECEIVED && chatMessage.getMessageType() == ChatMessage.TYPE_TEXT) {
            // 收到文字
            holder.leftlayout.setVisibility(View.VISIBLE);
            holder.leftMsg.setVisibility(View.VISIBLE);
            holder.rightlayout.setVisibility(View.GONE);
            holder.leftMsg.setText(chatMessage.getContent());

        } else if (chatMessage.getType() == ChatMessage.TYPE_SENT && chatMessage.getMessageType() == ChatMessage.TYPE_TEXT) {
            // 发出文字
            holder.rightlayout.setVisibility(View.VISIBLE);
            holder.rightMsg.setVisibility(View.VISIBLE);
            holder.leftlayout.setVisibility(View.GONE);
            holder.rightMsg.setText(chatMessage.getContent());
        }

    }

    @Override
    public int getItemCount() {
        return chatMessageList.size();
    }

}
