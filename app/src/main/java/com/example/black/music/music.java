package com.example.black.music;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.browse.MediaBrowser;
import android.net.sip.SipSession;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.black.music.fabu_seach.record;
import com.example.black.music.search_cong.music_adapter;
import com.example.black.music.search_cong.myadapter;
import com.example.black.music.search_cong.song;

import org.apache.http.HttpConnection;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.example.black.music.application.user_state;


public class music extends AppCompatActivity {
    private ListView listview;
    private EditText search;
    private Button sea_button;
    private Button button1_2;
    private String musicname;
    public ArrayList<record> music_list = new ArrayList<record> ( );
    public ArrayList<record> temp_list = new ArrayList<record> ( );
    public list lock = new list ( );
    public music_adapter adapter = null;
    private int temp_posi ;
    private ProgressDialog progressDialog = null;
    public static String auto_user = null;
    public static String auto_pass = null;

    Handler handler = new Handler ( ) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.obj.equals ("success")) {
                if (music_list.size () != 0){
                    adapter = new music_adapter (music.this, music_list);
                    listview.setAdapter (adapter);
                }else {
                    Toast.makeText (music.this,"未找到该歌曲",Toast.LENGTH_SHORT).show ();
                }

            } else {
                Toast.makeText (music.this, "" + msg.obj, Toast.LENGTH_SHORT).show ( );
            }
        }
    };

    Handler upload_han = new Handler (){
      @Override
      public void handleMessage(Message msg){
          progressDialog.dismiss ();
          Toast.makeText (music.this,""+msg.obj,Toast.LENGTH_SHORT).show ();
          //Toast.makeText (music.this,"下载成功",Toast.LENGTH_SHORT).show ();
      }
    };

    Handler auto_login_handler = new Handler (){
        @Override
        public void handleMessage(Message msg){
            if (msg.obj.equals ("success")){
                Toast.makeText (music.this,"自动登录成功",Toast.LENGTH_SHORT).show ();
            }
            else Toast.makeText (music.this,""+msg.obj,Toast.LENGTH_SHORT).show ();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_music);

        search = (EditText) findViewById (R.id.search);
        sea_button = (Button) findViewById (R.id.search_b);
        button1_2 = (Button) findViewById (R.id.button2);
        listview = (ListView) findViewById (R.id.list);
        listview.setTextFilterEnabled (true);

        sea_button.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                musicname = search.getText ( ).toString ( );
                if (musicname.equals ("")) {
                    Toast.makeText (music.this, "请输入歌曲名", Toast.LENGTH_SHORT).show ( );
                } else {
                    new Thread (new myThread ( )).start ( );
                    listview.setOnItemClickListener (new AdapterView.OnItemClickListener ( ) {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            temp_posi = position;
                            AlertDialog.Builder dialog = new AlertDialog.Builder (music.this);
                            dialog.setTitle ("提示");
                            dialog.setMessage ("是否下载当前歌曲");
                            dialog.setPositiveButton ("确定", new DialogInterface.OnClickListener ( ) {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if (true){
                                        progressDialog = new ProgressDialog (music.this);
                                        progressDialog.setTitle ("提示");
                                        progressDialog.setMessage ("正在下载");

                                        //动态申请权限
                                        int hasReadExternalStoragePermission = ContextCompat.checkSelfPermission (getApplication ( ), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                                        Log.e ("PERMISION_CODE", hasReadExternalStoragePermission + "***");
                                        int temp = PackageManager.PERMISSION_GRANTED;
                                        if (hasReadExternalStoragePermission == PackageManager.PERMISSION_GRANTED) {
                                            progressDialog.show ( );
                                            new Thread (new upload_thread ( )).start ( );
                                        } else {
                                            //若没有授权，会弹出一个对话框
                                            ActivityCompat.requestPermissions (music.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                        }
                                        dialog.dismiss ( );
                                    }
                                    else {
                                        Toast.makeText (music.this,"您还未登录",Toast.LENGTH_SHORT).show ();
                                        Intent intent = new Intent (music.this,login.class);
                                        startActivity (intent);
                                    }

                                }
                            });
                            dialog.setNegativeButton ("取消", new DialogInterface.OnClickListener ( ) {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss ( );
                                }
                            });
                            dialog.show ( );
                        }
                    });
                }
            }
        });

        button1_2.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View view) {
                Intent intent1_2 = new Intent (music.this, userpage.class);
                startActivity (intent1_2);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (permissions[0].equals (Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //用户同意授权，执行读取文件的代码
                int a = temp_posi;
                progressDialog.show ( );
                new Thread (new upload_thread ( )).start ( );
            } else {
                //若用户不同意授权，直接暴力退出应用。。
                Toast.makeText (music.this, "授权失败", Toast.LENGTH_SHORT).show ( );
                finish ( );
            }
        }
    }

    //歌曲搜索线程
    public class myThread implements Runnable {
        @Override
        public void run() {
            String temp = "";
            Message msg = new Message ( );
            music_list = new ArrayList<> ();
            HttpPost httpPost = new HttpPost ("http://47.100.202.93/music/search_music.php");
            ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair> ( );
            params.add (new BasicNameValuePair ("musicname", musicname));
            try {
                httpPost.setEntity (new UrlEncodedFormEntity (params, HTTP.UTF_8));
                HttpResponse response = new DefaultHttpClient ( ).execute (httpPost);
                int re = response.getStatusLine ( ).getStatusCode ( );
                if (response.getStatusLine ( ).getStatusCode ( ) == 200) {
                    String str = "[" + EntityUtils.toString (response.getEntity ( ), HTTP.UTF_8) + "]";
                    JSONArray json = JSONArray.parseArray (str);
                    JSONObject object = null;
                    for (int i = 0; i < json.size ( ); i++) {
                        object = json.getJSONObject (i);
                        record sea_muc = new record ( );
                        sea_muc.filename = object.getString ("filename");
                        sea_muc.username = object.getString ("username");
                        lock.write_list (music_list, sea_muc);
                    }
                    temp = "success";
                    msg.obj = temp;
                    handler.sendMessage (msg);
                } else {
                    temp = "链接失败";
                    msg.obj = temp;
                    handler.sendMessage (msg);
                }
            } catch (Exception e) {
                e.printStackTrace ( );
            }
        }
    }

    //下载文件
    public Boolean  upload() {
        String filename = music_list.get (temp_posi).filename.replaceAll (" ", "%20");
        String str_url = "http://47.100.202.93/upload/" + filename;
        String path = Environment.getExternalStorageDirectory ( ).toString ( )+"/appmusic";
        File dir = new File(path);
        if (dir.exists ()) {
            dir.mkdir ( );

        }
        Log.e ("tag","create appmusic dir");
        Log.i ("TAG", "" + path);
        try {
            URL url = new URL (str_url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection ( );
            connection.setReadTimeout (5000);
            connection.setConnectTimeout (5000);
            connection.setRequestProperty ("Charset", "UTF-8");
            connection.setRequestMethod ("GET");
            int a = connection.getResponseCode ( );
            InputStream app = connection.getErrorStream ( );
            if (connection.getResponseCode ( ) == 200) {
                InputStream inputStream = connection.getInputStream ( );
                FileOutputStream fileOutputStream = null;
                if (inputStream != null) {
                    fileOutputStream = new FileOutputStream (path+File.separator+music_list.get (temp_posi).filename);
                    byte[] buf = new byte[1024];
                    int ch;
                    while ((ch = inputStream.read (buf)) != -1) {
                        fileOutputStream.write (buf, 0, ch);//将获取到的流写入文件中
                    }
                }
                if (fileOutputStream != null) {
                    fileOutputStream.flush ( );
                    fileOutputStream.close ( );
                }
            }
        } catch (MalformedURLException e) {
            Log.e ("tag","error");
            e.printStackTrace ( );
        } catch (IOException e) {
            e.printStackTrace ( );
        }
        Log.e ("tag","finish");
        return true;
    }

    //歌曲下载线程
    public class upload_thread implements Runnable {
        @Override
        public void run() {
            upload ();
            int a = user_state;
            if (upload()){
                Log.e ("tag1","下载成功success");
                String temp = "下载成功";
                Message msg = new Message ();
                msg.obj = temp;
                upload_han.sendMessage (msg);

            }

            //将下载记录存入数据库
        }
    }

    class list {
        synchronized void write_list(ArrayList<record> list, record re) {
            list.add (re);
        }
        synchronized ArrayList read_list(ArrayList<record> list) {
            return list;
        }
    }

    public void download_mysql(Context context,String filename){
        String username = auto_user;
        HttpPost httpPost = new HttpPost ("http://47.100.202.93/music/down_mysql");
        ArrayList<BasicNameValuePair> params = new ArrayList<> ();
        params.add (new BasicNameValuePair ("username",username));
        params.add(new BasicNameValuePair ("filename",filename));

       /* try{
            httpPost.setEntity ();
        }*/

    }


}


