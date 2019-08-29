package com.example.black.music.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.black.music.R;
import com.example.black.music.history;
import com.example.black.music.login;
import com.example.black.music.seach_localmusic;
import com.example.black.music.shuiyin_detection;
import com.example.black.music.userdata;

public class MeFragment extends Fragment implements View.OnClickListener{

    private View mRootView;
    private TextView nickName,uploadMusic,downloadHistory,detection;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_me,null);
        initView();
        initAction();
        return mRootView;
    }

    void initView(){
        nickName = mRootView.findViewById(R.id.nickname);
        uploadMusic = mRootView.findViewById(R.id.fabu);
        downloadHistory = mRootView.findViewById(R.id.buy_his);
        detection = mRootView.findViewById(R.id.shuiyin);
    }

    void initAction(){

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.nickname:
                if(!isOnLine()){

                }
//                    startIntentSenderForResult();
                else
                    startActivity(new Intent(getActivity(), userdata.class));
                break;
            case R.id.fabu:
                if(isOnLine()){
                    startActivity(new Intent(getActivity(), seach_localmusic.class));
                }
                break;
            case R.id.buy_his:
                if(isOnLine())
                    startActivity(new Intent(getActivity(), history.class));
                break;
            case R.id.shuiyin:
                if(isOnLine())
                    startActivity(new Intent(getActivity(), shuiyin_detection.class));
                break;
        }
    }


    //判斷登錄狀態
    boolean isOnLine(){

        if(true)
            return true;
        Toast.makeText(getContext(),"您還未登錄",Toast.LENGTH_SHORT).show();
        return false;
    }
}
