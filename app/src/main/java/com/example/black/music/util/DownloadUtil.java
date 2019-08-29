package com.example.black.music.util;

import android.content.Context;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.example.black.music.bean.DownloadThreadInfo;
import com.example.black.music.bean.MusicFileInfo;
import com.example.black.music.service.DownloadService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadUtil {

    private String url,filename;
    private long fileSize;
    private File file,dir;
    private DownloadThreadInfo threadInfo;
    private RandomAccessFile raf;
    private DownloadDbInterfaceImpl downloadDb;
    private int startPoint,mFinish;
    private Messenger messenger;
    public static boolean isPause = false;
    private Timer timer = new Timer();

    public DownloadUtil(Context context, MusicFileInfo musicFileInfo, Messenger messenger){
        this.url = musicFileInfo.getUrl();
        this.filename = musicFileInfo.getFilename();
        this.dir = new File(musicFileInfo.getPath());
        this.fileSize = musicFileInfo.getLength();
        this.messenger = messenger;
        downloadDb = new DownloadDbInterfaceImpl(context);
    }

    public void download(){

        Log.v("Download","进入download");
        //从数据库内读取上次下载线程信息
        List<DownloadThreadInfo> threads = downloadDb.getThread(url);
        if(threads.size() == 0){
            threadInfo = new DownloadThreadInfo(0,this.url);
            threadInfo.setFileName(filename);
            threadInfo.setFileLength(fileSize);
        }else{
            threadInfo = threads.get(0);
        }
        startPoint = threadInfo.getStart()+threadInfo.getFinish();
        Log.v("Download","起始下载位置："+startPoint + " " + threads.size());
        new Thread(new DownloadThread(threadInfo)).start();

        //定时将进度反馈给activity
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
            }
        },0,500);
    }

    class DownloadThread extends Thread{
        DownloadThreadInfo threadInfo = null;

        public DownloadThread(DownloadThreadInfo info){
            this.threadInfo = info;
        }

        @Override
        public void run() {
            Log.v("Download","开始下载");

            //将新的下载记录插入数据库
            if(!downloadDb.isExit(threadInfo.getUrl(),threadInfo.getThreadId()))
                downloadDb.insertThread(threadInfo);

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .header("RANGE", "bytes=" + startPoint + "-")//断点续传
                    .url(url)
                    .build();
            try{
                Response response = client.newCall(request).execute();
            }catch (IOException e){
                e.printStackTrace();
            }

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Message message = new Message();
                    message.arg1 = DownloadService.DOWNLOAD_FAIL;
                    try {
                        messenger.send(message);
                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                    }
                }

                @Override

                public void onResponse(Call call, Response response) throws IOException {
                    Log.v("Download","进入");
                    InputStream is = null;
                    try{
                        byte[] buff = new byte[1024];
                        int readLen = 0;
                        is = response.body().byteStream();
                        if(fileSize == 0L){
                            return;
                        }
                        file = new File(dir,filename);
                        raf = new RandomAccessFile(file,"rwd");
                        raf.seek(startPoint);
                        while((readLen = is.read(buff)) != -1){
                            raf.write(buff,0,readLen);
                            mFinish += readLen;
                            int progress = new Double(((startPoint+mFinish)*1.0 / fileSize*1.0)*100).intValue();
                            Log.v("Download","下载进度："+progress + isPause + " " + fileSize +" " +"已下载长度" + mFinish + " " + startPoint);
                            Message downingMessage = new Message();
                            downingMessage.arg1 = DownloadService.DOWNLOADING;
                            downingMessage.what = progress;
                            messenger.send(downingMessage);
                            if(isPause){
                                downloadDb.updateThread(threadInfo.getUrl(),threadInfo.getThreadId(),threadInfo.getFinish()+mFinish);
                                Message pauseMessage = new Message();
                                pauseMessage.arg1 = DownloadService.DOWNLOAD_PAUSE;
                                messenger.send(pauseMessage);
                                timer.cancel();
                                return;
                            }
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        if (raf != null && is != null){
                            raf.close();
                            is.close();
                        }
                    }
                    Message successMessage = new Message();
                    successMessage.arg1 = DownloadService.DOWNLOAD_SUCCESS;
                    try {
                        messenger.send(successMessage);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    downloadDb.deleteThread(threadInfo.getThreadId(),threadInfo.getUrl());
                    timer.cancel();
                }
            });
        }
    }

}
