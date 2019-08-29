package com.example.black.music.activity;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.black.music.R;
import com.example.black.music.myrecycle_adapter;

import java.util.ArrayList;
import java.util.List;

public class recycleview extends Activity {

    private RecyclerView recyclerView;
    private List<String> data;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.recycleview);

        recyclerView = findViewById (R.id.recycleview);
        recyclerView.setLayoutManager (new LinearLayoutManager (this));
        recyclerView.setAdapter (new myrecycle_adapter (data));
    }

    void get_data(Context context){
        data = new ArrayList<> ();
        Uri uri = Uri.parse ("");
        Cursor cursor = context.getContentResolver ().query (uri,null,null,null,null);
    }
}
