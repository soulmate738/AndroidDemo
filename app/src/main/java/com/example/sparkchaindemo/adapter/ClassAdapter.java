package com.example.sparkchaindemo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sparkchaindemo.R;
import com.example.sparkchaindemo.entity.ClassItem;

import java.util.List;

/**
 * @author anjia
 */
public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {
    private Context mContext;
    private List<ClassItem> items;
    private OnItemClickListener mListener;

    public ClassAdapter(List<ClassItem> data) {
        items = data;
    }

    // 设置点击事件监听器的方法
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    // 定义点击事件接口
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public static class ClassViewHolder extends RecyclerView.ViewHolder {
        public TextView itemTitle;
        public ImageView itemStatus, itemYoujiantou;

        public ClassViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            itemTitle = itemView.findViewById(R.id.mainitem_tv_title);
            itemStatus = itemView.findViewById(R.id.mainitem_iv_status);
            itemYoujiantou = itemView.findViewById(R.id.mainitem_iv_youjiantou);

            // 设置整个item的点击事件
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    @SuppressLint({"MissingInflatedId", "ResourceType"})
    @NonNull
    @Override
    public ClassAdapter.ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_class, parent, false);
        return new ClassViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassAdapter.ClassViewHolder holder, int position) {
        ClassItem mainItem = items.get(position);
        holder.itemTitle.setText(mainItem.getTitle());
        if (mainItem.getStatus() == 0) {
            holder.itemStatus.setImageResource(R.mipmap.tongguo);
        } else if (mainItem.getStatus() == 1) {
            holder.itemStatus.setImageResource(R.mipmap.doing);
            holder.itemTitle.setBackgroundResource(R.drawable.class_doing);
        } else if (mainItem.getStatus() == 2) {
            holder.itemStatus.setImageResource(R.mipmap.lock);
        }
        holder.itemYoujiantou.setImageResource(R.mipmap.youjiantou);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}