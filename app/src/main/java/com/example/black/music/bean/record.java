package com.example.black.music.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.TextView;

public class record implements Parcelable {
    public String username;
    public String qkl_id;
    public String filename;
    public String date;

    public record(){
        username="";
        qkl_id="";
        filename="";
        date="";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public static final Parcelable.Creator<record> creator = new Creator<record>() {
        @Override
        public record createFromParcel(Parcel source) {
            return null;
        }

        @Override
        public record[] newArray(int size) {
            return new record[0];
        }
    };
}

