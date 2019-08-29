package com.example.black.music.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.black.music.R;
import com.example.black.music.adapter.SearchListAdapter;
import com.example.black.music.bean.MusicFileInfo;
import com.example.black.music.fragment.MainFragment;
import com.example.black.music.login;
import com.example.black.music.music;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;
import static com.example.black.music.login.str_username;

public class SearchActivity extends Activity implements View.OnClickListener , SearchListAdapter.OnItemClickListener{

    private ImageView backIcon,searchIcon;
    private EditText searchText;
    private RecyclerView muistListView;
    private View loadingView;

    private List<MusicFileInfo> musicList;
    private String searchMusicName;
    private SearchListAdapter adapter;

    private static final int SEARCH_SUCCESS = 0x111;
    private static final int SEARCH_FAIL = 0x222;

    Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.v("fragment",msg.arg1+"");
            switch (msg.arg1){
                case SEARCH_SUCCESS:
                    if (musicList.size ( ) != 0) {
                        adapter = new SearchListAdapter(SearchActivity.this, musicList);
                        adapter.setItemClickListner(SearchActivity.this);
                        muistListView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
                        muistListView.setAdapter (adapter);
                    } else {
                        Toast.makeText (SearchActivity.this, "未找到该歌曲", Toast.LENGTH_SHORT).show ( );
                    }
                    break;
                case SEARCH_FAIL:
                    Toast.makeText(SearchActivity.this,"加載失敗", LENGTH_SHORT).show();
                    break;
            }
            loadingView.setVisibility(View.GONE);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initView();
        initAction();
    }

    void initView(){
        backIcon = findViewById(R.id.search_back_icon);
        searchIcon = findViewById(R.id.search_searchIcon);
        searchText = findViewById(R.id.search_text);
        loadingView = findViewById(R.id.search_activity_loading);
        muistListView = findViewById(R.id.search_list);
    }

    void initAction(){
        searchIcon.setClickable(true);
        searchIcon.setOnClickListener(this);
        backIcon.setClickable(true);
        backIcon.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search_back_icon:
                finish();
                break;
            case R.id.search_searchIcon:
                searchIconClick();
                break;
        }
    }

    void searchIconClick(){
        searchMusicName = searchText.getText().toString();
        if(searchMusicName.equals("") || searchMusicName == null){
            Toast.makeText(this,"請輸入歌曲名", Toast.LENGTH_SHORT).show();
            return;
        }
        new SearchThread(searchMusicName).start();
        loadingView.setVisibility(View.VISIBLE);
    }

    /*
    下拉刷新
    通過判斷listview第一個item到listview頂部的距離是否為零
     */
    void topFrash(){
    }

    @Override
    public void onItemClick(int i) {
        itemClick(i);
    }

    void itemClick(final int position){
        AlertDialog.Builder dialog = new AlertDialog.Builder (this);
        dialog.setTitle ("提示");
        dialog.setMessage ("是否下载当前歌曲");
        dialog.setPositiveButton ("确定", new DialogInterface.OnClickListener ( ) {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (str_username != null) {
                    //断点下载测试
                    String filename = musicList.get (position).getFilename();
                    String filenameUrl = filename.replaceAll (" ", "%20");
                    String str_url = "http://47.100.202.93/upload/yuan/" + filenameUrl;
                    Intent intent = new Intent(SearchActivity.this, DownloadActivity.class);
                    intent.putExtra("url",str_url);
                    intent.putExtra("filename",filename);
                    startActivity(intent);
                } else {
                    Toast.makeText (SearchActivity.this, "您还未登录", Toast.LENGTH_SHORT).show ( );
                    Intent intent = new Intent (SearchActivity.this, login.class);
                    startActivity (intent);
                }
                dialog.dismiss();
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

    //歌曲搜索线程
    class SearchThread extends Thread {

        private String musicName;

        SearchThread(String name){
            this.musicName = name;
        }

        @Override
        public void run() {
            String temp = "";
            Message msg = new Message ( );
            musicList = new ArrayList<>( );
            HttpPost httpPost = new HttpPost ("http://47.100.202.93/music/search_music.php");
            ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair> ( );
            params.add (new BasicNameValuePair ("musicname", musicName));
            try {
                httpPost.setEntity (new UrlEncodedFormEntity(params, HTTP.UTF_8));
                HttpResponse response = new DefaultHttpClient( ).execute (httpPost);
                int re = response.getStatusLine ( ).getStatusCode ( );
                if (response.getStatusLine ( ).getStatusCode ( ) == 200) {
                    String str = "[" + EntityUtils.toString (response.getEntity ( ), HTTP.UTF_8) + "]";
                    Log.v("fragment",str);
                    JSONArray json = JSONArray.parseArray (str);
                    JSONObject object = null;
                    for (int i = 0; i < json.size ( ); i++) {
                        object = json.getJSONObject (i);
                        MusicFileInfo tmpMusic = new MusicFileInfo ( );
                        tmpMusic.setFilename(object.getString("filename"));
                        tmpMusic.setSinger(object.getString("username"));
                        musicList.add(tmpMusic);
                    }
                    Log.v("fragment",musicList.size()+"");
                    msg.arg1 = SEARCH_SUCCESS;
                    mHandler.sendMessage (msg);
                } else {
                    msg.arg1 = SEARCH_FAIL;
                    mHandler.sendMessage (msg);
                }
            } catch (Exception e) {
                e.printStackTrace ( );
            }
        }
    }

    class Locklist {
        synchronized void write_list(List<MusicFileInfo> list, MusicFileInfo re) {
            list.add (re);
        }

        synchronized ArrayList read_list(ArrayList<MusicFileInfo> list) {
            return list;
        }
    }
}
