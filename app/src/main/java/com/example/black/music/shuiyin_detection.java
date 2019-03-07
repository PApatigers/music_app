package com.example.black.music;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class shuiyin_detection extends Activity {

    TextView yuan_path,ce_path;
    Button yuan_button,ce_button,start_button,return_button;
    String temp_path;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.shuiyin_detection);

        yuan_path = findViewById (R.id.yuan_path);
        ce_path = findViewById (R.id.ce_path);
        yuan_button = findViewById (R.id.yuan_button);
        ce_button = findViewById (R.id.ce_button);
        start_button = findViewById (R.id.start);
        return_button = findViewById (R.id.return_ce);

        //添加点击事件
        yuan_button.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                open_filebrowser();
                yuan_path.setText (temp_path);
            }
        });
        ce_button.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                open_filebrowser ();
                ce_path.setText (temp_path);
            }
        });
        start_button.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                String path_yuan = yuan_path.getText ().toString ();
                String path_ce = ce_path.getText ().toString ();
            }
        });
        return_button.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View v) {
                shuiyin_detection.this.finish();
            }
        });
    }

    void open_filebrowser(){
        Intent intent = new Intent (Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent,1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        temp_path = "";
        if (resultCode == Activity.RESULT_OK) {//是否选择，没选择就不会继续
            Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);
            int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            actualimagecursor.moveToFirst();
            String path = actualimagecursor.getString(actual_image_column_index);
            temp_path = path;
        }
    }
}
