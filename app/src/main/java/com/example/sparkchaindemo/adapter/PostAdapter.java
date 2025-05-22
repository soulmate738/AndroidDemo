package com.example.sparkchaindemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sparkchaindemo.R;
import com.example.sparkchaindemo.entity.ClassItem;
import com.example.sparkchaindemo.entity.Post;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author anjia
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private List<Post> postList;
    private Context context;
    private PostAdapter.OnItemClickListener mListener;


    // 设置点击事件监听器的方法
    public void setOnItemClickListener(PostAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    // 定义点击事件接口
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public PostAdapter(List<Post> postList, Context context) {
        this.postList = postList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.tvNickname.setText(post.getNickname());
        holder.tvContent.setText(post.getContent());
        holder.tvTime.setText(formatTime(post.getTimestamp()));
        holder.tvLikeCount.setText(String.valueOf(post.getLikeCount()));
        holder.tvCommentCount.setText(String.valueOf(post.getCommentCount()));

        // 加载头像（需使用Glide/Picasso等图片库）
        Glide.with(context)
                .load(post.getAvatarUrl())
                .placeholder(R.mipmap.banner1)
                .error(R.mipmap.banner1)
                .into(holder.ivAvatar);

        // 处理媒体内容
        String mediaUrl = post.getMediaUrl();
        if (!mediaUrl.isEmpty()) {
            holder.flMedia.setVisibility(View.VISIBLE);
            holder.ivMediaImage.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(mediaUrl)
                    .placeholder(R.mipmap.banner2)
                    .error(R.mipmap.banner2)
                    .into(holder.ivMediaImage);
        } else {
            holder.flMedia.setVisibility(View.GONE);
        }

        // 点赞点击事件
        holder.ivLike.setOnClickListener(v -> {
            if (!post.isLiked()) {
                post.setLikeCount(post.getLikeCount() + 1);
                post.setLiked(true);
                notifyItemChanged(position);
            } else {
                post.setLikeCount(post.getLikeCount() - 1);
                post.setLiked(false);
                notifyItemChanged(position);
            }
        }); // 设置整个item的点击事件


    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView ivAvatar;
        TextView tvNickname, tvTime, tvContent;
        FrameLayout flMedia;
        ImageView ivMediaImage;
        ImageView ivLike;
        TextView tvLikeCount;
        ImageView ivComment;
        TextView tvCommentCount;

        public ViewHolder(@NonNull View itemView, final PostAdapter.OnItemClickListener listener) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            tvNickname = itemView.findViewById(R.id.tv_nickname);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvContent = itemView.findViewById(R.id.tv_content);
            flMedia = itemView.findViewById(R.id.fl_media);
            ivMediaImage = itemView.findViewById(R.id.iv_media_image);
            ivLike = itemView.findViewById(R.id.iv_like);
            tvLikeCount = itemView.findViewById(R.id.tv_like_count);
            ivComment = itemView.findViewById(R.id.iv_comment);
            tvCommentCount = itemView.findViewById(R.id.tv_comment_count);
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

    private String formatTime(long timestamp) {
        long currentTime = System.currentTimeMillis();
        long diff = currentTime - timestamp;

        // 1分钟内
        if (diff < 60 * 1000) {
            return "刚刚";
        }

        // 1小时内
        if (diff < 60 * 60 * 1000) {
            long minutes = diff / (60 * 1000);
            return minutes + "分钟前";
        }

        // 24小时内
        if (diff < 24 * 60 * 60 * 1000) {
            long hours = diff / (60 * 60 * 1000);
            return hours + "小时前";
        }

        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTimeInMillis(currentTime);

        Calendar postCalendar = Calendar.getInstance();
        postCalendar.setTimeInMillis(timestamp);

        // 判断是否为同一天
        if (currentCalendar.get(Calendar.YEAR) == postCalendar.get(Calendar.YEAR) &&
                currentCalendar.get(Calendar.DAY_OF_YEAR) == postCalendar.get(Calendar.DAY_OF_YEAR)) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            return sdf.format(new Date(timestamp));
        }

        // 判断是否为昨天
        currentCalendar.add(Calendar.DAY_OF_YEAR, -1);
        if (currentCalendar.get(Calendar.YEAR) == postCalendar.get(Calendar.YEAR) &&
                currentCalendar.get(Calendar.DAY_OF_YEAR) == postCalendar.get(Calendar.DAY_OF_YEAR)) {
            return "昨天";
        }

        // 判断是否为前天
        currentCalendar.add(Calendar.DAY_OF_YEAR, -1);
        if (currentCalendar.get(Calendar.YEAR) == postCalendar.get(Calendar.YEAR) &&
                currentCalendar.get(Calendar.DAY_OF_YEAR) == postCalendar.get(Calendar.DAY_OF_YEAR)) {
            return "前天";
        }

        // 判断是否为同一年
        if (currentCalendar.get(Calendar.YEAR) == postCalendar.get(Calendar.YEAR)) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd", Locale.getDefault());
            return sdf.format(new Date(timestamp));
        }

        // 不同年份
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}
