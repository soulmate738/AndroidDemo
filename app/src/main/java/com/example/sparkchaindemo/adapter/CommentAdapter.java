package com.example.sparkchaindemo.adapter;

/**
 * @author anjia
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sparkchaindemo.R;
import com.example.sparkchaindemo.entity.Comment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context context;
    private List<Comment> commentList;
    private OnCommentInteractionListener listener;

    public CommentAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    public void setListener(OnCommentInteractionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);

        holder.ivAvatar.setImageResource(comment.getAvatarResId());
        holder.tvNickname.setText(comment.getNickname());
        holder.tvContent.setText(comment.getContent());
        holder.tvLikeCount.setText(String.valueOf(comment.getLikeCount()));

        // 设置时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        holder.tvTime.setText(sdf.format(new Date(comment.getTimestamp())));

        // 设置点赞状态
        if (comment.isLiked()) {
            holder.ivLike.setImageResource(R.mipmap.heart3);
        } else {
            holder.ivLike.setImageResource(R.mipmap.heart);
        }

        // 设置回复内容
        if (comment.getReplyContent() != null && !comment.getReplyContent().isEmpty()) {
            holder.llReplyContainer.setVisibility(View.VISIBLE);
            holder.tvReplyContent.setText(comment.getReplyContent());
        } else {
            holder.llReplyContainer.setVisibility(View.GONE);
        }

        // 设置点赞点击事件
        holder.llLike.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLikeComment(position);
            }
        });

        // 设置回复点击事件
        holder.tvReply.setOnClickListener(v -> {
            if (listener != null) {
                listener.onReplyComment(position, comment.getNickname());
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public interface OnCommentInteractionListener {
        void onLikeComment(int position);
        void onReplyComment(int position, String nickname);
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar, ivLike;
        TextView tvNickname, tvTime, tvContent, tvLikeCount, tvReply, tvReplyContent;
        LinearLayout llLike, llReplyContainer;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_comment_avatar);
            ivLike = itemView.findViewById(R.id.iv_comment_like);
            tvNickname = itemView.findViewById(R.id.tv_comment_nickname);
            tvTime = itemView.findViewById(R.id.tv_comment_time);
            tvContent = itemView.findViewById(R.id.tv_comment_content);
            tvLikeCount = itemView.findViewById(R.id.tv_comment_like_count);
            tvReply = itemView.findViewById(R.id.tv_reply);
            tvReplyContent = itemView.findViewById(R.id.tv_reply_content);
            llLike = itemView.findViewById(R.id.ll_comment_like);
            llReplyContainer = itemView.findViewById(R.id.ll_reply_container);
        }
    }
}
