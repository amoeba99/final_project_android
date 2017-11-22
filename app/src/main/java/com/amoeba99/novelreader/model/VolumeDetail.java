package com.amoeba99.novelreader.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Amoeba on 11/19/2017.
 */
@IgnoreExtraProperties
public class VolumeDetail {
    String content;
    String head;
    String title;

    public VolumeDetail(String content, String head, String title){
        this.content = content;
        this.head = head;
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
