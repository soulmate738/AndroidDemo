package com.example.sparkchaindemo.entity;

/**
 * @author anjia
 */

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 评论实体类
 */
public class Comment implements Parcelable {
    private int avatarResId;
    private String nickname;
    private String content;
    private long timestamp;
    private int likeCount;
    private boolean isLiked;
    private String replyContent; // 回复内容，为空表示没有回复

    public Comment(int avatarResId, String nickname, String content, long timestamp, int likeCount, boolean isLiked, String replyContent) {
        this.avatarResId = avatarResId;
        this.nickname = nickname;
        this.content = content;
        this.timestamp = timestamp;
        this.likeCount = likeCount;
        this.isLiked = isLiked;
        this.replyContent = replyContent;
    }

    protected Comment(Parcel in) {
        avatarResId = in.readInt();
        nickname = in.readString();
        content = in.readString();
        timestamp = in.readLong();
        likeCount = in.readInt();
        isLiked = in.readByte() != 0;
        replyContent = in.readString();
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    public int getAvatarResId() {
        return avatarResId;
    }

    public void setAvatarResId(int avatarResId) {
        this.avatarResId = avatarResId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(avatarResId);
        dest.writeString(nickname);
        dest.writeString(content);
        dest.writeLong(timestamp);
        dest.writeInt(likeCount);
        dest.writeByte((byte) (isLiked ? 1 : 0));
        dest.writeString(replyContent);
    }
}