package com.example.salva.bomba;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class BDPreguntas extends SQLiteOpenHelper {

    public BDPreguntas(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Preguntas(idPreg integer not null unique primary key, Pregunta VARCHAR(200))");
        db.execSQL("CREATE TABLE RespuestasV(idRV integer,Respuesta VARCHAR(50),FOREIGN KEY(idRV) REFERENCES Preguntas(idPreg))");
        db.execSQL("CREATE TABLE RespuestasF(idRF integer,Respuesta VARCHAR(50),FOREIGN KEY(idRF) REFERENCES Preguntas(idPreg))");
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Preguntas");
        db.execSQL("DROP TABLE IF EXISTS RespuestasV");
        db.execSQL("DROP TABLE IF EXISTS RespuestasF");
        onCreate(db);
    }

}
