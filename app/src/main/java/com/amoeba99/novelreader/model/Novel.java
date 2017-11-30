package com.amoeba99.novelreader.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

/**
 * Created by Amoeba on 11/20/2017.
 */
@IgnoreExtraProperties
public class Novel {
    private String key;
    private String name;
    private String img;
    private String count;
    private String totalscore;

    public Novel(String key, String name, String img, String count, String totalscore) {
        this.key = key;
        this.name = name;
        this.img = img;
        this.count = count;
        this.totalscore = totalscore;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getTotalscore() {
        return totalscore;
    }

    public void setTotalscore(String totalscore) {
        this.totalscore = totalscore;
    }
}
