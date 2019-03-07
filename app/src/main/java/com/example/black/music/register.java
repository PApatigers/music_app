package com.example.black.music;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.lang.String;

public class register extends Activity {
    Button send, return_2;
    String str_r_username = "";
    String str_r_email = "";
    String str_r_password = "";
    String str_r_repassword = "";
    String str = "";
    ProgressDialog wait;

    @SuppressLint("HandlerLeak")
    Handler handle = new Handler (){
        public void handleMessage(android.os.Message msg){
            Toast.makeText (register.this,""+msg.obj,Toast.LENGTH_SHORT).show ();
            wait.dismiss ();
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        send = (Button) findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                str_r_username = ((EditText) findViewById(R.id.username)).getText().toString();
                str_r_email = ((EditText) findViewById(R.id.email)).getText().toString();
                str_r_password = ((EditText) findViewById(R.id.password)).getText().toString();
                str_r_repassword = ((EditText) findViewById(R.id.re_password)).getText().toString();


                wait = new ProgressDialog(register.this);
                wait.setTitle("提示");

                if (str_r_repassword.compareTo("") != 0 && str_r_username.compareTo("") != 0 && str_r_password.compareTo("") != 0 && str_r_email.compareTo("") != 0) {
                    if (str_r_password.equals(str_r_repassword)) {
                        wait.setMessage("注册中！");
                        wait.show();
                        new Thread(new re_Thread()).start();
                    } else {
                        Toast.makeText(register.this, "两次密码不一致！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    Toast.makeText(register.this, "请确认信息是否完整！", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        return_2 = (Button) findViewById(R.id.return_2);
        return_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register.this.finish();
            }
        });
    }

    public class re_Thread implements Runnable {
        @Override
        public void run() {
            Message msg = new Message ();
            String temp = "";
            HttpPost RE_POST = new HttpPost("http://47.100.202.93/music/register.php");
            ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            params.add(new BasicNameValuePair ("re_username", str_r_username));
            params.add(new BasicNameValuePair("re_email", str_r_email));
            params.add(new BasicNameValuePair("re_password", str_r_password));
            try {
                RE_POST.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                final HttpResponse RE_REPOSE = new DefaultHttpClient().execute(RE_POST);
                Log.e("status", RE_REPOSE.getStatusLine().toString());
                if (RE_REPOSE.getStatusLine().getStatusCode() == 200) {
                    try {
                        str = EntityUtils.toString(RE_REPOSE.getEntity(), HTTP.UTF_8);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (str.substring(1, str.length()).equals("success")) {
                        temp = "注册成功";
                        register.this.finish();
                    }else if(str.equals ("exist_error")) {
                        temp = "用户名已存在";
                    }
                    else {
                        temp = "注册失败";
                    }
                    msg.obj = temp;
                    handle.sendMessage (msg);
                }
            } catch (Exception x) {
                x.printStackTrace();
            }
        }
    }
}