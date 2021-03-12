package com.example.myfirstapplication;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.View;


import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {


        //System.out.println("chave 1 "+ intent.getExtras().getString("chave1"));
        //System.out.println("chave 2 "+ intent.getExtras().getString("chave2"));
        System.out.println("chave "+ intent.getExtras().getString("chave"));
        String chave = intent.getExtras().getString("chave");
        //String chave="";
        NotificationDialog n = new NotificationDialog();

        if(chave.equals("retornoAlmoco")) {
            n.send(context,10, "Ponto Almoço", "Você precisa registrar retorno do almoço"+ new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
        }

        if(chave.equals("saida")) {
            n.send(context,11, "Ponto Saida", "Você precisa registrar a saida"+ new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
        }

        Log.i("NotForget.ALARME", "O alarme "+ chave +" executou as: "+new Date());

    }






}
