package com.example.black.music.util;

import com.example.black.music.bean.DownloadThreadInfo;

import java.util.List;

public interface DownloadDbInterface {

    void insertThread(DownloadThreadInfo threadInfo);

    void deleteThread(int threadId , String url);

    void updateThread(String url , int threadId,int finish);

    boolean isExit(String url , int threadId);

    List<DownloadThreadInfo> getThread(String url);

    List<DownloadThreadInfo> getAllThread();
}
