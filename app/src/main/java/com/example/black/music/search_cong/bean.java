package com.example.black.music.search_cong;

import android.Manifest;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.telephony.mbms.MbmsErrors;

import com.example.black.music.search_cong.song;

import java.util.ArrayList;
import java.util.List;

//@RuntimePermissions
public class bean {

    //@NeedsPermission(Manifest.permission.CAMERA)
    public static  List<song> getMusicdata(Context context){
        List<song> list = new ArrayList<song>();

        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                null, MediaStore.Audio.AudioColumns.IS_MUSIC);
        if (cursor != null){
            while (cursor.moveToNext()){
                song music = new song();
                music.song = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                music.singer = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                music.path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                music.duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                music.size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));

                if (music.size > 1000*800){
                    if (music.song.contains("-")){
                        String[] str = music.song.split("-");
                        music.singer = str[0];
                        music.song = str[1];
                    }
                    list.add(music);
                }
            }
            cursor.close();
        }
        return  list;
    }


    //定义一个方法来格式化获取到的时间
    public static String formatTime(int time){
        if (time / 1000 / 60 < 10 ){
            return time /1000/60 + ":0" + time /1000%60;
        }else{
             return time /100/60 + ":" + time /1000%60;
        }
    }

}
