package com.example.black.music;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.black.music.fabu_seach.record;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import com.alibaba.fastjson.JSON;
import com.example.black.music.search_cong.his_adapter;
import com.example.black.music.search_cong.music_adapter;

import java.util.ArrayList;

import static com.example.black.music.music.auto_user;

public class history extends Activity {
    private  ListView listView;
    his_adapter adapter  =null;
    private Handler handler = new Handler ( ) {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText (history.this, "" + msg.obj, Toast.LENGTH_SHORT).show ( );
            if (msg.obj.equals ("success")) {
                adapter = new his_adapter (history.this,list);
                listView.setAdapter (adapter);
            }
        }
    };
    public ArrayList<record> list = new ArrayList<> ();
    list al = new list ();
    String aaa = null;

    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.history);

        Intent intent = getIntent ();
        aaa = intent.getStringExtra ("nick");
        listView = findViewById (R.id.history);
        new Thread (new his_thread ()).start ();
    }

    public class his_thread implements Runnable{
        @Override
        public void run(){
            String user = aaa;
            if (!user.equals ("")) {
                HttpPost POST = new HttpPost ("http://47.100.202.93/music/fabu_search.php");
                ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair> ( );
                params.add (new BasicNameValuePair ("username", user));
                try {
                    POST.setEntity (new UrlEncodedFormEntity (params, HTTP.UTF_8));
                    final HttpResponse REPOSE = new DefaultHttpClient ( ).execute (POST);
                    Log.e ("status", REPOSE.getStatusLine ( ).toString ( ));
                    Message msg = new Message ( );
                    String temp = null;
                    if (REPOSE.getStatusLine ( ).getStatusCode ( ) == 200) {
                        String json = "[" + EntityUtils.toString (REPOSE.getEntity ( ), HTTP.UTF_8) + "]";
                        JSONArray jsonArray = JSONArray.parseArray (json);
                        JSONObject object = null;
                        for (int i = 0; i < jsonArray.size ( ); i++) {
                            object = jsonArray.getJSONObject (i);
                            String filename = object.getString ("filename");
                            String date = object.getString ("date");
                            record re = new record ( );
                            re.filename = filename;
                            re.date = date;
                            al.write_list (list, re);
                        }
                        temp = "success";
                        if (al.read_list (list).size ( ) == 0) {
                            temp = "您还未发布任何歌曲";
                        }
                    } else {
                        temp = "链接失败！";
                    }
                    msg.obj = temp;
                    handler.sendMessage (msg);
                } catch (Exception e) {
                    e.printStackTrace ( );
                }
            }
        }
    }

    class list {
        synchronized void write_list(ArrayList<record> list , record re){
            list.add (re);
        }
        synchronized ArrayList read_list(ArrayList<record> list ){
            return list;
        }
    }
}
