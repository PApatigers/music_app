package com.example.black.music.search_cong;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.black.music.R;

import java.util.List;

public class myadapter extends BaseAdapter {
    private Context context;
    private List<song> list; //listview要显示的数据
    private myadapter adapter;

    public myadapter(Object seach , List list){
        this.context = (Context) seach;
        this.list = list;
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
            view.setTag (holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }

        //给控件赋值
        holder.song.setText(list.get(i).song.toString());
        holder.singer.setText(list.get(i).singer.toString());
        //转换时间
        int duration =  list.get(i).duration;
        String time = bean.formatTime(duration);
        holder.duration.setText(time);
        holder.pasition.setText(i+1+"");

        return view;
    }
    class ViewHolder{
        TextView song;
        TextView singer;
        TextView duration;
        TextView pasition;
    }

    public void onClilck(View view){
        
    }
}
