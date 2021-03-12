package com.example.myfirstapplication;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private Date dtEntrada;
    private Date dtSaida;
    private Date dtInicioAlmoco;
    private Date dtFimAlmoco;
    public Timer timer;



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //o formulario é resetado quando o app identifica o mudança de dia
        String DataAtual = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        //if(getPersistValue("tdata").equalsIgnoreCase(DataAtual)==false) {
            setPersistValue("tdata", new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
            resetComponents();
        //}

        //Exibe data atual na tela
        TextView tdata = findViewById(R.id.txtData);
        tdata.setText(getPersistValue("tdata"));


        //Atualiza o formuário de acordo com as regras para a visuaçização dos botoes
        updateViewComponetState();


    }

    //Limpa os valores armazenados na SharedPreferences possibilitando que o metodo resetComponents() retorne os componentes ao estado inicial
    private void resetComponents(){
        setPersistValue("Entrada", "");
        setPersistValue("InicioAlmoco", "");
        setPersistValue("FimAlmoco", "");
        setPersistValue("Saida", "");
        setPersistValue("dtRemFimAlmoco", "");
        setPersistValue("dtRemSaida", "");

    }

    //Atualiza o formuário de acordo com as regras para a visuaçização dos botoes
    private void updateViewComponetState(){

        findViewById(R.id.bntEntrada).setEnabled(false);
        findViewById(R.id.bntInicioAlmoco).setEnabled(false);
        findViewById(R.id.bntFimAlmoco).setEnabled(false);
        findViewById(R.id.bntSaida).setEnabled(false);


        //recupera os valores da SharedPreferences e exibe na tela
        TextView t;
        t = findViewById(R.id.txtEntrada);
        t.setText(getPersistValue("Entrada"));
        t = findViewById(R.id.txtInicioAlmoco);
        t.setText(getPersistValue("InicioAlmoco"));
        t = findViewById(R.id.txtFimAlmoco);
        t.setText(getPersistValue("FimAlmoco"));
        t = findViewById(R.id.txtSaida);
        t.setText(getPersistValue("Saida"));

        TextView txtLembrete = findViewById(R.id.txtLembrete);
        ImageView imgSino = findViewById(R.id.imgSino);
        imgSino.setVisibility(View.INVISIBLE);
        txtLembrete.setText("");



        //verifica quais botoes devem ser habilitados
        if (getPersistValue("Entrada")=="") {
            findViewById(R.id.bntEntrada).setEnabled(true);
        }
        else if (getPersistValue("InicioAlmoco")=="" &&  getPersistValue("Entrada")!="") {
            findViewById(R.id.bntInicioAlmoco).setEnabled(true);

        }
        else if (getPersistValue("FimAlmoco")=="" && getPersistValue("InicioAlmoco")!="" &&  getPersistValue("Entrada")!="") {
            findViewById(R.id.bntFimAlmoco).setEnabled(true);

            imgSino.setVisibility(View.VISIBLE);
            txtLembrete.setText("Lembrete fim de almoço programado: "+getPersistValue("dtRemFimAlmoco"));
        }
        else if (getPersistValue("Saida")=="" && getPersistValue("FimAlmoco")!="" && getPersistValue("InicioAlmoco")!="" &&  getPersistValue("Entrada")!="") {
            findViewById(R.id.bntSaida).setEnabled(true);

            imgSino.setVisibility(View.VISIBLE);
            txtLembrete.setText("Lembrete saida programado: "+getPersistValue("dtRemSaida"));
        }

    }




    //metodo invocado quando o inicio do ponto é registrado programando um lembrete para 58 minutos
    private void lembrarRetornoAlmoco(){
        try {


            AlarmManager alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, AlarmReceiver.class);
            //Intent intent = new Intent("ALARME_PONTO");

            intent.putExtra("chave","retornoAlmoco");
            PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(System.currentTimeMillis());
            c.add(Calendar.MINUTE, 58);
            //c.add(Calendar.SECOND, 5);
            alarmMgr.set(AlarmManager.RTC_WAKEUP,  c.getTimeInMillis(), alarmIntent);

            //armazena a data que sera lembrado
            setPersistValue("dtRemFimAlmoco", new SimpleDateFormat("HH:mm:ss").format(c.getTime()));

        }catch (Exception e){
           new NotificationDialog().send(this,3,"Time Erro","Não foi possível programar o lembrete de almoço");
        }

    }

    //metodo invocado quando o fim do almoço é registrado programando um lembrete para a saida.
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void lembrarSaida(){

        try {

            //recupera registro de alarme do sistema
            AlarmManager alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

            //referencia a classe a ser chamada quando o quando atingir o tempo da agenda. Deve ser registrada no AndroidManifest.xml
            Intent intent = new Intent(this, AlarmReceiver.class);

            //adição de parametro para identificar na classe AlarmReceiver.class qual mensagem deve ser enviada
            intent.putExtra("chave","saida");

            //istancia uma mensagem broacast para o sistema anfroid
            PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

            //obeem dada da entrada
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Calendar c = Calendar.getInstance();
            c.setTime(formato.parse(getPersistValue("dtEntradaTT")));

            //adiciona 8h e 58 min a data de entrada
            c.add(Calendar.MINUTE, 538);
            //c.add(Calendar.SECOND, 10);


            //programa o alarme para o tempo especificado
            alarmMgr.set(AlarmManager.RTC_WAKEUP,  c.getTimeInMillis(), alarmIntent);

            //armazena a data que sera lembrado
            setPersistValue("dtRemSaida", new SimpleDateFormat("HH:mm:ss").format(c.getTime()));

        }catch (Exception e){
            new NotificationDialog().send(this,3,"Time Erro","Não foi possível programar o lembrete de saida");
        }



    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void registrarEntrada(View v){
        dtEntrada = new Date();

        //armazena data de entrada na SharedPreferences para calcular a data de saida
        String currentTimeString = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(dtEntrada);
        setPersistValue("dtEntradaTT", currentTimeString);


        //armazena data de entrada na SharedPreferences para exibir no textView
        currentTimeString = new SimpleDateFormat("HH:mm:ss").format(dtEntrada);
        setPersistValue("Entrada", currentTimeString);

        //atualiza o estado dos botoes
        updateViewComponetState();



    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void registrarSaida(View v){

        dtSaida = new Date();
        String currentTimeString = new SimpleDateFormat("HH:mm:ss").format(dtSaida);

        setPersistValue("Saida", currentTimeString);
        updateViewComponetState();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void inicioAlmoco(View v){
        dtInicioAlmoco = new Date();

        String currentTimeString = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(dtInicioAlmoco);
        setPersistValue("dtInicioAlmocoTT", currentTimeString);

        currentTimeString = new SimpleDateFormat("HH:mm:ss").format(dtInicioAlmoco);
        setPersistValue("InicioAlmoco", currentTimeString);

        //invoca notificação para lembrar retorno do almoço
        this.lembrarRetornoAlmoco();
        updateViewComponetState();



    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void fimAlmoco(View v){
        dtFimAlmoco= new Date();
        String currentTimeString = new SimpleDateFormat("HH:mm:ss").format(dtFimAlmoco);
        setPersistValue("FimAlmoco", currentTimeString);

        //invoca notificação para lembrar a saida
        this.lembrarSaida();
        updateViewComponetState();


    }


    //graca tipos primitivos no sistema android em chave/valor possibilitando sua recuperação mesmo com o programa sendo encerrado
    private void setPersistValue(String pChave, String pValor){
        // Precisamos de um objeto Editor para fazer mudanças de preferência.
        // Todos os objetos são de android.context.Context
        SharedPreferences settings = getSharedPreferences("PREFS_NAME", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(pChave, pValor);
        editor.commit();
    }


    //obtem dados amarenados no sistema android
    private String getPersistValue(String pChave){
        SharedPreferences settings = getSharedPreferences("PREFS_NAME", 0);
        return settings.getString(pChave, "");

    }





}