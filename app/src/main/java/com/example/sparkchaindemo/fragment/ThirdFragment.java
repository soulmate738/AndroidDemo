package com.example.sparkchaindemo.fragment;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sparkchaindemo.R;
import com.example.sparkchaindemo.activity.PostDetailActivity;
import com.example.sparkchaindemo.activity.PublishPostActivity;
import com.example.sparkchaindemo.adapter.PostAdapter;
import com.example.sparkchaindemo.entity.Post;
import com.example.sparkchaindemo.entity.PostViewModel;

public class ThirdFragment extends Fragment {
    private RecyclerView rvPosts;
    private PostAdapter adapter;
    private PostViewModel viewModel;
    private ActivityResultLauncher<Intent> publishPostLauncher;
    private ActivityResultLauncher<Intent> postDetailLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_third, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(PostViewModel.class);
        initActivityResultLaunchers();
    }

    private void initView(View view) {
        rvPosts = view.findViewById(R.id.rv_posts);
        ImageButton btnAddPost = view.findViewById(R.id.btn_add_post);

        // 使用ViewModel的列表数据
        adapter = new PostAdapter(viewModel.getPostList(), requireContext());
        adapter.setOnItemClickListener((position) -> {
            Intent intent = new Intent(requireContext(), PostDetailActivity.class);
            intent.putExtra("post", viewModel.getPostList().get(position));
            intent.putExtra("position", position);
            postDetailLauncher.launch(intent);
        });

        rvPosts.setAdapter(adapter);
        btnAddPost.setOnClickListener(v -> publishPostLauncher.launch(new Intent(requireContext(), PublishPostActivity.class)));

        // 首次加载数据
        if (!viewModel.isDataLoaded()) {
            mockData();
            viewModel.setDataLoaded(true);
        }
    }

    private void initActivityResultLaunchers() {
        // 处理发布动态结果
        publishPostLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                (ActivityResult result) -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Post newPost = data.getParcelableExtra("new_post");
                            if (newPost != null) {
                                viewModel.addPost(newPost);
                                adapter.notifyItemInserted(0);
                                rvPosts.scrollToPosition(0);
                                Toast.makeText(requireContext(), "发布成功", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );

        // 处理详情页返回结果
        postDetailLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                (ActivityResult result) -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Post updatedPost = data.getParcelableExtra("updatedPost");
                            int position = data.getIntExtra("position", -1);
                            if (position != -1 && updatedPost != null) {
                                viewModel.updatePost(position, updatedPost);
                                adapter.notifyItemChanged(position);
                            }
                        }
                    }
                }
        );
    }

    private void mockData() {
        String uri1="https://picsum.photos/seed/post1/800/600";
        String uri2="https://picsum.photos/seed/post2/800/600";

        viewModel.getPostList().add(new Post(R.mipmap.head2, "我今年十六岁", "今天天气真好！", uri1, System.currentTimeMillis(),2, 3 ,false));
        viewModel.getPostList().add(new Post(R.mipmap.head4, "我今年十六岁", "今天吃什么？", uri2, System.currentTimeMillis() - 3600000, 2, 2,false));
        adapter.notifyDataSetChanged();
    }

    // 移除onActivityResult方法，已完全由ActivityResultLauncher处理
}