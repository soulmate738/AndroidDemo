package com.example.sparkchaindemo.entity;

/**
 * @author anjia
 */
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;

public class PostViewModel extends ViewModel {
    private List<Post> postList = new ArrayList<>();
    private boolean isDataLoaded = false;

    public List<Post> getPostList() {
        return postList;
    }

    public boolean isDataLoaded() {
        return isDataLoaded;
    }

    public void setDataLoaded(boolean dataLoaded) {
        isDataLoaded = dataLoaded;
    }

    // 添加新帖子（可选封装方法）
    public void addPost(Post post) {
        postList.add(0, post); // 新帖子添加到顶部
    }

    // 更新指定位置的帖子
    public void updatePost(int position, Post updatedPost) {
        if (position >= 0 && position < postList.size()) {
            postList.set(position, updatedPost);
        }
    }
}