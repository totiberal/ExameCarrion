package com.example.alberto.exame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ProcesaSalario extends Activity {

    private BaseDatos baseDatos;
    public static enum TIPOREDE{MOBIL,ETHERNET,WIFI,SENREDE};
    private TIPOREDE conexion;
    Button btnSalario, btnVer, btnGravar;
    TextView texto;
    private final String DESCARGAR="http://manuais.iessanclemente.net/images/5/53/Salaries.xml";
    private File rutaArquivo;
    static String nome;
    File ruta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_procesa_salario);

        Intent intent=getIntent();
        nome=intent.getExtras().getString("nome");

        btnGravar=(Button) findViewById(R.id.idGravar);
        btnSalario=(Button) findViewById(R.id.idProcesar);
        texto=(TextView) findViewById(R.id.idTv);
        btnVer=(Button) findViewById(R.id.idLer);

        btnSalario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                texto.setText("");
                conexion = comprobarRede();
                if (conexion == TIPOREDE.SENREDE) {
                    Toast.makeText(getApplicationContext(), "NON SE PODE FACER ESTA PRACTICA SEN CONEXION A INTERNET", Toast.LENGTH_LONG).show();
                    finish();
                }
                Thread fio = new Thread() {
                    @Override
                    public void run() {
                        descargarArquivo();
                    }
                };
                fio.start();
                while (fio.isAlive()) Log.i("FIO", "DESCARGANDO...");
                Toast.makeText(getApplicationContext(), "Acabei de descargar", Toast.LENGTH_LONG).show();
                procesarXML();
            }
        });

        btnVer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                encherTextView();
            }
        });

        btnGravar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gravarFicheiro();
            }
        });
    }

    public void gravarFicheiro(){
        try{
            File ficheiro=new File(Environment.getExternalStorageDirectory()+"/"+nome+".txt");
            OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(ficheiro));
            ArrayList<Salario> arrayL=baseDatos.selecionar();
            for(Salario s: arrayL){
                Toast.makeText(getApplicationContext(),s.getSalario()+"",Toast.LENGTH_SHORT).show();
                osw.write(s.toString()+"\n");
            }
            osw.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void encherTextView(){
        ArrayList<Salario> arrayL=baseDatos.selecionar();
        texto.setText("");
        texto.append("Total Salary   Month \n");
        for(Salario s: arrayL){
            texto.append(s.getSalario()+"         "+s.getMes()+"\n");
            Toast.makeText(getApplicationContext(),s.getSalario()+"",Toast.LENGTH_SHORT).show();
        }
    }

    public void procesarXML(){
        try{
            InputStream is=new FileInputStream(rutaArquivo);
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(is, "UTF-8");

            int evento = parser.nextTag();
            Salario s = null;
            while(evento != XmlPullParser.END_DOCUMENT) {
                if(evento == XmlPullParser.START_TAG) {
                    if (parser.getName().equals("salary")) {
                        s = new Salario();
                        evento = parser.nextTag();
                        s.setMes(parser.nextText());
                        evento = parser.nextTag();
                        s.setSalario(Double.parseDouble(parser.nextText()));
                        evento = parser.nextTag();
                        s.setSalario(s.getSalario() + Double.parseDouble(parser.nextText()));
                        evento = parser.nextTag();
                        s.setSalario(s.getSalario() + Double.parseDouble(parser.nextText()));
                    }
                }
                if(evento == XmlPullParser.END_TAG) {
                    if (parser.getName().equals("salary")) {
                        try{
                            baseDatos.engadir(s);
                            Log.i("Engadido", s.toString());
                        }catch (Exception ex){

                        }

                    }
                }

                evento = parser.next();
            }

            is.close();



        }catch(Exception ex){
            Log.e("ERROR", "Estou en procesar xml");
        }
    }

    private void descargarArquivo() {
        URL url = null;
        try {
            url = new URL(DESCARGAR);
        } catch (MalformedURLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return;
        }

        HttpURLConnection conn = null;
        String nomeArquivo = Uri.parse(DESCARGAR).getLastPathSegment();
        rutaArquivo = new File(Environment.getExternalStorageDirectory(),"/SALARIO/"+nomeArquivo);
        ruta=new File(Environment.getExternalStorageDirectory(),"/SALARIO/");
        if(!ruta.exists())ruta.mkdirs();
        try {

            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);/* milliseconds */
            conn.setConnectTimeout(15000);/* milliseconds */
            conn.setRequestMethod("POST");
            conn.setDoInput(true);/* Indicamos que a conexión vai recibir datos */

            conn.connect();

            int response = conn.getResponseCode();
            if (response != HttpURLConnection.HTTP_OK) {
                return;
            }
            OutputStream os = new FileOutputStream(rutaArquivo);
            InputStream in = conn.getInputStream();
            byte data[] = new byte[1024];// Buffer a utilizar
            int count;
            while ((count = in.read(data)) != -1) {
                os.write(data, 0, count);
            }
            os.flush();
            os.close();
            in.close();
            conn.disconnect();
            Log.i("COMUNICACION", "ACABO");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            Log.e("COMUNICACION", e.getMessage());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("COMUNICACION", e.getMessage());
        }

    }

    private TIPOREDE comprobarRede(){
        NetworkInfo networkInfo=null;

        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            switch(networkInfo.getType()){
                case ConnectivityManager.TYPE_MOBILE:
                    return TIPOREDE.MOBIL;
                case ConnectivityManager.TYPE_ETHERNET:
                    return TIPOREDE.ETHERNET;
                case ConnectivityManager.TYPE_WIFI:
                    return TIPOREDE.WIFI;
            }
        }
        return TIPOREDE.SENREDE;
    }

    @Override
    public void onStart(){
        super.onStart();
        if(baseDatos==null){
            baseDatos=new BaseDatos(this);
            baseDatos.sqlLiteDB=baseDatos.getWritableDatabase();
        }
    }

}
