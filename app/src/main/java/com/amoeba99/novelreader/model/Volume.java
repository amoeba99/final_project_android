package com.amoeba99.novelreader.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

/**
 * Created by Amoeba on 11/20/2017.
 */
public class Volume implements Parcelable{

    private String title;
    private String content;

    public Volume(String title, String content) {
        this.title = title;
        this.content = content;
    }

    protected Volume(Parcel in) {
        title = in.readString();
        content = in.readString();
    }

    public static final Creator<Volume> CREATOR = new Creator<Volume>() {
        @Override
        public Volume createFromParcel(Parcel in) {
            return new Volume(in);
        }

        @Override
        public Volume[] newArray(int size) {
            return new Volume[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(content);
    }
}
