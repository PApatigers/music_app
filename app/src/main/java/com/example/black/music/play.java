package com.example.black.music;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class play {
    MediaPlayer mediaPlayer ;

    public void music_paly(String path){
        File file = new File(path);
        if (file.exists () && file.length ()>0){
            try {
                mediaPlayer= new MediaPlayer ();
                mediaPlayer.setDataSource (path);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                //异步装载媒体资源
                mediaPlayer.prepareAsync ();
                mediaPlayer.setOnPreparedListener (new MediaPlayer.OnPreparedListener ( ) {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mediaPlayer.start ();
                        //Toast.makeText ()
                    }
                });
            }catch (IOException e) {
                e.printStackTrace ( );
            }
        }
    }
}
