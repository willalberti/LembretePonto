package com.example.NotForget.view;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.NotForget.R;
import com.example.NotForget.business.*;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;

public class MainActivity extends AppCompatActivity {

    /* private Date dtEntrada;
    private Date dtSaida;
    private Date dtInicioAlmoco;
    private Date dtFimAlmoco;

     */
    private ViewHolder viewHolder = new ViewHolder();
    public Timer timer;
    private Lembrar bsLembrar;


    private static class ViewHolder{
        TextView txtEntrada;
        TextView txtInicioAlmoco;
        TextView txtFimAlmoco;
        TextView txtSaida;
        TextView txtData;
        TextView txtLembrete;
        Button bntSaida;
        Button bntEntrada;
        Button bntFimAlmoco;
        Button bntInicioAlmoco;
        ImageView imgSino;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bsLembrar= new Lembrar(this);

        this.viewHolder.txtEntrada=findViewById(R.id.txtEntrada);
        this.viewHolder.txtInicioAlmoco=findViewById(R.id.txtInicioAlmoco);
        this.viewHolder.txtFimAlmoco=findViewById(R.id.txtFimAlmoco);
        this.viewHolder.txtSaida=findViewById(R.id.txtSaida);
        this.viewHolder.txtData=findViewById(R.id.txtData);
        this.viewHolder.txtLembrete=findViewById(R.id.txtLembrete);
        this.viewHolder.bntSaida = findViewById(R.id.bntSaida);
        this.viewHolder.bntEntrada = findViewById(R.id.bntEntrada);
        this.viewHolder.bntFimAlmoco = findViewById(R.id.bntFimAlmoco);
        this.viewHolder.bntInicioAlmoco = findViewById(R.id.bntInicioAlmoco);
        this.viewHolder.imgSino = findViewById(R.id.imgSino);



        //o formulario é resetado quando o app identifica o mudança de dia
        bsLembrar.verificarSeDiaRegistrado();

        //Exibe data atual na tela
        this.viewHolder.txtData.setText(bsLembrar.obterValorArmazenamento("tdata"));


        //Atualiza o formuário de acordo com as regras para a visuaçização dos botoes
        updateViewComponetState();


    }

    //Limpa os valores armazenados na SharedPreferences possibilitando que o metodo resetComponents() retorne os componentes ao estado inicial
    private void resetComponents(){
        bsLembrar.resetArmazenamento();
    }

    //Atualiza o formuário de acordo com as regras para a visuaçização dos botoes
    private void updateViewComponetState(){

        this.viewHolder.bntEntrada.setEnabled(false);
        this.viewHolder.bntInicioAlmoco.setEnabled(false);
        this.viewHolder.bntFimAlmoco.setEnabled(false);
        this.viewHolder.bntSaida.setEnabled(false);


        //recupera os valores da SharedPreferences e exibe na tela
        this.viewHolder.txtEntrada.setText(bsLembrar.obterValorArmazenamento("Entrada"));
        this.viewHolder.txtInicioAlmoco.setText(bsLembrar.obterValorArmazenamento("InicioAlmoco"));
        this.viewHolder.txtFimAlmoco.setText(bsLembrar.obterValorArmazenamento("FimAlmoco"));
        this.viewHolder.txtSaida.setText(bsLembrar.obterValorArmazenamento("Saida"));

        this.viewHolder.imgSino.setVisibility(View.INVISIBLE);
        this.viewHolder.txtLembrete.setText("");

        //verifica quais botoes devem ser habilitados
        String bntHabilitar = bsLembrar.verificarPontoRegistrado();

        if (bntHabilitar.equals("Entrada")){
            this.viewHolder.bntEntrada.setEnabled(true);
        }
        else if(bntHabilitar.equals("InicioAlmoco")){
            this.viewHolder.bntInicioAlmoco.setEnabled(true);
        }
        else if(bntHabilitar.equals("FimAlmoco")){
            this.viewHolder.bntFimAlmoco.setEnabled(true);
            this.viewHolder.imgSino.setVisibility(View.VISIBLE);
            this.viewHolder.txtLembrete.setText("Lembrete fim de almoço programado: "+ bsLembrar.obterValorArmazenamento("dtRemFimAlmoco"));
        }
        else if(bntHabilitar.equals("Saida")){
            this.viewHolder.bntSaida.setEnabled(true);
            this.viewHolder.imgSino.setVisibility(View.VISIBLE);
            this.viewHolder.txtLembrete.setText("Lembrete saida programado: "+ bsLembrar.obterValorArmazenamento("dtRemSaida"));
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void registrarEntrada(View v){
        bsLembrar.registrarEntrada(null);
        updateViewComponetState();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void registrarSaida(View v){
        bsLembrar.registrarSaida(null);
        updateViewComponetState();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void inicioAlmoco(View v){
        bsLembrar.registrarInicioAlmoco(null);
         updateViewComponetState();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void fimAlmoco(View v){
        bsLembrar.registrarFimAlmoco(null);
        updateViewComponetState();
    }

    public void irConfigurar(View v){
        Intent intent = new Intent(this, Configurar.class);
        startActivity(intent);
    }



}