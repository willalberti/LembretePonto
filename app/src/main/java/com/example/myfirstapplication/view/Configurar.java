package com.example.myfirstapplication.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myfirstapplication.R;
import com.example.myfirstapplication.business.*;

public class Configurar extends Activity {

    private Lembrar bsLembrar;
    private ViewHolder viewHolder = new ViewHolder();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configurar);
        bsLembrar = new Lembrar(this);
        this.viewHolder.txtEntrada=findViewById(R.id.txtEntrada);
        this.viewHolder.txtInicioAlmoco=findViewById(R.id.txtInicioAlmoco);
        this.viewHolder.txtFimAlmoco=findViewById(R.id.txtFimAlmoco);


        this.viewHolder.txtEntrada.setText(bsLembrar.obterValorArmazenamento("Entrada"));
        this.viewHolder.txtInicioAlmoco.setText(bsLembrar.obterValorArmazenamento("InicioAlmoco"));
        this.viewHolder.txtFimAlmoco.setText(bsLembrar.obterValorArmazenamento("FimAlmoco"));


    }

    public void irVoltar(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void salvar(View v) {
        try {
            bsLembrar.alterarDatas(this.viewHolder.txtEntrada.getText().toString(),
                    this.viewHolder.txtInicioAlmoco.getText().toString(),
                    this.viewHolder.txtFimAlmoco.getText().toString());

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Log.i("Erro", e.getMessage());
        }
    }


    private static class ViewHolder{
        TextView txtEntrada;
        TextView txtInicioAlmoco;
        TextView txtFimAlmoco;
    }


}
