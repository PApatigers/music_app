package com.example.black.music.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.IBinder;

import java.io.IOException;

public class music_play extends Service {

    int status = 0x11;
    MediaPlayer mplayer;
    int current = 0;
    AssetManager am;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand (intent, flags, startId);
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        return super.bindService (service, conn, flags);
    }

    @Override
    public IBinder onBind(Intent intend){
        return null;
    }


    //自定义广播
    public class Myreceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            int control = intent.getIntExtra ("control",-1);
            switch (control){

                //
                case 1:
                    if (status == 0x11){
                       // prepareandplay(music[current]);
                        status = 0x12;
                    }
                    else if(status == 0x12){

                    }
                    else if(status == 0x13){

                    }
                    break;


                case 2:
                    if (status == 0x12 || status == 0x13){
                        mplayer.stop();
                    }
                    break;
            }

            Intent sendintent = new Intent ();
            sendintent.putExtra ("update",status);
            sendintent.putExtra ("current",current);
            sendBroadcast (sendintent);
        }
    }

    private void prepareandplay(String music){
        try{
            AssetFileDescriptor afd = am.openFd (music);
            mplayer.reset ();
            mplayer.setDataSource (afd.getFileDescriptor (),afd.getStartOffset (),afd.getLength ());

            mplayer.prepare ();
            mplayer.start ();
        }catch (IOException e){
            e.printStackTrace ();
        }
    }
}
