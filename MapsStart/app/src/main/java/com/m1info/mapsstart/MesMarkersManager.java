package com.m1info.mapsstart;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class MesMarkersManager {

    public static final String TABLE_NAME = "Marker";
    public static final String KEY_ID_MARKER="idMarker";
    public static final String KEY_ID_MAGASIN="idMagasin";
    public static final String KEY_NOM_MAGASIN="nomMagasin";
    public static final String KEY_LAT_MARKER="latMarker";
    public static final String KEY_LNG_MARKER="lngMarker";
    public static final String KEY_ADRESSE="adresse";
    public static final String CREATE_TABLE_MARKERS = "CREATE TABLE "+TABLE_NAME+
            " (" +
            " "+KEY_ID_MARKER +" INTEGER primary key," +
            " "+KEY_ID_MAGASIN + " TEXT," +
            " "+KEY_NOM_MAGASIN +" TEXT," +
            " "+KEY_LAT_MARKER +" TEXT," +
            " "+KEY_LNG_MARKER +" TEXT," +
            " "+KEY_ADRESSE +" TEXT" +
            ");";
    private MySQLite maBaseSQLite; // notre gestionnaire du fichier SQLite
    public SQLiteDatabase db;

    // Constructeur
    public MesMarkersManager(Context context)
    {
        maBaseSQLite = MySQLite.getInstance(context);
    }

    public void open()
    {
        db = maBaseSQLite.getWritableDatabase();

    }

    public void close()
    {
        db.close();
    }


    public long addElemMarker(MesMarkers mesMarkers) {
        // Tous les produits
        Cursor c = getAllListeMarkers();

        // Ajout d'un enregistrement dans la table
        ContentValues values = new ContentValues();
        values.put("idMagasin", mesMarkers.getIdMagasin());
        values.put("nomMagasin", mesMarkers.getNomMagasin());
        values.put("latMarker", mesMarkers.getLatMarker());
        values.put("lngMarker", mesMarkers.getLngMarker());
        values.put("adresse", mesMarkers.getAdresse());
        Log.d("places", values.get("latMarker").toString() + ", " + values.get("lngMarker").toString());

        // Parcours des produits de la table pour voir si il existe déjà ou si il est vide
        // Auquel cas on fais un toast et on ne l'ajoute pas
        if (c.moveToFirst()) {
            do {
                if (c.getString(c.getColumnIndex(MesMarkersManager.KEY_NOM_MAGASIN)).equals(values.get("nomMagasin").toString()) && c.getString(c.getColumnIndex(MesMarkersManager.KEY_LAT_MARKER)).equals(values.get("latMarker").toString()) && c.getString(c.getColumnIndex(MesMarkersManager.KEY_LNG_MARKER)).equals(values.get("lngMarker").toString())){
                    return -2;
                }
            }
            while (c.moveToNext());
        }
        // insert() retourne l'id du nouvel enregistrement inséré, ou -1 en cas d'erreur

        return db.insert("Marker",null,values);
    }


    public void supMarkerParMagasin(String magasin) {
        db.delete("Marker", "nomMagasin" + " = ?", new String[]{String.valueOf(magasin)});
    }

    public int suppTout(){
        return db.delete("Marker", null, null);
    }


    public Cursor getAllListeMarkers() {
        // sélection de tous les enregistrements de la table
        return db.rawQuery("SELECT idMagasin, nomMagasin, latMarker, lngMarker, adresse FROM Marker", null);
    }


    public Cursor getPositionWithName(String nomMagasin) {
        return db.rawQuery("SELECT latMarker, lngMarker FROM Marker WHERE nomMagasin = ?", new String[]{String.valueOf(nomMagasin)});
    }

    public boolean checkMarker(String magasin)
    {
        boolean res;
        res = false;
        Cursor c = db.rawQuery("SELECT * FROM Marker WHERE nomMagasin = ?", new String[]{String.valueOf(magasin)});
        int i = 0;
        if (c.moveToFirst()) {
            do {
                i++;
            } while (c.moveToNext());
        }
        c.close();

        if( i != 0)
        {
            Log.d("places", db.rawQuery("SELECT * FROM Marker WHERE nomMagasin = ?", new String[]{String.valueOf(magasin)}).toString());
            res = true;
        }
        return res;
    }


}

