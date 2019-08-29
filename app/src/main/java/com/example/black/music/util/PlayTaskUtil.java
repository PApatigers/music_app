package com.example.black.music.util;

import android.media.MediaPlayer;
import android.os.Looper;

public class PlayTaskUtil implements PlayTask {

    private MediaPlayer mediaPlayer;
    private String fileSource;

    private PlayTaskUtil(){
        mediaPlayer = new MediaPlayer();
    };

    public PlayTaskUtil getInstance(){
        return InstanceHolder.instance;
    }

    private static class InstanceHolder{
        private static final PlayTaskUtil instance = new PlayTaskUtil();
    }


    public String getFileSource() {
        Looper.prepare();
        return fileSource;
    }


    public void setFileSource(String fileSource) {
        this.fileSource = fileSource;
    }

    @Override
    public void startPlay() {

    }

    @Override
    public void stopPlay() {

    }
}
