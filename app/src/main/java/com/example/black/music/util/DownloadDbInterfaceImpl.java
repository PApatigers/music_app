package com.example.black.music.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.black.music.bean.DownloadThreadInfo;
import com.example.black.music.db.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class DownloadDbInterfaceImpl implements DownloadDbInterface {

    DBHelper dbHelper;
    SQLiteDatabase db;
    public DownloadDbInterfaceImpl(Context context){
        dbHelper = new DBHelper(context);
    }
    @Override
    public void insertThread(DownloadThreadInfo threadInfo) {
        db = dbHelper.getWritableDatabase();
        db.execSQL("insert into thread_info(thread_id , url , start , finish , fileName , fileSize) values(?,?,?,?,?,?)",
                new Object[]{threadInfo.getThreadId(),threadInfo.getUrl(),threadInfo.getStart(),
                        threadInfo.getFinish(),threadInfo.getFileName(),threadInfo.getFileLength()});
        db.close();
    }

    @Override
    public void deleteThread(int threadId , String url) {
        db = dbHelper.getWritableDatabase();
        db.execSQL("delete  from thread_info where url = ? and thread_id = ?" , new Object[]{url,threadId});
        db.close();
    }

    @Override
    public void updateThread(String url , int threadId , int finish) {
        db = dbHelper.getWritableDatabase();
        db.execSQL("update thread_info set finish = ? where url = ? and thread_id = ?",new Object[]{finish , url , threadId});
        db.close();
    }

    @Override
    public List<DownloadThreadInfo> getThread(String url) {
        List<DownloadThreadInfo> threads = new ArrayList<>();
        db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from thread_info where url = ?" , new String[]{url});

        while(cursor.moveToNext()){
            DownloadThreadInfo thread = new DownloadThreadInfo();
            thread.setThreadId(cursor.getInt(cursor.getColumnIndex("thread_id")));
            thread.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            thread.setStart(cursor.getInt(cursor.getColumnIndex("start")));
            thread.setFinish(cursor.getInt(cursor.getColumnIndex("finish")));
            thread.setEnd(cursor.getInt(cursor.getColumnIndex("end")));
            threads.add(thread);
        }
        cursor.close();
        db.close();
        return threads;
    }

    @Override
    public boolean isExit(String url, int thread_id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from thread_info where url = ? and thread_id = ?",
                new String[]{url, thread_id + ""});
        boolean exists = cursor.moveToNext();
        cursor.close();
        db.close();
        return exists;
    }

    @Override
    public List<DownloadThreadInfo> getAllThread() {
        List<DownloadThreadInfo> allThreadInfo = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from thread_info",null);

        while(cursor.moveToNext()){
            DownloadThreadInfo threadInfo = new DownloadThreadInfo();
            threadInfo.setFileName(cursor.getString(cursor.getColumnIndex("fileName")));
            threadInfo.setFileLength(cursor.getLong(cursor.getColumnIndex("fileSize")));
            threadInfo.setFinish(cursor.getInt(cursor.getColumnIndex("finish")));
            threadInfo.setUrl(cursor.getString(cursor.getColumnIndex("url")));
            allThreadInfo.add(threadInfo);
        }
        cursor.close();
        db.close();
        return allThreadInfo;
    }
}
