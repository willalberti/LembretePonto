package com.example.myfirstapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;



public class NotificationDialog extends AppCompatActivity  {



    public void send(Context c,int pId, String pTitulo, String pMensagem) {
        try {

            NotificationManager notificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
            String NOTIFICATION_CHANNEL_ID = "tutorialspoint_01";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_MAX);
                // Configure the notification channel.
                notificationChannel.setDescription("texto da notificação");
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(1);
                notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(c, NOTIFICATION_CHANNEL_ID);
            notificationBuilder.setAutoCancel(true)
                    .setDefaults(android.app.Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setTicker("Lembrete")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentTitle(pTitulo)
                    .setContentText(pMensagem)
                    .setContentInfo("Information");
            notificationManager.notify(pId, notificationBuilder.build());


        } catch (Exception e) {
            System.out.println(e.getMessage());
            Log.i("NotForget.notification",e.getMessage());
        }

    }
}
