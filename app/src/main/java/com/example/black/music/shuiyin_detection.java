package com.example.black.music;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

import static com.example.black.music.login.str_username;

public class shuiyin_detection extends Activity {

    TextView yuan_path,ce_path;
    Button yuan_button,ce_button,start_button,return_button;
    ImageView img ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.shuiyin_detection);

        yuan_path = findViewById (R.id.yuan_path);
        ce_path = findViewById (R.id.ce_path);
        yuan_button = findViewById (R.id.yuan_button);
        ce_button = findViewById (R.id.ce_button);
        start_button = findViewById (R.id.start);
        return_button = findViewById (R.id.return_ce);
        img = findViewById (R.id.img_shuiyin);

        //添加点击事件
        yuan_button.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent,1);
                //yuan_path.setText (temp_path);
            }
        });
        ce_button.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("audio/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent,2);
            }
        });
        start_button.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                if (!yuan_path.getText ().toString ().equals ("") && !ce_path.getText ().toString ().equals ("")){
                    upload_two uploadtwo = new upload_two ();
                    uploadtwo.execute();
                }
                else {
                    Toast.makeText (shuiyin_detection.this,"请选择文件",Toast.LENGTH_SHORT).show ();
                }


            }
        });
        return_button.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                shuiyin_detection.this.finish();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {//是否选择，没选择就不会继续
            Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);
            int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            actualimagecursor.moveToFirst();
            String path = actualimagecursor.getString(actual_image_column_index);
            if (requestCode == 1){
                yuan_path.setText (path);
            }
            else if(requestCode == 2){
                ce_path.setText (path);
            }
        }
    }

    class upload_two extends AsyncTask<Object, Integer, Void> {

        String path_1,path_2;
        ProgressDialog wait;
        Socket client;
        File file1,file2;
        Long totalsize ;
        DataOutputStream data = null;
        FileInputStream filein;
        Bitmap bitmap = null;

        @Override
        protected void onPreExecute(){
            path_1 = yuan_path.getText ().toString ();
            path_2 = ce_path.getText ().toString ();
            wait = new ProgressDialog (shuiyin_detection.this);
            wait.setMessage("正在上传...");
            wait.setIndeterminate(false);
            wait.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            wait.setProgress(0);
            wait.show();
            Log.e ("tag",""+path_1+"+"+path_2);
        }


        @Override
        protected Void doInBackground(Object... objects) {

            try {
                client = new Socket ("192.168.137.1",10056);
                file1 = new File(path_1);
                file2 = new File(path_2);
                totalsize = file1.length() + file2.length();
                if (!file1.exists() && !file2.exists()) {
                    Log.e ("tag","file error");
                    return null;
                }

                filein = new FileInputStream(file1);
                data = new DataOutputStream(client.getOutputStream());
                Log.e ("tag","connecting");

                data.writeUTF (str_username);
                data.writeUTF(file1.getName());
                data.writeLong(file1.length());
                data.writeUTF(file2.getName());
                data.writeLong(file2.length());

                Log.e ("tag","uploading 总的："+ totalsize+ "fil1:" + file1.length () + "file2: "+file2.length ());

                byte[] bytes = new byte[1024];
                int length = 0;
                int progress = 0;
                Long recv_length = 0L;
                while((length = filein.read(bytes, 0, bytes.length)) != -1) {
                    data.write(bytes, 0, length);
                    data.flush();
                    recv_length += length;
                    publishProgress ((int) (100L*recv_length/totalsize));
            }
                filein.close();

                Log.e ("tag","文件1传输完成\n"+"uploadint 2");
                filein = new FileInputStream(file2);
                bytes = new byte[1024];
                length  = 0;
                Log.e ("tag","此时的progress"+progress);
                byte[] aaa = new byte[1];
                data.write (aaa);
                while((length = filein.read(bytes, 0, bytes.length)) != -1) {
                    data.write(bytes,0,length);
                    data.flush();
                    recv_length += length;
                    publishProgress ((int) (100*recv_length/totalsize));
                }

                Log.e ("tag","所有文件传输完毕");
                filein.close();
                data.close();

                //开始接受传来的图片
                client = new Socket ("192.168.137.1",10056);
                Log.e("tag","重新建立链接");
                Log.e ("tag","图像来了");
                DataInputStream img_in = new DataInputStream (client.getInputStream ());
                bitmap =  BitmapFactory.decodeStream(img_in);

            }catch (Exception e) {
                Log.e ("tag","link error"+e.getMessage ());
                e.printStackTrace();
            }
//            try{
//
//
//            }catch (IOException e) {
//                e.printStackTrace();
//            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            wait.setProgress(progress[0]);
        }

        protected void onPostExecute(Void result) {
            try {
                wait.dismiss();
                img.setImageBitmap(bitmap);
                Toast.makeText (shuiyin_detection.this, "传输完成", Toast.LENGTH_SHORT).show ( );
            } catch (Exception e) {
            }
        }
    }
}
