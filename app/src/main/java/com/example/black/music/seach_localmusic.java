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
import java.net.URL;
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
        list = new ArrayList<> ( );

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


                                    /*wait = new ProgressDialog (seach_localmusic.this);
                                    wait.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                    wait.setTitle ("提示");
                                    wait.setMessage ("正在上传···");
                                    wait.setMax (100);
                                    wait.show ( );
                                    new Thread (new Mythread ( )).start ( );*/
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


/*    class mythred implements Runnable{

        @Override
        public void run(){
            //生成水印图片
            File file = new File (ttt);
            String img_path = Environment.getExternalStorageDirectory ( ).toString ( )+"/appmusic/1.jpg";
            String out_path = Environment.getExternalStorageDirectory ( ).toString ( )+"/appmusic/shuiyin/" + file.getName ();
            create_img (str_username,img_path);
            try{
                shuiyin_test sy = new shuiyin_test ();
                sy.embed (img_path,ttt,out_path);
            }catch (MWException e){
                e.printStackTrace ();
            }
        }
    }*/

    class FileUploadTask extends AsyncTask<Object, Integer, Void> {

        private ProgressDialog wait = null;
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;
        DataInputStream inputStream = null;
        //the file path to upload
        String pathToOurFile = ttt;
        //the server address to process uploaded file
        String urlServer = "http://47.100.202.93/music/file.php";
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "ad143asd";

        File uploadFile = new File(pathToOurFile);
        long totalSize = uploadFile.length(); // Get size of file, bytes

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
            long length = 0;
            int progress;
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 256 * 1024;// 256KB
            Long a = totalSize;
            Log.e ("tag",a.toString ());
            try {
                FileInputStream fileInputStream = new FileInputStream(uploadFile);
                URL url = new URL(urlServer);
                connection = (HttpURLConnection) url.openConnection();

                // 设置每次发送块大小
                connection.setChunkedStreamingMode(256 * 1024);// 256KB

                //允许输入输出流
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);

                // 使用POST
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Charset", "UTF-8");
                connection.setRequestProperty("Content-Type",
                        "multipart/form-data;boundary=" + boundary);

                outputStream = new DataOutputStream(
                        connection.getOutputStream());
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream
                        .writeBytes("Content-Disposition: form-data; name=\"file\";filename=\""
                                + uploadFile.getName () + "\"" + lineEnd);
                /*outputStream
                        .writeBytes("Content-Type: application/octet-stream; charset="
                        + "UTF_8" + lineEnd);*/
                outputStream.writeBytes(lineEnd);

                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // Read file
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {
                    outputStream.write(buffer, 0, bufferSize);
                    length += bufferSize;
                    progress = (int) ((length * 100) / totalSize);
                    publishProgress(progress);

                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(twoHyphens + boundary + twoHyphens
                        + lineEnd);
                publishProgress(100);
                outputStream.flush();
                outputStream.close();

                // 接收服务端返回消息
                int serverResponseCode = connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();
                Log.e ("tag",serverResponseCode+" && " + serverResponseMessage);

                InputStream inputStream=connection.getInputStream();
                byte[] data=new byte[1024];
                StringBuffer sb1=new StringBuffer();
                int leng=0;
                while ((length=inputStream.read(data))!=-1){
                    String s=new String(data, Charset.forName("utf-8"));
                    sb1.append(s);
                }
                Log.e ("tag",sb1.toString ());
                inputStream.close();


                fileInputStream.close();
                connection.disconnect();

            } catch (Exception ex) {
                // Exception handling
                // showDialog("" + ex);
                // Toast toast = Toast.makeText(UploadtestActivity.this, "" +
                // ex,
                // Toast.LENGTH_LONG);

            }
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
                Toast.makeText (seach_localmusic.this, "传输完成", Toast.LENGTH_SHORT).show ( );
                // TODO Auto-generated method stub
            } catch (Exception e) {
            }
        }

    }
}
