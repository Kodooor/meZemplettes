package com.m1info.mapsstart;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

// Fonction qui gère le fichier / la création de la base de données
public class MySQLite extends SQLiteOpenHelper {

    // Nom de la base de données
    private static final String DATABASE_NAME = "db.sqlite";
    // Database version
    private static final int DATABASE_VERSION = 2;
    // Instance de mySQLite
    private static MySQLite sInstance;

    // Fonction qui retourne l'instance de MySQLite
    public static synchronized MySQLite getInstance(Context context) {
        if (sInstance == null) { sInstance = new MySQLite(context); }
        return sInstance;
    }

    // Constructeur
    public MySQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    // On crée les deux tables
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Création de la base de données
        // on exécute ici les requêtes de création des tables
        sqLiteDatabase.execSQL(MesCoursesManager.CREATE_TABLE_COURSES);
        sqLiteDatabase.execSQL(MesMarkersManager.CREATE_TABLE_MARKERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        // Mise à jour de la base de données
        // méthode appelée sur incrémentation de DATABASE_VERSION
        // on peut faire ce qu'on veut ici, comme recréer la base :
        onCreate(sqLiteDatabase);
    }
    

}
