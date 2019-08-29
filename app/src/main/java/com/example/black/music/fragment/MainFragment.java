package com.example.black.music.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.support.design.widget.TabLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.black.music.R;
import com.example.black.music.activity.DownloadActivity;
import com.example.black.music.activity.SearchActivity;
import com.example.black.music.adapter.MainPagerAdapter;
import com.example.black.music.adapter.SearchListAdapter;
import com.example.black.music.adapter.ShufflingPagerAdpater;
import com.example.black.music.bean.MusicFileInfo;
import com.example.black.music.login;
import com.example.black.music.test.TestActivity;
import com.example.black.music.view.ChildViewPager;

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

public class MainFragment extends Fragment{

    private View mRootView;
    private ViewPager viewPager;
    private List<ImageView> imageViewList;  //輪播圖片集合
    private List<Fragment> fragments;
    private TabLayout tableLayout;
    private ChildViewPager hotListViewPager;
    private FragmentManager fm;
    private int[] imageSource = {
            R.drawable.first,
            R.drawable.second,
            R.drawable.seven,
            R.drawable.third,
            R.drawable.ranklist_fifth
    };
    private static final int SHUFFLING_TIME = 2000;  //輪播間隔時間

    //是否滑動
    private boolean scrollFlag = false;

    private List<String> tabList;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null){
            mRootView = inflater.inflate(R.layout.fragment_main,null);

            initData();
            initView();
            initAciton();
        }
        return mRootView;
    }

    //初始化輪播圖片資源
    void initData(){
        imageViewList = new ArrayList<>();
        for(int i =0 ; i < imageSource.length ;i++ ){
            ImageView imageView = new ImageView(getContext());
            imageView.setImageResource(imageSource[i]);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageViewList.add(imageView);
        }

        tabList = new ArrayList<>();
        tabList.add(getString(R.string.main_tab1));
        tabList.add(getString(R.string.main_tab2));

    }

    void initView(){
        //listView = mRootView.findViewById(R.id.main_list);
        viewPager = mRootView.findViewById(R.id.img_v);
        //loading = mRootView.findViewById(R.id.loding);
        tableLayout = mRootView.findViewById(R.id.main_table);

        tableLayout.addTab(tableLayout.newTab().setText(R.string.main_tab1));
        tableLayout.addTab(tableLayout.newTab().setText(R.string.main_tab2));
        hotListViewPager = mRootView.findViewById(R.id.list_pager);

        Fragment downLoadHot = new DownLoadHotListFragment();
        Fragment upLoadHot = new UploadHotListFragment();
        fragments = new ArrayList<>();
        fragments.add(downLoadHot);
        fragments.add(upLoadHot);
        fm = getFragmentManager();
        PagerAdapter adapter = new MainPagerAdapter(fm,fragments,tabList);
        hotListViewPager.setAdapter(adapter);
        hotListViewPager.setCurrentItem(0);
        tableLayout.setupWithViewPager(hotListViewPager);

        ShufflingPagerAdpater adpater = new ShufflingPagerAdpater(viewPager,imageViewList);
        viewPager.setAdapter(adpater);
        viewPager.setCurrentItem(0);

        //topFrash();
        autoPaly();
    }

    void initAciton(){


        hotListViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }




    //设置首页图片轮播
    void autoPaly(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                    }
                });
                SystemClock.sleep(SHUFFLING_TIME);
            }
        });
    }


}
