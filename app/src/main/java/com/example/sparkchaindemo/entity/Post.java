package com.example.sparkchaindemo.entity;

import android.net.Uri;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;


/**
 * @author anjia
 */public class Post implements Parcelable {
    public int getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(int avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }


    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    // 原有成员变量...
    private int avatarUrl;
    private String nickname;
    private String content;
    private String mediaUrl;       // 图片或视频URL（空字符串表示无媒体）
    private long timestamp;
    private int likeCount;
    private int commentCount;

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    private boolean isLiked;
    public Post(Parcel in) {
        avatarUrl = in.readInt();
        nickname = in.readString();
        content = in.readString();
        mediaUrl = in.readString();
        timestamp = in.readLong();
        likeCount = in.readInt();
        commentCount = in.readInt();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            isLiked = in.readBoolean();
        }
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    public Post(int head, String name, String content, String mediaUrl, long time, int likeCount, int commentCount, boolean isLiked) {
        this.avatarUrl = head;
        this.nickname = name;
        this.content = content;
        this.mediaUrl = mediaUrl;
        this.timestamp = time;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.isLiked = isLiked;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(avatarUrl);
        dest.writeString(nickname);
        dest.writeString(content);
        dest.writeString(mediaUrl);
        dest.writeLong(timestamp);
        dest.writeInt(likeCount);
        dest.writeInt(commentCount);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dest.writeBoolean(isLiked);
        }
    }
}