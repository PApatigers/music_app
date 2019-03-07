package com.example.black.music;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class userdata extends Activity {
    Button re_data,exit;
    TextView nickname;
    //View realname,IDnum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user);

        Intent intent = getIntent();
        String name = intent.getStringExtra("nick");
        nickname = (TextView)findViewById(R.id.nickname);
        nickname.setText(name);

        /*realname = realname.findViewById(R.id.realname);
        realname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        IDnum = IDnum.findViewById(R.id.IDnum);
        IDnum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/

        re_data = (Button)findViewById(R.id.re_data);
        re_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        exit = (Button) findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(userdata.this,login.class);
                setResult(5,intent);
                intent.putExtra("nick","请登录");
                clean_sp ();
                userdata.this.finish();
                Toast.makeText(userdata.this,"已退出",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void clean_sp(){
        SharedPreferences sp = getSharedPreferences ("login",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit ();
        editor.clear ();
        editor.commit ();
    }
}
