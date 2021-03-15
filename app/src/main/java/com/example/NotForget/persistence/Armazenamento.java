package com.example.NotForget.persistence;

import android.content.Context;
import android.content.SharedPreferences;

public class Armazenamento {

    //graca tipos primitivos no sistema android em chave/valor possibilitando sua recuperação mesmo com o programa sendo encerrado
    public void setPersistValue(String pChave, String pValor, Context context){
        // Precisamos de um objeto Editor para fazer mudanças de preferência.
        // Todos os objetos são de android.context.Context
        SharedPreferences settings = context.getSharedPreferences("PREFS_NAME", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(pChave, pValor);
        editor.commit();
    }


    //obtem dados amarenados no sistema android
    public String getPersistValue(String pChave, Context context){
        SharedPreferences settings = context.getSharedPreferences("PREFS_NAME", 0);
        return settings.getString(pChave, "");

    }
}
