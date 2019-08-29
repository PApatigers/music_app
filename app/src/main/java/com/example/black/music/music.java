package com.example.black.music;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.black.music.activity.DownloadActivity;
import com.example.black.music.activity.MainActivity;
import com.example.black.music.adapter.ShufflingPagerAdpater;
import com.example.black.music.bean.record;
import com.example.black.music.search_cong.music_adapter;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;
import static com.example.black.music.login.str_username;


public class music extends Activity {
    private BottomNavigationView navigationView;
    private ListView listview;
    private EditText search;
    private ImageView sea_button;
    private ImageView mDownload;
    private String musicname;
    public ArrayList<record> music_list = new ArrayList<record> ( );
    public ArrayList<record> temp_list = new ArrayList<record> ( );
    public list lock = new list ( );
    public music_adapter adapter = null;
    private int temp_posi;
    private ProgressDialog progressDialog = null;
    public static String auto_user = null;
    public static String imsi = null;
    private View refrash;

    private List<ImageView> imageViewList;  //輪播圖片集合
    private int[] imageSource = {
            R.drawable.first,
            R.drawable.second,
            R.drawable.seven,
            R.drawable.third
    };
    private static final int SHUFFLING_TIME = 2000;  //輪播間隔時間
    private ViewPager viewPager;
    private boolean isDie = false;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener ( ) {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId ( )) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_me:
                    Intent intent = new Intent (music.this, userpage.class);
                    startActivity (intent);
                    return true;
            }
            return false;
        }
    };
    Handler handler = new Handler ( ) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.obj.equals ("success")) {
                if (music_list.size ( ) != 0) {
                    adapter = new music_adapter (music.this, music_list);
                    listview.setAdapter (adapter);
                } else {
                    Toast.makeText (music.this, "未找到相關歌曲", Toast.LENGTH_SHORT).show ( );
                }

            } else {
                Toast.makeText (music.this, "" + msg.obj, Toast.LENGTH_SHORT).show ( );
            }
            refrash.setVisibility(View.GONE);
        }
    };

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState (bundle);
        String temp_edit = search.getText ( ).toString ( );
        bundle.putString ("search_his", temp_edit);
    }

    @Override
    public void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState (bundle);
        String huifu = bundle.getString ("search_his");
        search.setText (huifu);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_music);

        //启动activity默认不启动软键盘
        getWindow ( ).setSoftInputMode (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        initPermission (this);
        getIMSI ();
        refrash = findViewById(R.id.loading);
        search = (EditText) findViewById (R.id.searchtext);
        sea_button = findViewById (R.id.mainsearchicon);
        sea_button.setClickable(true);
        mDownload = findViewById(R.id.downicon);
        mDownload.setClickable(true);

        listview = (ListView) findViewById (R.id.list);
        listview.setTextFilterEnabled (true);
        topFlash();

        initData();


        sea_button.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                musicname = search.getText ( ).toString ( );
                if (musicname.equals ("")) {
                    Toast.makeText (music.this, "请输入歌曲名", Toast.LENGTH_SHORT).show ( );
                } else {
                    new Thread (new myThread ( )).start ( );
                    refrash.setVisibility(View.VISIBLE);
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

                                    if (str_username != null) {

                                        //断点下载测试
                                        String filename = music_list.get (temp_posi).filename;
                                        String filenameUrl = music_list.get (temp_posi).filename.replaceAll (" ", "%20");
                                        String str_url = "http://47.100.202.93/upload/yuan/" + filenameUrl;
                                        Intent intent = new Intent(music.this,DownloadActivity.class);
                                        intent.putExtra("url",str_url);
                                        intent.putExtra("filename",filename);
                                        startActivity(intent);
                                        //

//                                        progressDialog = new ProgressDialog (music.this);
//                                        progressDialog.setTitle ("提示");
//                                        progressDialog.setMessage ("正在下载");
//                                        progressDialog.show ( );
//                                        new Thread (new upload_thread ( )).start ( );
//                                        dialog.dismiss ( );
                                    } else {
                                        Toast.makeText (music.this, "您还未登录", Toast.LENGTH_SHORT).show ( );
                                        Intent intent = new Intent (music.this, login.class);
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

        mDownload.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View view) {
                Intent intent1_2 = new Intent (music.this, MainActivity.class);
                startActivity (intent1_2);
            }
        });

        navigationView = findViewById (R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener (mOnNavigationItemSelectedListener);
    }

    //初始化輪播圖片資源
    void initData(){
        viewPager = findViewById(R.id.img_v);
        imageViewList = new ArrayList<>();
        for(int i =0 ; i < imageSource.length ;i++ ){
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(imageSource[i]);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageViewList.add(imageView);
        }
        Log.v("img_v",""+imageViewList.size() + " + " + imageSource.length);

        ShufflingPagerAdpater adpater = new ShufflingPagerAdpater(viewPager,imageViewList);
        viewPager.setAdapter(adpater);
        viewPager.setCurrentItem(0);
        autoPlay();
    }

    //设置首页图片轮播
    void autoPlay(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(!isDie){
                    Log.v("shuffing","進入刷新");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                            Log.v("shuffing","替換"+viewPager.getCurrentItem());
                        }
                    });
                    SystemClock.sleep(SHUFFLING_TIME);
                }
            }
        }).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        isDie = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isDie = true;
    }

    //歌曲搜索线程
    public class myThread implements Runnable {
        @Override
        public void run() {
            String temp = "";
            Message msg = new Message ( );
            music_list = new ArrayList<> ( );
            HttpPost httpPost = new HttpPost ("http://47.100.202.93/music/search_music.php");
            ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair> ( );
            params.add (new BasicNameValuePair ("musicname", musicname));
            try {
                httpPost.setEntity (new UrlEncodedFormEntity (params, HTTP.UTF_8));
                HttpResponse response = new DefaultHttpClient ( ).execute (httpPost);
                int re = response.getStatusLine ( ).getStatusCode ( );
                if (response.getStatusLine ( ).getStatusCode ( ) == 200) {
                    String str =  EntityUtils.toString (response.getEntity ( ), HTTP.UTF_8);
                    Log.v("searchmusic",str);
                    if(! str.equals("0")){
                        str = "[" + str + "]";
                        Log.v("searchmusic","進入解析");
                        JSONArray json = JSONArray.parseArray (str);
                        JSONObject object = null;
                        for (int i = 0; i < json.size ( ); i++) {
                            object = json.getJSONObject (i);
                            record sea_muc = new record ( );
                            sea_muc.filename = object.getString ("filename");
                            sea_muc.username = object.getString ("username");
                            lock.write_list (music_list, sea_muc);
                        }
                    }
                    Log.v("searchmusic",""+music_list.size());
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



    class list {
        synchronized void write_list(ArrayList<record> list, record re) {
            list.add (re);
        }

        synchronized ArrayList read_list(ArrayList<record> list) {
            return list;
        }
    }

    //下载信息存入数据库
    public Boolean download_mysql(Context context, String filename) {
        String str = "";
        String user_info = str_username;
        Log.v ("", "yishoudao");
        HttpPost RE_POST = new HttpPost ("http://47.100.202.93/music/download_mysql.php");
        ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair> ( );
        params.add (new BasicNameValuePair ("user_info", user_info));
        params.add (new BasicNameValuePair ("filename", filename));
        try {
            RE_POST.setEntity (new UrlEncodedFormEntity (params, HTTP.UTF_8));
            final HttpResponse RE_REPOSE = new DefaultHttpClient ( ).execute (RE_POST);
            Log.e ("status", RE_REPOSE.getStatusLine ( ).toString ( ));
            int temp = RE_REPOSE.getStatusLine ( ).getStatusCode ( );
            if (RE_REPOSE.getStatusLine ( ).getStatusCode ( ) == 200) {
                Log.v ("", "download_in_mysql");
                try {
                    str = EntityUtils.toString (RE_REPOSE.getEntity ( ), HTTP.UTF_8);
                } catch (IOException e) {
                    e.printStackTrace ( );
                }
                if (str.equals ("null")) {
                    str = "下载录入数据库失败";
                    return false;
                }
            } else {
                str = "link_error";
                Log.e ("Exception", str);
                return false;
            }
        } catch (Exception x) {
            x.printStackTrace ( );
        }
        return true;

    }

    private List<String> permissionList = new ArrayList<> ( );
    String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE};

    public void initPermission(Context context) {

        permissionList.clear ( );
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission (this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add (permissions[i]);//添加还未授予的权限
            }
        }

        if (permissionList.size ( ) > 0) ActivityCompat.requestPermissions (this, permissions, 1);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean haspermissionmiss = false;
        if (requestCode == 1) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == -1) {
                    haspermissionmiss = true;
                }
            }
        }

        if (haspermissionmiss) {
            Toast.makeText (this, "授权失败", Toast.LENGTH_SHORT).show ( );
            this.finish ( );
        } else {
            Toast.makeText (this, "授权完成", Toast.LENGTH_SHORT).show ( );
            Log.e ("tag","授权完成");

        }

    }

    String getIMSI(){
        TelephonyManager mTelephonyMgr = (TelephonyManager) this.getSystemService (Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission (this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return " ";
        }
        imsi = mTelephonyMgr.getSubscriberId ( );
        if(imsi == null)
            imsi = "";
        Log.e ("tag","imsi" + imsi);
        return imsi;
    }


    void topFlash(){
        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState){
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        if(listview.getFirstVisiblePosition() == 0){
                            refrash.setVisibility(View.VISIBLE);
                            if (musicname != null)
                                new Thread(new myThread()).start();
                            else
                                Log.v("refrash","musicname為空");
                            Toast.makeText(music.this,"到頂部", LENGTH_SHORT).show();
                        }

                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }
}


