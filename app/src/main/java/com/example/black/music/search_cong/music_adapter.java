package com.example.black.music.search_cong;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.black.music.R;
import com.example.black.music.fabu_seach.record;

import java.util.ArrayList;
import java.util.List;

public class music_adapter extends BaseAdapter {
    private Context context;
    private List<record> list; //listview要显示的数据
    private LayoutInflater layoutInflater;

    public music_adapter(Object seach , ArrayList list){
        this.context = (Context) seach;
        this.list = list;
        layoutInflater = (LayoutInflater)this.context.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount(){ return list.size (); }

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
            view = view.inflate(context, R.layout.search_list,null);
            holder.musicname = (TextView) view.findViewById(R.id.musicname);
            holder.username = (TextView) view.findViewById(R.id.username);
            holder.position = (TextView) view.findViewById (R.id.music_postion);
            view.setTag (holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }

        //给控件赋值
        holder.musicname.setText(list.get(i).filename.toString());
        holder.username.setText(list.get(i).username.toString());
        holder.position.setText(i+1+"");;


        return view;
    }
    class ViewHolder{
        TextView username;
        TextView musicname;
        TextView position;
    }
}
