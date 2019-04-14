package com.example.black.music;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.black.music.search_cong.bean;
import com.example.black.music.search_cong.myadapter;
import com.example.black.music.search_cong.song;
import com.mathworks.toolbox.javabuilder.MWException;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;



import shuiyin.shuiyin_test;



//import static android.net.sip.SipErrorCode.TIME_OUT;
//mport static android.provider.Telephony.Mms.Part.CHARSET;
import static android.graphics.Bitmap.Config.ARGB_8888;
import static com.example.black.music.application.user_state;
import static com.example.black.music.login.str_username;

public class seach_localmusic extends Activity {
    private Button re;
    private  String temp_p = "";
    private ListView music_list;
    private List<song> list;
    private myadapter adapter;
    private Button search,play;
    public File file;
    String ttt = "";
    //ProgressDialog wait;
    AlertDialog.Builder dialog;
    MediaPlayer mediaPlayer = new MediaPlayer ();
    private  int TIME_OUT = 100000;
    private String CHARSET = "UTF_8";


    private Handler handler = new Handler ( ) {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText (seach_localmusic.this, "" + msg.obj, Toast.LENGTH_SHORT).show ( );
            //wait.dismiss ( );
        }
    };

    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate (saveInstanceState);
        setContentView (R.layout.fabu);

        int hasReadExternalStoragePermission = ContextCompat.checkSelfPermission (getApplication ( ), Manifest.permission.READ_EXTERNAL_STORAGE);
        Log.e ("PERMISION_CODE", hasReadExternalStoragePermission + "***");
        int temp = PackageManager.PERMISSION_GRANTED;
        if (hasReadExternalStoragePermission == PackageManager.PERMISSION_GRANTED) {
            obtainMediaInfo ();
        } else {
            //若没有授权，会弹出一个对话框
            ActivityCompat.requestPermissions (this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (permissions[0].equals (Manifest.permission.READ_EXTERNAL_STORAGE) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //用户同意授权，执行读取文件的代码
                obtainMediaInfo ();
            } else {
                //若用户不同意授权，直接暴力退出应用。。
                Toast.makeText (seach_localmusic.this, "授权失败", Toast.LENGTH_SHORT).show ( );
                finish ( );
            }
        }
    }

    private void obtainMediaInfo() {
        final int[] p = new int[1];
        music_list = (ListView) findViewById (R.id.music_list);
        search = (Button) findViewById (R.id.search);
        play = (Button)findViewById (R.id.play);
        re = (Button)findViewById (R.id.re);
        list = new ArrayList<> ( );

        re.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                seach_localmusic.this.finish ();
            }
        });

        search.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View view) {
                list = bean.getMusicdata (seach_localmusic.this);
                if (list.size ( ) != 0) {
                    adapter = new myadapter (seach_localmusic.this, list);
                    music_list.setOnItemClickListener (new AdapterView.OnItemClickListener ( ) {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            ttt = list.get (position).path;
                            file = new File (list.get (position).path);
                            dialog = new AlertDialog.Builder (seach_localmusic.this);
                            dialog.setTitle ("提示");
                            p[0] = position+1;
                            dialog.setMessage ("是否选择发布第" + p[0] +"歌曲");
                            dialog.setPositiveButton ("确定", new DialogInterface.OnClickListener ( ) {
                                @Override
                                public void onClick(final DialogInterface dialog, int which) {
                                    FileUploadTask fileuploadtask = new FileUploadTask();
                                    fileuploadtask.execute();
                                    dialog.dismiss ();
                                }
                            });
                            dialog.setNegativeButton ("取消", new DialogInterface.OnClickListener ( ) {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss ();
                                }
                            });
                            dialog.show ();
                        }
                    });
                  /*  play.setOnClickListener (new View.OnClickListener ( ) {
                        @Override
                        public void onClick(View v) {
                            play a = new play ();
                            a.music_paly ();
                        }
                    });*/
                    music_list.setAdapter (adapter);
                } else {
                    Toast.makeText (seach_localmusic.this, "未找到音乐文件", Toast.LENGTH_SHORT).show ( );
                }
            }
        });
    }

    //生成含有发布者信息的水印图像
    public void create_img(String publisher_id , String img_path){
        //BufferedImage bi = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
        Bitmap bitmap = Bitmap.createBitmap (64,64, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint ();
        paint.setColor (Color.WHITE);
        paint.setTextSize (12);
        paint.setTextAlign (Paint.Align.CENTER);
        Canvas canvas = new Canvas (bitmap);
        //canvas.drawColor (Color.BLACK);

        canvas.drawText (publisher_id,bitmap.getWidth ()/2,bitmap.getHeight ()/2,paint);

        //保存图片
        try
        {
            FileOutputStream fileOutputStream = new FileOutputStream (img_path);
            bitmap.compress (Bitmap.CompressFormat.JPEG,100,fileOutputStream);
            fileOutputStream.close ();
        }catch (Exception e) {
            e.printStackTrace ();
        }
    }

    //发布信息存入数据库
    public  Boolean fabu_mysql(Context context , String filename){
        String str = "";
        String user_info = str_username;
        Log.v ("","yishoudao");
        HttpPost RE_POST = new HttpPost("http://47.100.202.93/music/fabu_mysql.php");
        ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair ("user_info", user_info));
        params.add(new BasicNameValuePair("filename", filename));
        try {
            RE_POST.setEntity(new UrlEncodedFormEntity (params, HTTP.UTF_8));
            final HttpResponse RE_REPOSE = new DefaultHttpClient ().execute(RE_POST);
            Log.e("status", RE_REPOSE.getStatusLine().toString());
            int temp = RE_REPOSE.getStatusLine().getStatusCode();
            if (RE_REPOSE.getStatusLine().getStatusCode() == 200) {
                Log.v ("","fabu_in_mysql");
                try {
                    String temp_utf = EntityUtils.toString(RE_REPOSE.getEntity(), HTTP.UTF_8);
                    str = temp_utf.substring (1,temp_utf.length ());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (str.equals ("null")){
                    str = "发布信息录入数据库失败";
                    return false;
                }
            }
            else {
                str = "link_error";
                Log.e ("Exception",str);
                return false;
            }
        } catch (Exception x) {
            x.printStackTrace();
        }
        return true;
    }



    class FileUploadTask extends AsyncTask<Object, Integer, Void> {

        private ProgressDialog wait = null;
        private int state;
        Socket client = null;
        File file;
        DataOutputStream data = null;
        DataInputStream data_in = null;
        FileInputStream filein = null;
        Long totalSize;

        //执行异步任务之前执行，并在主线程执行
        @Override
        protected void onPreExecute() {
            wait = new ProgressDialog(seach_localmusic.this);
            wait.setMessage("正在上传...");
            wait.setIndeterminate(false);
            wait.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            wait.setProgress(0);
            wait.show();
        }

        //执行异步线程，紧接前一步执行
        @Override
        protected Void doInBackground(Object... arg0) {
            int response_code = 0;
            try {
                client = new Socket ("47.100.202.93", 10056);
            }catch (IOException e){
                state = 0;
                Log.e ("tag","链接失败");
                e.printStackTrace ();
                return null ;
            }

            try {

                file = new File (ttt);
                totalSize = file.length ();
                if (file.exists ( )) {
                    Log.e ("tag","connecting");
                    filein = new FileInputStream (file);
                    data = new DataOutputStream (client.getOutputStream ( ));


                    data.writeUTF (str_username);
                    data.writeUTF (file.getName ());
                    byte[] bytes = new byte[1024];
                    int length = 0;
                    Long recv_length = 0L;
                    while ((length = filein.read (bytes, 0, bytes.length)) != -1) {
                        data.write (bytes, 0, length);
                        recv_length += length;
                        publishProgress((int) ((recv_length * 100) / totalSize));  // 触发onProcsessUpgrade()
                    }
                    data.flush ( );
                    client.shutdownOutput ();

                    //接收反馈
                    data_in = new DataInputStream(client.getInputStream());
                    int len1;
                    String str=null;
                    byte[] b1 = new byte[1024];
                    while((len1=data_in.read(b1))!=-1){
                       str  = new String(b1, 0, len1);
                    }
                    response_code = Integer.parseInt (str);
                }
                else {
                    Log.e ("tag","file is not exist");
                    client.close ();
                }
//            catch (UnknownHostException e) {
//
//                return null;
//            }
//            catch (SocketTimeoutException e) {
//                return null;
            }catch(IOException e){
                Log.e ("tag","链接失败");
                state = 0;
                e.printStackTrace ( );
                return null;
            }
            try{
                filein.close();
                data.close();
                data_in.close ();
                client.close ();
                state = 1;

                if(response_code == 200){
                    if (!fabu_mysql (seach_localmusic.this,file.getName ().toString ())){
                        Log.e ("tag","存入数据库异常");
                    }
                    else Log.e ("tag","成功存入数据库");
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
            Log.e ("tag","upload success");
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            wait.setProgress(progress[0]);
        }

        //异步线程执行完后，将结果返回给该方法，并在主UI线程中执行
        @Override
        protected void onPostExecute(Void result) {
            try {
                wait.dismiss();
                String note = "";
                if (state == 1) {
                    note = "传输完成";
                }
                else if (state == 0){
                    note = "链接失败！请检查网络";
                }
                Toast.makeText (seach_localmusic.this, note, Toast.LENGTH_SHORT).show ( );
                // TODO Auto-generated method stub
            } catch (Exception e) {
            }
        }

    }
}
