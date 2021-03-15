package com.example.NotForget.business;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.NotForget.domain.AlarmReceiver;
import com.example.NotForget.domain.NotificationDialog;
import com.example.NotForget.persistence.*;

import androidx.annotation.RequiresApi;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Lembrar {
    private Armazenamento arm = new Armazenamento();
    private Context context;

    public Lembrar(Context pcontext){
        context = pcontext;
    }


    public void verificarSeDiaRegistrado() {
        String DataAtual = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        if(!arm.getPersistValue("tdata",this.context).equalsIgnoreCase(DataAtual)) {
            arm.setPersistValue("tdata",new SimpleDateFormat("dd/MM/yyyy").format(new Date()),this.context);
            resetArmazenamento();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void registrarEntrada(Date pData){
        Date dt;

        if (pData==null)
            dt= new Date();
        else
            dt=pData;

        String currentTimeString;

        //armazena data de entrada na SharedPreferences para calcular a data de saida
        currentTimeString = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(dt);
        arm.setPersistValue("dtEntradaTT", currentTimeString,context);

        //armazena data de entrada na SharedPreferences para exibir no textView
        currentTimeString = new SimpleDateFormat("HH:mm:ss").format(dt);
        arm.setPersistValue("Entrada", currentTimeString,context);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void registrarInicioAlmoco(Date pData){

        Date dt;
        if (pData==null)
            dt= new Date();
        else
            dt=pData;

        String currentTimeString;

        currentTimeString = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(dt);
        arm.setPersistValue("dtInicioAlmocoTT", currentTimeString,this.context);

        currentTimeString = new SimpleDateFormat("HH:mm:ss").format(dt);
        arm.setPersistValue("InicioAlmoco", currentTimeString,this.context);

        //invoca notificação para lembrar retorno do almoço
        this.lembrarRetornoAlmoco();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void registrarFimAlmoco(Date pData){
        Date dt;
        if (pData==null)
            dt= new Date();
        else
            dt=pData;

        String currentTimeString = new SimpleDateFormat("HH:mm:ss").format(dt);
        arm.setPersistValue("FimAlmoco", currentTimeString,this.context);

        //invoca notificação para lembrar a saida
        this.cancelarAlarme("retornoAlmoco");
        this.lembrarSaida();
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    public void registrarSaida(Date pData){
        Date dt;
        if (pData==null)
            dt= new Date();
        else
            dt=pData;

        String currentTimeString = new SimpleDateFormat("HH:mm:ss").format(dt);
        arm.setPersistValue("Saida", currentTimeString,this.context);

    }

    //NÃO IMPLEMENTADO
    public void cancelarAlarme(String tipo){

        if (tipo.equals("retornoAlmoco")) {
            Intent intent = new Intent(this.context, AlarmReceiver.class);
            intent.putExtra("chave", "retornoAlmoco");
            PendingIntent alarmIntent = PendingIntent.getBroadcast(this.context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager alarmMgr = (AlarmManager) this.context.getSystemService(Context.ALARM_SERVICE);
            alarmMgr.cancel(alarmIntent);
        }
        if (tipo.equals("saida")) {
            Intent intent = new Intent(this.context, AlarmReceiver.class);
            intent.putExtra("chave", "saida");
            PendingIntent alarmIntent = PendingIntent.getBroadcast(this.context, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager alarmMgr = (AlarmManager) this.context.getSystemService(Context.ALARM_SERVICE);
            alarmMgr.cancel(alarmIntent);
        }

    }


    //metodo invocado quando o inicio do ponto é registrado programando um lembrete para 58 minutos
    private void lembrarRetornoAlmoco(){
        try {
            //cancelar o alarme atual antes de criar um novo
            this.cancelarAlarme("retornoAlmoco");

            AlarmManager alarmMgr = (AlarmManager) this.context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this.context, AlarmReceiver.class);
            //Intent intent = new Intent("ALARME_PONTO");

            intent.putExtra("chave","retornoAlmoco");
            PendingIntent alarmIntent = PendingIntent.getBroadcast(this.context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);



            //obeem dada da entrada
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Calendar c = Calendar.getInstance();
            c.setTime(formato.parse(arm.getPersistValue("dtInicioAlmocoTT", this.context)));

            c.add(Calendar.MINUTE, 58);
            //c.add(Calendar.SECOND, 10);
            alarmMgr.set(AlarmManager.RTC_WAKEUP,  c.getTimeInMillis(), alarmIntent); //dtInicioAlmocoTT

            //armazena a data que sera lembrado
            arm.setPersistValue("dtRemFimAlmoco", new SimpleDateFormat("HH:mm:ss").format(c.getTime()),this.context);

        }catch (Exception e){
            new NotificationDialog().send(this.context,3,"Time Erro","Não foi possível programar o lembrete de almoço");
        }

    }

    //metodo invocado quando o fim do almoço é registrado programando um lembrete para a saida.
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void lembrarSaida() {

        try {
            //cancelar o alarme atual antes de criar um novo
            this.cancelarAlarme("saida");

            //recupera registro de alarme do sistema
            AlarmManager alarmMgr = (AlarmManager) this.context.getSystemService(Context.ALARM_SERVICE);

            //referencia a classe a ser chamada quando o quando atingir o tempo da agenda. Deve ser registrada no AndroidManifest.xml
            Intent intent = new Intent(this.context, AlarmReceiver.class);

            //adição de parametro para identificar na classe AlarmReceiver.class qual mensagem deve ser enviada
            intent.putExtra("chave", "saida");

            //istancia uma mensagem broacast para o sistema anfroid
            PendingIntent alarmIntent = PendingIntent.getBroadcast(this.context, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);

            //obeem dada da entrada
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Calendar c = Calendar.getInstance();
            c.setTime(formato.parse(arm.getPersistValue("dtEntradaTT", this.context)));

            //adiciona 8h e 58 min a data de entrada
            c.add(Calendar.MINUTE, 538);
            //c.add(Calendar.SECOND, 20);


            //programa o alarme para o tempo especificado
            alarmMgr.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), alarmIntent);

            //armazena a data que sera lembrado
            arm.setPersistValue("dtRemSaida", new SimpleDateFormat("HH:mm:ss").format(c.getTime()), this.context);

        } catch (Exception e) {
            new NotificationDialog().send(this.context, 3, "Time Erro", "Não foi possível programar o lembrete de saida");
        }
    }

    public void resetArmazenamento(){
        arm.setPersistValue("Entrada", "",this.context);
        arm.setPersistValue("InicioAlmoco", "",this.context);
        arm.setPersistValue("FimAlmoco", "",this.context);
        arm.setPersistValue("Saida", "",this.context);
        arm.setPersistValue("dtRemFimAlmoco", "",this.context);
        arm.setPersistValue("dtRemSaida", "",this.context);
        this.cancelarAlarme("retornoAlmoco");
        this.cancelarAlarme("saida");

    }

    public String obterValorArmazenamento(String chave){
        return arm.getPersistValue(chave,this.context);
    }

    public void gravarValorArmazenamento(String chave,String valor){
        arm.setPersistValue(chave,valor,this.context);
    }



    public String verificarPontoRegistrado(){

        String strEntrada=arm.getPersistValue("Entrada",this.context);
        String strInicioAlmoco=arm.getPersistValue("InicioAlmoco",this.context);
        String strFimAlmoco=arm.getPersistValue("FimAlmoco",this.context);
        String strSaida=arm.getPersistValue("Saida",this.context);


        if (strEntrada=="") {
            //this.viewHolder.bntEntrada.setEnabled(true);
            return "Entrada";
        }
        else if (strInicioAlmoco == "" &&  strEntrada != "") {
            //this.viewHolder.bntInicioAlmoco.setEnabled(true);
            return "InicioAlmoco";

        }
        else if (strFimAlmoco =="" && strInicioAlmoco != "" &&  strEntrada!="") {
            //this.viewHolder.bntFimAlmoco.setEnabled(true);
            //this.viewHolder.imgSino.setVisibility(View.VISIBLE);
            //this.viewHolder.txtLembrete.setText("Lembrete fim de almoço programado: "+ arm.getPersistValue("dtRemFimAlmoco",this.context));
            return "FimAlmoco";
        }
        else if (strSaida == "" && strFimAlmoco !="" && strInicioAlmoco !="" &&  strEntrada!="") {
            //this.viewHolder.bntSaida.setEnabled(true);
            //this.viewHolder.imgSino.setVisibility(View.VISIBLE);
            //this.viewHolder.txtLembrete.setText("Lembrete saida programado: "+ arm.getPersistValue("dtRemSaida",this.context));
            return  "Saida";
        } else{
            return  "err";
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void alterarDatas(String pEntrada, String pInicio, String pFim) throws Exception {
        try {
            if (
                    (pEntrada.isEmpty() && pInicio.isEmpty() && pFim.isEmpty())
                            || (!pEntrada.isEmpty() && pInicio.isEmpty() && pFim.isEmpty())
                            || (!pEntrada.isEmpty() && !pInicio.isEmpty() && pFim.isEmpty())
                            || (!pEntrada.isEmpty() && !pInicio.isEmpty() && !pFim.isEmpty())
            ) {


                String DataAtual = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                Date date;

                if (!pEntrada.isEmpty()) {
                    date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(DataAtual + " " + pEntrada);
                    this.registrarEntrada(date);
                } else {
                    arm.setPersistValue("Entrada", "", this.context);
                }

                if (!pInicio.isEmpty()) {
                    date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(DataAtual + " " + pInicio);
                    this.registrarInicioAlmoco(date);
                } else {
                    arm.setPersistValue("InicioAlmoco", "", this.context);
                    this.cancelarAlarme("retornoAlmoco");
                }

                if (!pFim.isEmpty()) {
                    date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(DataAtual + " " + pFim);
                    this.registrarFimAlmoco(date);
                } else {
                    arm.setPersistValue("FimAlmoco", "", this.context);
                    this.cancelarAlarme("saida");
                }


            } else {
                throw new Exception("Dadas não informadas corretamente");
            }
        } catch (Exception e) {
            throw e;
            //Log.i("NotForget-data", "erro: Lembrar.alterarDatas() -> " + e.getMessage());
        }

    }


}
