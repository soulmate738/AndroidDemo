package com.example.sparkchaindemo.entity;


public class ChatItem {
    private String name;        // 昵称（唯一标识）
    private int avatarResId;    // 头像资源 ID
    private String lastMessage; // 最新消息内容
    private String time;        // 最新消息时间

    public ChatItem(String name, int avatarResId, String lastMessage, String time) {
        this.name = name;
        this.avatarResId = avatarResId;
        this.lastMessage = lastMessage;
        this.time = time;
    }

    // Getters and Setters
    public String getName() { return name; }
    public int getAvatarResId() { return avatarResId; }
    public String getLastMessage() { return lastMessage; }
    public String getTime() { return time; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
    public void setTime(String time) { this.time = time; }
}