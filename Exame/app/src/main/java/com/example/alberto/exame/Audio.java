package com.example.alberto.exame;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;

public class Audio extends AppCompatActivity {

    File file;
    Spinner spinner;
    static int numero;
    Button btnReproducir, btnParar, btnGravar;
    private MediaPlayer mediaplayer;
    private boolean pause;
    String[] nomes;
    String arquivoGravar;
    private MediaRecorder mediaRecorder;
    ArrayAdapter<String> adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        mediaplayer = new MediaPlayer();
        mediaRecorder = new MediaRecorder();
        btnGravar=(Button) findViewById(R.id.idGravar);
        pause = false;
        btnReproducir=(Button) findViewById(R.id.idReproducir);
        btnParar=(Button) findViewById(R.id.idParar);
        file=new File(Environment.getExternalStorageDirectory()+"/AUDIO/");
        if(!file.exists()) file.mkdirs();
        spinner=(Spinner) findViewById(R.id.idSpinner);
        nomes=file.list();
         adaptador= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, nomes);
        spinner.setAdapter(adaptador);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                numero = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnReproducir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String ruta = Environment.getExternalStorageDirectory() + "/AUDIO/" + nomes[numero];
                    mediaplayer.reset();

                    mediaplayer.setDataSource(ruta);
                    mediaplayer.prepare();
                    mediaplayer.start();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.e("MULTIMEDIA", e.getMessage());
                }

            }
        });

        btnParar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaplayer.isPlaying())
                    mediaplayer.stop();
                pause=false;
            }
        });

        btnGravar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timeStamp = DateFormat.getDateTimeInstance().format(
                        new Date()).replaceAll(":", "").replaceAll("/", "_")
                        .replaceAll(" ", "_");

                mediaRecorder = new MediaRecorder();
                arquivoGravar = Environment.getExternalStorageDirectory()+"/AUDIO/" + timeStamp + ".3gp";
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mediaRecorder.setMaxDuration(10000);
                mediaRecorder.setAudioEncodingBitRate(32768);
                mediaRecorder.setAudioSamplingRate(8000); // No emulador só 8000 coma
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                mediaRecorder.setOutputFile(arquivoGravar);
                try {
                    mediaRecorder.prepare();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    mediaRecorder.reset();
                }
                mediaRecorder.start();
                abrirDialogo("GRAVAR");
                adaptador.notifyDataSetChanged();

            }
        });

    }

    private void abrirDialogo(String tipo) {
        if (tipo == "GRAVAR") {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                    .setMessage("GRAVANDO").setPositiveButton(
                            "PREME PARA PARAR",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // TODO Auto-generated method stub
                                    mediaRecorder.stop();
                                    mediaRecorder.release();
                                    mediaRecorder = null;
                                }
                            });
            dialog.show();
        }
    }

        @Override
    protected void onPause() {
        super.onPause();

        if (mediaplayer.isPlaying()){
            mediaplayer.pause();
            pause = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (pause) {
            mediaplayer.start();
            pause = false;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle estado) {
        estado.putBoolean("MEDIAPLAYER_PAUSE", pause);
        super.onSaveInstanceState(estado);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("MEDIAPLAYER_PAUSE", false);
        pause = savedInstanceState.getBoolean("MEDIAPLAYER_PAUSE");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mediaplayer.isPlaying()) mediaplayer.stop();

        if (mediaplayer != null) mediaplayer.release();
        mediaplayer = null;

    }
}
