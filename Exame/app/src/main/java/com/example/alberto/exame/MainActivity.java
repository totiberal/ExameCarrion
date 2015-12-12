package com.example.alberto.exame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText etNumero, etNome;
    Button btnChamar, btnSalary, btnAudio;
    private SharedPreferences preferencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //preferencias();
        etNome=(EditText) findViewById(R.id.idNome);
        etNumero=(EditText) findViewById(R.id.idTelefono);
        btnChamar=(Button) findViewById(R.id.idChamar);
        btnAudio=(Button) findViewById(R.id.idAudio);
        btnSalary=(Button) findViewById(R.id.idSalary);
        meterPreferencias();
        btnChamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etNumero.getText().toString().equalsIgnoreCase("") || etNumero.getText() == null){
                    Toast.makeText(getBaseContext(),R.string.nonTelefono,Toast.LENGTH_SHORT).show();
                }else{
                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:(+34)"+etNumero.getText().toString())));
                }
            }
        });

        btnSalary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etNome.getText().toString().equalsIgnoreCase("") || etNome.getText() == null){
                    Toast.makeText(getBaseContext(),R.string.nonNome,Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent=new Intent(getApplicationContext(), ProcesaSalario.class);
                    intent.putExtra("nome",etNome.getText().toString());
                    startActivity(intent);
                }
            }
        });

        btnAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etNome.getText().toString().equalsIgnoreCase("") || etNome.getText() == null){
                    Toast.makeText(getBaseContext(),R.string.nonNome,Toast.LENGTH_SHORT).show();
                }else{
                    startActivity(new Intent(getApplicationContext(),Audio.class));
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.idVerde) {
            item.setChecked(true);
            SharedPreferences.Editor editor = preferencias.edit();
            editor.putString("cor","verde");
            editor.commit();
            meterPreferencias();
            return true;
        }

        if (id == R.id.idAzul) {
            item.setChecked(true);
            SharedPreferences.Editor editor = preferencias.edit();
            editor.putString("cor","azul");
            editor.commit();
            meterPreferencias();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void meterPreferencias(){
        preferencias=getSharedPreferences("nome",MODE_PRIVATE);
        String cor=preferencias.getString("cor","azul");
        if(cor.equalsIgnoreCase("azul")){
            etNome.setTextColor(getResources().getColor(R.color.azul));
        }else{
            etNome.setTextColor(getResources().getColor(R.color.verde));
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        meterPreferencias();
    }


}
