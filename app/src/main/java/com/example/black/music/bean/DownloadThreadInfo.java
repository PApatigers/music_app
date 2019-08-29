package com.example.black.music.bean;

import com.example.black.music.util.DownloadStateInterFace;

public class DownloadThreadInfo {

    int threadId;
    String url,fileName,path;
    int start,finish,end;
    long fileLength;

    public DownloadThreadInfo(){}

    public DownloadThreadInfo(int threadId , String url){
        this.threadId = threadId;
        this.url = url;
    }

    public void setThreadId(int Id){
        this.threadId = Id;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setFinish(int finish) {
        this.finish = finish;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getThreadId() {
        return threadId;
    }

    public String getUrl() {
        return url;
    }

    public int getFinish() {
        return finish;
    }

    public int getEnd() {
        return end;
    }

    public int getStart() {
        return start;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileLength() {
        return fileLength;
    }

    public String getPath() {
        return path;
    }
}

