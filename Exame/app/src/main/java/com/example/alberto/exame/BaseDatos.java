package com.example.alberto.exame;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by alberto on 11/12/15.
 */
public class BaseDatos extends SQLiteOpenHelper {

    public SQLiteDatabase sqlLiteDB;
    public final static int version=1;
    public final static String NOME_BD="DATOS";
    private String CREAR_TABOA_DATOS ="CREATE TABLE DATOS ( " + "mes VARCHAR( 50 )  PRIMARY KEY,"+" pasta REAL NOT NULL)";

    public BaseDatos(Context context) {
        super(context, NOME_BD, null, version);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREAR_TABOA_DATOS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS DATOS");
        onCreate(db);
    }

    public void engadir(Salario s){
        double salario=s.getSalario();
        String mes=s.getMes();
        sqlLiteDB.execSQL("INSERT INTO DATOS (mes,pasta) VALUES ('"+s.getMes()+"','"+s.getSalario()+"')");
    }

    public ArrayList<Salario> selecionar(){
        ArrayList<Salario> devolver = new ArrayList<>();

        Cursor datosConsulta = sqlLiteDB.rawQuery("Select mes, pasta from DATOS", null);
        if (datosConsulta.moveToFirst()) {
            Salario s;
            while (!datosConsulta.isAfterLast()) {
                s = new Salario(datosConsulta.getString(0),
                        datosConsulta.getDouble(1));
                devolver.add(s);
                datosConsulta.moveToNext();
            }
        }
        return devolver;
    }





}
