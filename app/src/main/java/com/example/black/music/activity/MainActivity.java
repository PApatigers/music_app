package com.example.black.music.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.black.music.R;
import com.example.black.music.adapter.MainPagerAdapter;
import com.example.black.music.fragment.MainFragment;
import com.example.black.music.fragment.MeFragment;
import com.example.black.music.view.ChildViewPager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements View.OnClickListener{

    private List<Fragment> list = new ArrayList<>();
    private BottomNavigationView navigationView;
    private ImageView searchIcon,downIcon;
    FragmentManager fm ;
    ChildViewPager viewPager;
    FragmentPagerAdapter fragmentPagerAdapter;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener ( ) {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId ( )) {
                case R.id.navigation_home:
                    viewPager.setCurrentItem (0);
                    return true;
                case R.id.navigation_me:
                    viewPager.setCurrentItem (1);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle s){
        super.onCreate (s);
        fm = getSupportFragmentManager();
        setContentView (R.layout.activity_main);
        initFragmentlist();
        fragmentPagerAdapter = new MainPagerAdapter(fm,list);
        initview();
        initPermission(this);
        initAction();
    }

    void initFragmentlist(){
        Fragment index = new MainFragment();
        Fragment user = new MeFragment();
        list.add (index);
        list.add (user);

        Log.v("fragment",""+list.size() + fm.getFragments().size());
    }

    void initview(){
        viewPager = findViewById (R.id.main_viewpager);
        viewPager.setAdapter (fragmentPagerAdapter);
        viewPager.setCurrentItem (0);
        navigationView = findViewById (R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        searchIcon = findViewById(R.id.main_search_icon);
        downIcon = findViewById(R.id.main_down_icon);

        searchIcon.setClickable(true);
        searchIcon.setOnClickListener(this);
        downIcon.setClickable(true);
        downIcon.setOnClickListener(this);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                MenuItem item = navigationView.getMenu().getItem(i);
                item.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    void initAction(){
        //启动activity默认不启动软键盘
        getWindow ( ).setSoftInputMode (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
        String imsi;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.main_down_icon:
                startActivity(new Intent(this,DownloadActivity.class));
                break;
            case R.id.main_search_icon:
                startActivity(new Intent(this,SearchActivity.class));
                break;
        }
    }
}
