package com.example.black.music.bean;

public class MusicFileInfo {

    String filename;
    String url;
    int length;
    String path;
    String singer;
    String qkl_id;
    String date;

    public MusicFileInfo(){}

    public MusicFileInfo(String name, String url, String path){
        this.filename = name;
        this.url = url;
        this.path = path;
    }

    public String getFilename() {
        return filename;
    }

    public String getUrl() {
        return url;
    }

    public int getLength() {
        return length;
    }

    public String getPath() {
        return path;
    }

    public String getSinger() {
        return singer;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
