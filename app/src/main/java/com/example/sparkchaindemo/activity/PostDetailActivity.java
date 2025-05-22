package com.example.sparkchaindemo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.sparkchaindemo.R;
import com.example.sparkchaindemo.adapter.CommentAdapter;
import com.example.sparkchaindemo.entity.Comment;
import com.example.sparkchaindemo.entity.Post;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity implements CommentAdapter.OnCommentInteractionListener {

    private Post post;
    private List<Comment> commentList = new ArrayList<>();
    private CommentAdapter commentAdapter;
    private EditText etComment;
    private Button btnSendComment;
    private LinearLayout llNoComments;
    private TextView tvCommentCount;
    private int currentReplyPosition = -1;
    private String currentReplyTo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        // 获取传递的Post对象
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("post")) {
            post = intent.getParcelableExtra("post");
        } else {
            // 如果没有传递Post对象，关闭页面
            finish();
            return;
        }

        initView();
        initData();
        initListeners();
    }

    private void initView() {
        // 初始化UI组件
        ImageView btnBack = findViewById(R.id.btn_back);
        ImageView ivAvatar = findViewById(R.id.iv_detail_avatar);
        TextView tvNickname = findViewById(R.id.tv_detail_nickname);
        TextView tvTime = findViewById(R.id.tv_detail_time);
        TextView tvContent = findViewById(R.id.tv_detail_content);
        ImageView ivMedia = findViewById(R.id.iv_detail_media);
        ImageView ivLike = findViewById(R.id.iv_detail_like);
        TextView tvLikeCount = findViewById(R.id.tv_detail_like_count);
        RecyclerView rvComments = findViewById(R.id.rv_comments);
        // setRecyclerViewHeight(rvComments);

        llNoComments = findViewById(R.id.ll_no_comments);
        tvCommentCount = findViewById(R.id.tv_comment_count);
        etComment = findViewById(R.id.et_comment);
        btnSendComment = findViewById(R.id.btn_send_comment);
        LinearLayout llLike = findViewById(R.id.ll_like);

        // 设置帖子信息
        ivAvatar.setImageResource(post.getAvatarUrl());
        tvNickname.setText(post.getNickname());

        // 格式化时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        tvTime.setText(sdf.format(new Date(post.getTimestamp())));

        tvContent.setText(post.getContent());

        // 设置媒体内容（图片）
        if (post.getMediaUrl() != null && !post.getMediaUrl().isEmpty()) {
            ivMedia.setVisibility(View.VISIBLE);
            Uri contentUri = Uri.parse(post.getMediaUrl());
            Glide.with(this)
                    .load(contentUri)
                    .into(ivMedia);
            Log.d("ImageURI", "原始 URI: " + post.getMediaUrl());
            Log.d("ImageURI", "路径: " + contentUri.getPath()); // 输出类似 /storage/emulated/0/DCIM/Screenshots/...
        } else {
            ivMedia.setVisibility(View.GONE);
            ivMedia.setImageResource(R.mipmap.ic_lxq);
        }

        // 设置点赞信息
        tvLikeCount.setText(String.valueOf(post.getLikeCount()));


        // 设置评论列表
        commentAdapter = new CommentAdapter(this, commentList);
        commentAdapter.setListener(this);

        rvComments.setLayoutManager(new LinearLayoutManager(this));
        rvComments.setAdapter(commentAdapter);


        // 返回按钮
        btnBack.setOnClickListener(v -> finish());

        llLike.setOnClickListener(v -> {
            post.setLiked(!post.isLiked()); // 直接修改原对象的点赞状态
            post.setLikeCount(post.getLikeCount() + (post.isLiked() ? 1 : -1));
            tvLikeCount.setText(String.valueOf(post.getLikeCount()));
        });
    }
    // 在 PostDetailActivity 中初始化 RecyclerView 后调用
    private void setRecyclerViewHeight(RecyclerView recyclerView) {
        CommentAdapter adapter = (CommentAdapter) recyclerView.getAdapter();
        if (adapter == null) return;

        int totalHeight = 0;
        for (int i = 0; i < adapter.getItemCount(); i++) {
            View itemView = adapter.onCreateViewHolder(recyclerView, adapter.getItemViewType(i)).itemView;
            itemView.measure(0, 0);
            totalHeight += itemView.getMeasuredHeight();
        }

        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) recyclerView.getLayoutParams();
        params.height = totalHeight + recyclerView.getPaddingTop() + recyclerView.getPaddingBottom();
        recyclerView.setLayoutParams(params);
    }
    private void initData() {
        // 清空原有数据（避免重复模拟）
        commentList.clear();

        // 获取帖子中携带的评论数量
        int commentCount = post.getCommentCount();
        // 新建头像数组
        int head[] = {R.mipmap.head4, R.mipmap.head2, R.mipmap.head3};
        // 生成模拟评论数据（根据评论数量动态创建）
        for (int i = 0; i < commentCount; i++) {
            Comment comment = new Comment(
                    head[i%3], // 循环使用3种头像
                    "用户" + (i + 1), // 用户昵称递增
                    "评论内容 " + (i + 1), // 评论内容递增
                    System.currentTimeMillis() - i * 3600000, // 时间递减（最近的评论在前）
                    i % 5, // 随机点赞数（0-4）
                    false, // 随机点赞状态（偶数为已点赞）
                    i % 2 == 1 ? "作者回复：感谢评论！" : "" // 随机添加作者回复
            );
            commentList.add(comment);
        }

        // 通知适配器数据变更
        commentAdapter.notifyDataSetChanged();
        updateCommentCount();
        checkEmptyComments();
    }



    private void initListeners() {
        // 监听评论输入框
        etComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnSendComment.setEnabled(s.length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 发送评论按钮
        btnSendComment.setOnClickListener(v -> {
            String commentText = etComment.getText().toString().trim();
            if (!commentText.isEmpty()) {
                addComment(commentText);
                etComment.setText("");
                currentReplyPosition = -1;
                currentReplyTo = "";
            }
        });
    }

    private void addComment(String commentText) {
        // 构建新评论
        Comment newComment;
        if (currentReplyPosition >= 0) {
            // 如果是回复某条评论
            Comment repliedComment = commentList.get(currentReplyPosition);
            newComment = new Comment(
                    R.mipmap.head1, // 当前用户头像
                    "当前用户", // 当前用户名
                    commentText,
                    System.currentTimeMillis(),
                    0,
                    false,
                    ""
            );

            // 更新被回复的评论，添加回复内容
            repliedComment.setReplyContent("回复 @" + newComment.getNickname() + ": " + commentText);
            commentAdapter.notifyItemChanged(currentReplyPosition);
        } else {
            // 如果是直接评论
            newComment = new Comment(
                    R.mipmap.head1, // 当前用户头像
                    "旋风小陀螺", // 当前用户名
                    commentText,
                    System.currentTimeMillis(),
                    0,
                    false,
                    ""
            );
        }

        // 添加新评论到列表
        commentList.add(0, newComment);
        commentAdapter.notifyItemInserted(0);

        // 更新评论数量
        post.setCommentCount(post.getCommentCount() + 1);
        updateCommentCount();
        checkEmptyComments();
    }

    private void updateCommentCount() {
        tvCommentCount.setText("(" + post.getCommentCount() + ")");
    }

    private void checkEmptyComments() {
        if (commentList.isEmpty()) {
            llNoComments.setVisibility(View.VISIBLE);
        } else {
            llNoComments.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLikeComment(int position) {
        Comment comment = commentList.get(position);
        comment.setLiked(!comment.isLiked());
        comment.setLikeCount(comment.getLikeCount() + (comment.isLiked() ? 1 : -1));
        commentAdapter.notifyItemChanged(position);
    }

    @Override
    public void onReplyComment(int position, String nickname) {
        currentReplyPosition = position;
        currentReplyTo = nickname;
        etComment.setHint("回复 @" + nickname + ":");
        etComment.requestFocus();
    }

    @Override
    public void onBackPressed() {
        // 创建返回数据的 Intent
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updatedPost", post); // 将更新后的 Post 对象返回
        resultIntent.putExtra("position", getIntent().getIntExtra("position", -1));
        setResult(RESULT_OK, resultIntent);
        Log.d("PostDetailActivity", "onBackPressed: 退出");
        super.onBackPressed();
    }
}