package com.example.black.music;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.black.music.login.str_username;


public class userpage extends Activity {
    Button re;
    TextView nick,fabu,history,shuiyin_detection;
    View.OnClickListener listener_login = null , listener_user = null;
    private static Bundle outState = null;
    public String temp="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e ("tag","正在create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.own);

        re = (Button) findViewById(R.id.re);
        nick = (TextView)findViewById(R.id.nickname);
        fabu = (TextView)findViewById(R.id.fabu);
        history = (TextView)findViewById(R.id.buy_his);
        shuiyin_detection = findViewById (R.id.shuiyin);

        re.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1_2 = new Intent(userpage.this,music.class);
                startActivity(intent1_2);
            }
        });

        View user = findViewById(R.id.nickname);
        String aaaa = str_username;
        if (aaaa.equals ("")){
            if (outState != null){
                nick.setText((outState.getString("nick")));
            }
        }
        else nick.setText (aaaa);

        listener_login = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    startActivityForResult(new Intent(userpage.this,login.class),1);
                    userpage.this.finish ();
            }
        };
        listener_user = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(userpage.this,userdata.class);
                Bundle bundle   = new Bundle ();
                bundle.putString ("nick",nick.getText ().toString ());
                intent.putExtras (bundle);
                startActivity (intent);
            }
        };

        if (nick.getText().toString().equals("请登录")){
            user.setOnClickListener(listener_login);
            fabu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(userpage.this,"您还未登录！",Toast.LENGTH_SHORT).show();
                }
            });
            history.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(userpage.this,"您还未登录！",Toast.LENGTH_SHORT).show();
                }
            });
            shuiyin_detection.setOnClickListener (new View.OnClickListener ( ) {
                @Override
                public void onClick(View v) {
                    Toast.makeText (userpage.this,"您还未登录",Toast.LENGTH_SHORT).show ();
                }
            });
        }
        else{
            user.setOnClickListener(listener_user);
            fabu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent_fabu = new Intent(userpage.this,seach_localmusic.class);
                    String temp = nick.getText().toString();
                    intent_fabu.putExtra("nick",temp);
                    startActivity(intent_fabu);
                }
            });
            history.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent_his = new Intent(userpage.this,history.class);
                    intent_his.putExtra("nick",outState.getString("nick").toString());
                    startActivity(intent_his);
                }
            });
            shuiyin_detection.setOnClickListener (new View.OnClickListener ( ) {
                @Override
                public void onClick(View v) {
                    Intent intent_detection = new Intent(userpage.this,shuiyin_detection.class);
                    startActivity (intent_detection);
                }
            });
        }
/*        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_his = new Intent(userpage.this,history.class);
                intent_his.putExtra("nick","quququ");
                startActivity(intent_his);
            }
        });*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("tag","正在回调result");
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 4) {
            String s=data.getStringExtra("nickname");
            temp = s ;
            nick.setText(s);
        }

        if (requestCode == 2 && resultCode == 5){
            String s = data.getStringExtra("nick");
            nick.setText(s);
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
/*        if (outState != null){
            outState.clear();
            outState=null;
        }*/
        if (!nick.getText().toString().equals("请登录") && !temp.equals("")) {
            outState = new Bundle();
            outState.putString("nick",temp);
        }
        else if (temp.equals("")){
            outState = new Bundle();
            outState.putString("nick",nick.getText().toString());
        }
    }

    @Override
    protected void onResume() {
        Log.e ("tag","正在resume" );
        super.onResume ( );

    }
}

