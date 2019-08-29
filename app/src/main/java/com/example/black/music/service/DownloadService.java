package com.example.black.music.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.telephony.mbms.FileInfo;
import android.util.Log;

import com.example.black.music.activity.DownloadActivity;
import com.example.black.music.bean.MusicFileInfo;
import com.example.black.music.util.DownloadDbInterfaceImpl;
import com.example.black.music.util.DownloadStateInterFace;
import com.example.black.music.util.DownloadUtil;

import java.util.LinkedHashMap;

public class DownloadService extends Service {

    private String musicUrl = null;
    private MusicFileInfo music;
    private Messenger mActivityMessenger = null;

    public static final int DOWNLOAD_SUCCESS = 0x1;
    public static final int DOWNLOADING = 0x2;
    public static final int DOWNLOAD_FAIL = 0x3;
    public static final int DOWNLOAD_PAUSE = 0x4;
    public static final int DOWNLOAD_START= 0x5;
    public static final int ACTIVITY_BIND = 0x6;


    //用於接收客戶端發來的消息
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.arg1){
                case DOWNLOAD_START:
                    music = (MusicFileInfo)msg.obj;
                    DownloadUtil.isPause = false;
                    DownloadUtil downloadtask = new DownloadUtil(DownloadService.this,music,mActivityMessenger);
                    downloadtask.download();
                    break;
                case ACTIVITY_BIND:
                    mActivityMessenger = msg.replyTo;
                    break;
                case DOWNLOAD_PAUSE:
                    DownloadUtil.isPause = true;
            }
        }
    };
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("zhouqi_test","serviceCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("zhouqi_test","serviceOnStartCommend");
        musicUrl = intent.getStringExtra("url");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("zhouqi_test","serviceOnBind");
        Messenger messenger = new Messenger(mHandler);
        return messenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        musicUrl = intent.getStringExtra("url");
        Log.e("zhouqi_test","serviceOnUnBind");
        return super.onUnbind(intent);
    }


}


