package com.example.myfirstapplication.domain;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


import com.example.myfirstapplication.domain.NotificationDialog;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver {
    //by: https://www.youtube.com/watch?v=OHRKI3B_CwY

    @Override
    public void onReceive(Context context, Intent intent) {


        //System.out.println("chave 1 "+ intent.getExtras().getString("chave1"));
        //System.out.println("chave 2 "+ intent.getExtras().getString("chave2"));
        System.out.println("chave "+ intent.getExtras().getString("chave"));
        String chave = intent.getExtras().getString("chave");
        //String chave="";
        NotificationDialog n = new NotificationDialog();

        if(chave.equals("retornoAlmoco")) {
            n.send(context,10, "Ponto Almoço", "Você precisa registrar retorno do almoço.");
        }

        if(chave.equals("saida")) {
            n.send(context,11, "Ponto Saida", "Você precisa registrar a saida.");
        }

        Log.i("NotForget.ALARME", "O alarme "+ chave +" executou as: "+new Date());

    }






}
