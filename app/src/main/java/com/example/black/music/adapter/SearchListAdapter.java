package com.example.black.music.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.black.music.R;
import com.example.black.music.bean.MusicFileInfo;

import java.util.List;

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.MyHolder>{


    private Context context;
    private List<MusicFileInfo> musicList;
    private OnItemClickListener listener;

    public SearchListAdapter(Context context, List<MusicFileInfo> musicFileInfos){
        this.context = context;
        this.musicList = musicFileInfos;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_list,null);
        MyHolder holder = new MyHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null)
                    listener.onItemClick(i);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        if(musicList.get(i) != null){
            myHolder.musicPosition.setText(""+(i+1));
            myHolder.musicName.setText(musicList.get(i).getFilename());
            myHolder.upLoader.setText(musicList.get(i).getSinger());
        }
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        private TextView musicPosition , musicName , upLoader;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            musicPosition = itemView.findViewById(R.id.music_postion);
            musicName = itemView.findViewById(R.id.musicname);
            upLoader = itemView.findViewById(R.id.username);
        }

    }

    public void setItemClickListner(OnItemClickListener listner){
        this.listener = listner;
    }

    public interface OnItemClickListener{
        void onItemClick(int i);
    }

}
