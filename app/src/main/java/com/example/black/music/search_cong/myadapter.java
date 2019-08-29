package com.example.black.music.search_cong;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.black.music.R;
import com.example.black.music.play;

import java.util.List;

public class myadapter extends BaseAdapter implements View.OnClickListener{
    private Context context;
    private List<song> list; //listview要显示的数据
    private myadapter adapter;
    private play playTask;
    private String tmpPath;

    private int tmpPosition;

    public myadapter(Object seach , List list){
        this.context = (Context) seach;
        this.list = list;
        playTask = play.getInstance();
    }

    @Override
    public int getCount(){
        return list.size();
    }

    @Override
    public Object getItem(int i){
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    //为listview每一项添加属性
    public View getView(final int i , View view , ViewGroup ViewGroup){
        ViewHolder holder = null;
        if (view == null){
            holder = new ViewHolder();
            view = view.inflate(context, R.layout.music_list,null);
            holder.song = (TextView) view.findViewById(R.id.music_song);
            holder.singer = (TextView) view.findViewById(R.id.music_singer);
            holder.duration = (TextView) view.findViewById(R.id.music_duration);
            holder.pasition = (TextView) view.findViewById(R.id.music_postion);
            holder.playBt = view.findViewById(R.id.play_bt);
            holder.pauseBt = view.findViewById(R.id.pause_bt);
            view.setTag (holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }

        //给控件赋值
        holder.song.setText(list.get(i).song.toString());
        holder.singer.setText(list.get(i).singer.toString());
        tmpPath = list.get(i).path;
        //转换时间
        int duration =  list.get(i).duration;
        String time = bean.formatTime(duration);
        holder.duration.setText(time);
        holder.pasition.setText(i+1+"");

        if(holder.playBt != null){
            tmpPosition = i;
            holder.playBt.setOnClickListener(this);
            holder.pauseBt.setOnClickListener(this);
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.play_bt:
                playTask.music_paly(list.get(tmpPosition).path);
                Log.v("playmusic",tmpPath +"  "+ tmpPosition);
                break;
            case R.id.pause_bt:
                playTask.pause();
                break;
        }
    }

    class ViewHolder{
        TextView song;
        TextView singer;
        TextView duration;
        TextView pasition;

        Button playBt,pauseBt;
    }

}
