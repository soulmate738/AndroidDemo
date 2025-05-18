package com.example.sparkchaindemo.entity;

import java.util.List;

/**
 * @author anjia
 */
public class ClassItem {

        private int status;
        private int id;
        private String title;
        private String content;
        private int resource;

    public ClassItem(String title,int id, int resource, int status,String content) {
        this.content = content;
        this.id = id;
        this.resource = resource;
        this.status = status;
        this.title = title;
    }
    public ClassItem() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
