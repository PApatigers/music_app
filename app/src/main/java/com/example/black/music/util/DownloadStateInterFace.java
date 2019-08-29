package com.example.black.music.util;

public interface DownloadStateInterFace {

    void DownloadSuccess();

    void Downloading(int progress);

    void DownloadFail();

    void DownloadPause();

}
