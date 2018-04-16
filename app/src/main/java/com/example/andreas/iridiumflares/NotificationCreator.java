package com.example.andreas.iridiumflares;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationCreator extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("alarm","sent notification");
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = intent.getParcelableExtra("notification");
        int notificationId = intent.getIntExtra("notificationID", 0);
        notificationManager.notify(notificationId, notification);
    }

}