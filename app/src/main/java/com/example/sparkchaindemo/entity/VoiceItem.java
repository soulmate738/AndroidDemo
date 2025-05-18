package com.example.sparkchaindemo.entity;

// VoiceItem.java
public class VoiceItem {
    private String id;
    private String name;
    private String filePath;
    private long duration; // 语音时长（毫秒）
    private long createTime;

    public VoiceItem(String id, String name, String filePath, long duration) {
        this.id = id;
        this.name = name;
        this.filePath = filePath;
        this.duration = duration;
        this.createTime = System.currentTimeMillis();
    }
    public VoiceItem(String name) {
        this.name = name;
        this.createTime = System.currentTimeMillis();
    }

    // Getters and setters
    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getFilePath() { return filePath; }
    public long getDuration() { return duration; }
    public long getCreateTime() { return createTime; }
    // 格式化时长显示
    public String getFormattedDuration() {
        long seconds = duration / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

}
