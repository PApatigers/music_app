package com.example.black.music.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.black.music.R;
import com.example.black.music.bean.DownloadThreadInfo;
import com.example.black.music.bean.MusicFileInfo;
import com.example.black.music.music;
import com.example.black.music.service.DownloadService;
import com.example.black.music.util.DownloadDbInterfaceImpl;
import com.example.black.music.util.DownloadStateInterFace;
import com.example.black.music.util.DownloadUtil;
import com.example.black.music.util.OkhttpNetUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.black.music.application.user_state;
import static com.example.black.music.login.str_username;

public class DownloadActivity extends Activity implements View.OnClickListener , DownloadStateInterFace {

    private String url,filename;
    private File dir;
    private Button btstop,btstart;
    private TextView fileText , downloadStateText;
    private ProgressBar progressBar;
    private MusicFileInfo musicFileInfo;
    private DownloadThreadInfo threadInfo;
    private DownloadDbInterfaceImpl downloadDb;

    private View noDownTask,downTask;

    private Messenger mServiceMessenger;

    private static final int DOWNLOAD_INIT = 0x1;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mServiceMessenger = new Messenger(service);

            Messenger mActivityMessenger = new Messenger(handler);
            Message message = new Message();
            message.arg1 = DownloadService.ACTIVITY_BIND;
            message.replyTo = mActivityMessenger;
            try {
                mServiceMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    //接收service消息
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1){
                case DownloadService.DOWNLOAD_FAIL:
                    downloadStateText.setText("下載失敗");
                    break;
                case DownloadService.DOWNLOAD_PAUSE:
                    downloadStateText.setText("下載暫停");
                    break;
                case DownloadService.DOWNLOAD_SUCCESS:
                    downloadStateText.setText("下載完成");
                    break;
                case DownloadService.DOWNLOADING:
                    downloadStateText.setText("正在下載");
                    progressBar.setProgress(msg.what);
                    break;
            }
        }
    };

    Handler mLocalHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.arg1 == DOWNLOAD_INIT){
                Message message = new Message();
                message.obj = musicFileInfo;
                message.arg1 = DownloadService.DOWNLOAD_START;
                try {
                    mServiceMessenger.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("zhouqi_test","onCreate" + this.getLocalClassName());
        downloadDb = new DownloadDbInterfaceImpl(this);
        setContentView(R.layout.item_download);
        initView();
        initService();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v("zhouqi_test","onStop" + this.getLocalClassName());
    }

    void initService(){
        String path = Environment.getExternalStorageDirectory ( ).toString ( ) + "/appmusic";
        musicFileInfo = new MusicFileInfo(filename,url,path);
        Log.v("Download",url+"  "+filename + " " + path);
        dir = new File (path);
        if (!dir.exists ( )) {
            dir.mkdir ( );
        }
        Intent intent = new Intent(this,DownloadService.class);
        intent.putExtra("url",url);
        bindService(intent,serviceConnection,BIND_AUTO_CREATE);
    }

    void initView(){
        progressBar = findViewById(R.id.pbProgress);
        btstart = findViewById(R.id.btStart);
        btstop = findViewById(R.id.btStop);
        fileText = findViewById(R.id.tvFileName);
        downloadStateText = findViewById(R.id.downstate);
        noDownTask = findViewById(R.id.nodowntask);
        downTask = findViewById(R.id.downtask);

        btstart.setOnClickListener(this);
        btstop.setOnClickListener(this);
        progressBar.setMax(100);

        url = getIntent().getStringExtra("url");
        filename = getIntent().getStringExtra("filename");

        if(url == null)
        //從數據庫中取出上次下載任務
            getDownTask();
        else{
            noDownTask.setVisibility(View.GONE);
            downTask.setVisibility(View.VISIBLE);
            fileText.setText(filename);
        }
    }

    void getDownTask(){
        List<DownloadThreadInfo> tmp = downloadDb.getAllThread();
        if(tmp.size() == 0){
            noDownTask.setVisibility(View.VISIBLE);
            downTask.setVisibility(View.GONE);
        }else{

        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
        Log.v("zhouqi_test","onDestory" + this.getLocalClassName());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btStop:
                Message message = new Message();
                message.arg1 = DownloadService.DOWNLOAD_PAUSE;
                try {
                    mServiceMessenger.send(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btStart:
                Log.v("Download","开始");
                new Thread(new InitThread(musicFileInfo)).start();
        }
    }

    @Override
    public void DownloadSuccess() {
        Message message = new Message();
        message.arg1 = DownloadService.DOWNLOAD_SUCCESS;
        handler.sendMessage(message);
    }

    @Override
    public void Downloading(int progress) {
        Message message = new Message();
        message.arg1 = DownloadService.DOWNLOADING;
        message.what = progress;
        handler.sendMessage(message);
        //Log.v("Download","Messenge收到");
    }

    @Override
    public void DownloadFail() {
        Message message = new Message();
        message.arg1 = DownloadService.DOWNLOAD_FAIL;
        handler.sendMessage(message);
    }

    @Override
    public void DownloadPause() {
        Message message = new Message();
        message.arg1 = DownloadService.DOWNLOAD_PAUSE;
        handler.sendMessage(message);
    }

    //先对文件进行验证，获取文件大小
    class InitThread extends Thread{

        private MusicFileInfo musicFileInfo;

        InitThread(MusicFileInfo musicFileInfo){
            this.musicFileInfo = musicFileInfo;
        }

        @Override
        public void run() {
            super.run();
            int length = 0;
            OkHttpClient okHttpClient = OkhttpNetUtil.getIns().getOkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            Call call = okHttpClient.newCall(request);

            try{
                Response response = call.execute();
                if(response.isSuccessful()){
                    length = (int)response.body().contentLength();
                    if(length <= 0)
                        return;
                }
            }catch (IOException e){
                e.printStackTrace();
            }finally {
                musicFileInfo.setLength(length);
                Message message = new Message();
                message.arg1 = DOWNLOAD_INIT;
                mLocalHandler.sendMessage(message);
            }
        }
    }

    //歌曲下载线程
    public class upload_thread implements Runnable {
        @Override
        public void run() {
            upload ( );
            //将下载记录存入数据库
        }
    }
    //下载文件
    public Boolean upload() {
        String str_url = url;
        String path = Environment.getExternalStorageDirectory ( ).toString ( ) + "/appmusic";
        File dir = new File (path);
        if (!dir.exists ( )) {
            dir.mkdir ( );
            Log.e ("tag", "create appmusic dir");
            Log.i ("TAG", "" + path);
        }
        try {
            URL url = new URL (str_url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection ( );
            connection.setReadTimeout (5000);
            connection.setConnectTimeout (5000);
            connection.setRequestProperty ("Charset", "UTF-8");
            connection.setRequestMethod ("GET");
            connection.connect ( );
            int a = connection.getResponseCode ( );
            InputStream app = connection.getErrorStream ( );
            if (connection.getResponseCode ( ) == 200) {
                InputStream inputStream = connection.getInputStream ( );
                FileOutputStream fileOutputStream = null;
                if (inputStream != null) {
                    fileOutputStream = new FileOutputStream (path + File.separator + filename);
                    byte[] buf = new byte[1024];
                    int ch;

                    //yuan
                    while ((ch = inputStream.read (buf)) != -1) {
                        fileOutputStream.write (buf, 0, ch);//将获取到的流写入文件中
                    }

//                    //加密课设
//                    StringBuffer str_k = new StringBuffer ();
//                    str_k.append (str_username);
//                    str_k.append (getIMSI ());
//                    Log.e ("tag","str_k:"+str_k);
//                    String res = str_k.toString ();
//                    int k = res.hashCode ( );
//                    Log.e ("tag", "" + k);
//                    int time = 0;
//
//                    //找到k在100以内最大因子
//                    int temp_yinzi = 0;
//                    int res_yinzi = 0;
//                    for(int i=1;i<100;i++) {
//                        if(k%i == 0)
//                            temp_yinzi = i;
//                        if(temp_yinzi > res_yinzi)
//                            res_yinzi = temp_yinzi;
//                    }
//
//                    while ((ch = inputStream.read ( )) != -1) {
//                        //Log.e ("tag","进行解密");
//                        if (time%res_yinzi == 0) fileOutputStream.write (ch ^ k);
//                        else fileOutputStream.write (ch);
//                        time++;
//                    }
//                    Log.e ("tag", time + "");
                }
                if (fileOutputStream != null) {
                    fileOutputStream.flush ( );
                    fileOutputStream.close ( );
                }
            }
            connection.disconnect ( );
        } catch (MalformedURLException e) {
            Log.e ("tag", "error");
            e.printStackTrace ( );
        } catch (IOException e) {
            e.printStackTrace ( );
        }
        Log.e ("tag", "finish");
        return true;
    }
}
