package com.example.sparkchaindemo.entity;

import android.widget.Adapter;
import android.widget.AdapterView;

/**
 * @author anjia
 * @date 2024/11/1 15:42
 */
public class ChatMessage {
    public static final int TYPE_RECEIVED = 0;
    public static final int TYPE_SENT = 1;
    public static final int TYPE_VOICE = 2;
    public static final int TYPE_TEXT = 3;
    private String content;
    private int type;
    private int messageType;
    private float time;// 仅用于语音消息，表示语音时长
    private String fileDir;

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }


    public ChatMessage(String content, int messageType, int type, float voiceDuration, String fileDir) {
        this.content = content;
        this.messageType = messageType;
        this.type = type;
        this.time = voiceDuration;
        this.fileDir = fileDir;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getFileDir() {
        return fileDir;
    }

    public void setFileDir(String fileDir) {
        this.fileDir = fileDir;
    }
}
