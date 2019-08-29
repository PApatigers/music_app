package com.example.black.music.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.black.music.R;

public class DownloadAdapter implements Adapter {

    private Context mContext;
    DownloadAdapter(Context context){
        this.mContext = context;
    }
    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView != null)
            return convertView;
        convertView = View.inflate(this.mContext, R.layout.item_download,parent);
        viewHolder = new ViewHolder();
        viewHolder.btStart = convertView.findViewById(R.id.btStart);
        viewHolder.btStop = convertView.findViewById(R.id.btStop);
        viewHolder.progressBar = convertView.findViewById(R.id.pbProgress);

        viewHolder.progressBar.setMax(100);
        viewHolder.btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        viewHolder.btStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    class ViewHolder{
        Button btStop,btStart;
        ProgressBar progressBar;
    }
}
