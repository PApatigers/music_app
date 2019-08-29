package com.example.black.music;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class myrecycle_adapter extends RecyclerView.Adapter<myrecycle_adapter.VH> {

    private List<String> mDatas;

    //构建viewHolder
    public static class VH extends  RecyclerView.ViewHolder{
        public final TextView title;
        public VH(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.title);
        }
    }

    public myrecycle_adapter(List<String> data) {
        this.mDatas = data;
    }

    @Override
    public void onBindViewHolder(@NonNull VH vh, int i) {

    }

    @Override
    public int getItemCount() {
        return mDatas.size ();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from (viewGroup.getContext ()).inflate (R.layout.recvcleview_item,viewGroup,false);
        return new VH(v);
    }
}
