package com.example.black.music.search_cong;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.black.music.R;
import com.example.black.music.fabu_seach.record;

import java.util.List;

public class his_adapter extends BaseAdapter {
    private Context context;
    private List<record> list; //listview要显示的数据
    //private his_adapter adapter;

    public his_adapter(Object seach , List list){
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
        ViewHolder holder;
        if (view == null){
            holder = new ViewHolder();
            view = view.inflate(context, R.layout.history_list,null);
            holder.HO_filename = (TextView) view.findViewById(R.id.xml_filename);
            holder.qkl_id = (TextView)view.findViewById (R.id.qkl_id);
            holder.H0_date = (TextView) view.findViewById(R.id.xml_date);
            holder.position = (TextView) view.findViewById (R.id.music_postion);
            view.setTag (holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }

        //给控件赋值
        holder.HO_filename.setText(list.get (i).filename.toString ());
        holder.qkl_id.setText (list.get (i).qkl_id.toString ());
        holder.H0_date.setText(list.get(i).date.toString());
        holder.position.setText(i+1+"");;

        return view;
    }
    class ViewHolder{
        TextView qkl_id;                    //区块链id
        TextView HO_filename ;              //发布歌曲名
        TextView H0_date ;                  //发布时间
        TextView position;
    }
}
