package com.example.black.music;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteAction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.RemoteViews;
import android.widget.RemoteViews.RemoteView;

public class notice extends AppCompatActivity {
    private NotificationManager notificationManager;
    private RemoteView remoteView;

    //@Override
    public void OnCreate(Bundle saveInstanceState){
        super.onCreate (saveInstanceState);
        setContentView (R.layout.player);

        notificationManager = (NotificationManager)getSystemService (NOTIFICATION_SERVICE);

      /*  private void createNotification(){
            NotificationCompat.Builder builder = new NotificationCompat.Builder (this);
            Intent intent = new Intent (this,music.class);
            PendingIntent intent1 = PendingIntent.getActivities (this,
                                                                5,
                                                                intent,
                                                                PendingIntent.FLAG_CANCEL_CURRENT);
            remoteView = new RemoteView (getPackageName (),R.layout.history_list);*/

    }
}
