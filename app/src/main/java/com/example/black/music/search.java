/*
package com.example.black.music;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.black.music.search_cong.myadapter;
import com.example.black.music.search_cong.song;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;


import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

public class search extends Activity{
    private ListView list;
    private EditText search;
    private Button sea_button;
    private  String musicname;
    myadapter adapter;
    List<song> music_list;

    public search() {
    }

    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_music);

        search = (EditText) findViewById (R.id.search);
        sea_button = (Button) findViewById (R.id.search_b);
        list = (ListView) findViewById (R.id.list);
        adapter = new myadapter (search.this, music_list);
        list.setTextFilterEnabled (true);

        musicname = search.getText ().toString ();
        sea_button.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                if(musicname.equals ("")){
                    Toast.makeText (search.this,"请输入歌曲名",Toast.LENGTH_SHORT).show ();
                }
                else {
                    new Thread (new MyThread ()).start ();
                }
            }
        });
    }

   public class MyThread implements Runnable {
        public void run(){
            HttpPost httpPost = new HttpPost ("http://47.100.202.93/music/sear_music.php");
            ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            params.add(new BasicNameValuePair("musicname", musicname));
            try {
                httpPost.setEntity (new UrlEncodedFormEntity (params, HTTP.UTF_8));
                HttpResponse response = new DefaultHttpClient ().execute (httpPost);
                if (response.getStatusLine ().getStatusCode () == 200){
                    String temp = EntityUtils.toString (response.getEntity (),HTTP.UTF_8);
                    Toast.makeText (search.this,temp,Toast.LENGTH_SHORT).show ();
                }
            }catch (Exception e){
                e.printStackTrace ();
            }
        }
    }
}
*/
