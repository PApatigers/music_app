package com.example.black.music;

import android.app.Application;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;


public class application extends Application {

    public static String username = null;
    public static int user_state = 2;            //标志用户登录状态——0：off,2:on
    String password = null;

    Handler handler = new Handler (){
        public void handleMessage(Message msg){
            Toast.makeText (application.this,""+msg.obj,Toast.LENGTH_SHORT).show ();
        }
    };

    public void onCreate(){
        super.onCreate ();
        Log.e ("tag","打开application");
        //new Thread(new mythread ()).start ();


    }

   /* public void read_sp(StringBuilder username , StringBuilder password){
        SharedPreferences sp = getSharedPreferences ("login",MODE_PRIVATE);
        if (sp != null){
            username.append (sp.getString ("username",""));
            password.append (sp.getString ("password",""));
        }
    }

    public void post(String user , String pass){
        HttpPost httpPost = new HttpPost ("http://47.100.202.93/music/java.php");
        ArrayList<BasicNameValuePair> params = new ArrayList<> ();
        params.add (new BasicNameValuePair ("username",user));
        params.add (new BasicNameValuePair ("password",pass));

        try{
            httpPost.setEntity (new UrlEncodedFormEntity (params, HTTP.UTF_8));
            final HttpResponse REPOSE = new DefaultHttpClient ().execute(httpPost);
            Log.e("status", REPOSE.getStatusLine().toString());
            Message msg = new Message ( );
            String temp = "";
            String str = "";
            if (REPOSE.getStatusLine ( ).getStatusCode ( ) == 200) {
                try {
                    str = EntityUtils.toString (REPOSE.getEntity ( ), HTTP.UTF_8);
                } catch (IOException e) {
                    e.printStackTrace ( );
                }
                if (str.equals ("success")) {
                    temp = "自动登录成功";
                }
            } else {
                temp = "自动登录失败，请重新登录";
                //清空sp
                SharedPreferences sp = getSharedPreferences ("login",MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit ();
                editor.clear ();
                editor.commit ();
            }
            msg.obj = temp;
            handler.sendMessage (msg);
        }catch (Exception e){
            e.printStackTrace ();
        }
    }

    public class mythread implements Runnable{
        @Override
        public void run(){
            StringBuilder user = new StringBuilder ();
            StringBuilder pass = new StringBuilder ();
            read_sp (user,pass);
            username = "" + user;
            password = "" + pass;
            post(username,password);
        }
    }*/
}
