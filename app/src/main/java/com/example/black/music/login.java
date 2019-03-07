package com.example.black.music;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import static com.example.black.music.application.user_state;

public class login extends Activity {
    Button register,login,re;
    EditText user , password;
    CheckBox remeber;
    ProgressDialog wait;                    //创建等待框

    private String str = "";
    public  static String str_username = "";
    public String str_passsword = "";

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler (){
        public void handleMessage(android.os.Message msg){
            Toast.makeText (login.this,""+msg.obj,Toast.LENGTH_SHORT).show ();
            wait.dismiss ();
        }
    };

    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my);

        //获取控件
        login = (Button) findViewById(R.id.login);
        remeber = (CheckBox)findViewById (R.id.remeber);
        user = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                str_username = user.getText().toString();
                str_passsword = password.getText().toString();

                //检查是否记住密码
                if (remeber.isChecked ()){
                    //将登陆信息存入sp，用于实现自动登录
                    save_sp (str_username,str_passsword);
                }

                wait = new ProgressDialog(login.this);
                wait.setTitle("提示");
                /*if(!checknet()){
                    Toast toast = Toast.makeText(login.this,"网络未连接", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }*/
                if (str_username.compareTo("")!=0 && str_passsword.compareTo("")!=0 ){
                    wait.setMessage("登录中···");
                    //wait.setCancelable(false);
                    wait.show();
                    new Thread (new MyThread ()).start ();
                }
                else if (str_username.compareTo("")==0 || str_passsword.compareTo("")==0 ){
                    Toast.makeText(login.this,"用户名或密码不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        register = (Button)findViewById(R.id.register);
        register.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login.this,register.class);
                startActivity(intent);
            }
        });

        re = (Button) findViewById(R.id.re);
        re.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login.this,userpage.class);
                startActivity(intent);
            }
        });
    }


    public class MyThread implements Runnable {

        @Override
        public void run() {
            HttpPost POST = new HttpPost("http://47.100.202.93/music/java.php");
            ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            params.add(new BasicNameValuePair("username", str_username));
            params.add(new BasicNameValuePair("password", str_passsword));
            try {
                POST.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
                final HttpResponse REPOSE = new DefaultHttpClient().execute(POST);
                Log.e("status", REPOSE.getStatusLine().toString());
                Message msg = new Message ( );
                String temp = "";
                if (REPOSE.getStatusLine ( ).getStatusCode ( ) == 200) {
                    try {
                        str = EntityUtils.toString (REPOSE.getEntity ( ), HTTP.UTF_8);
                        //JSONObject json = new JSONObject (str);

                    } catch (IOException e) {
                        e.printStackTrace ( );
                    }
                    if (str.compareTo ("error_username") == 0) {
                        temp = "用户名不存在";
                    } else if (str.equals ("error_password")) {
                        temp = "密码错误";
                    } else if (str.equals ("success")) {
                        Intent intent1 = new Intent ( );
                        intent1.putExtra ("nickname", str_username);
                        setResult (4, intent1);
                        login.this.finish ( );
                        temp = "登录成功";
                        user_state = 2;
                    } else {
                        temp = str;
                    }
                } else {
                    temp = "链接失败！";
                }
                msg.obj = temp;
                handler.sendMessage (msg);
            }catch (Exception e){
                e.printStackTrace ();
            }
        }
    }

    public void save_sp(String username , String password){
        SharedPreferences sp = getSharedPreferences ("login",MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit ();
        ed.putString ("username",username);
        ed.putString ("password",password);
        ed.commit ();
    }

/*    public static class user_info extends login{

        void intc(){}
        public static String  user(){
            String user_name = null;
            user_name = str_username;
            return user_name;
        };

    }*/
}

