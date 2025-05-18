package com.example.sparkchaindemo.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author anjia
 */
// VoicePackage.java
public class VoicePackage {
    private String id;
    private String name;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    private int count;
    private long createTime;
    private List<VoiceItem> voiceItems;

    public VoicePackage(String id, String name,int count) {
        this.id = id;
        this.name = name;
        this.createTime = System.currentTimeMillis();
        this.count =count;
        this.voiceItems = new ArrayList<>();
    }


    // Getters and setters
    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public long getCreateTime() { return createTime; }
    public List<VoiceItem> getVoiceItems() { return voiceItems; }
    public void addVoiceItem(VoiceItem item) { voiceItems.add(item); }
    public void removeVoiceItem(VoiceItem item) { voiceItems.remove(item); }
}

