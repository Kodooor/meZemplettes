package com.m1info.mapsstart;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class MesCoursesManager {

    public static final String TABLE_NAME = "Courses";
    public static final String KEY_ID_COURSES="idCourse";
    public static final String KEY_NOM_MAGASIN="nomMagasin";
    public static final String KEY_NOM_PRODUIT="nomProduit";
    public static final String CREATE_TABLE_COURSES = "CREATE TABLE "+TABLE_NAME+
            " (" +
            " "+KEY_ID_COURSES +" INTEGER primary key," +
            " "+KEY_NOM_MAGASIN +" TEXT," +
            " "+KEY_NOM_PRODUIT +" TEXT" +
            ");";
    private MySQLite maBaseSQLite; // notre gestionnaire du fichier SQLite
    public SQLiteDatabase db;

    // Constructeur
    public MesCoursesManager(Context context)
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


    public long addElemCourse(MesCourses mesCourses) {
        // Tous les produits
        Cursor c = getAllListeCourses();

        // Ajout d'un enregistrement dans la table
        ContentValues values = new ContentValues();
        values.put("nomMagasin", mesCourses.getNomMagasin());
        values.put("nomProduit", mesCourses.getNomProduit());

        // Parcours des produits de la table pour voir si il existe déjà ou si il est vide
        // Auquel cas on fais un toast et on ne l'ajoute pas
        if (c.moveToFirst()) {
            do {
                if (c.getString(c.getColumnIndex(MesCoursesManager.KEY_NOM_PRODUIT)).equals(values.get("nomProduit").toString()) && c.getString(c.getColumnIndex(MesCoursesManager.KEY_NOM_MAGASIN)).equals(values.get("nomMagasin"))){
                    return -2;
                }
                if (values.get("nomProduit").toString().equals("")) {
                    return -3;
                }
            }
            while (c.moveToNext());
        }
        // insert() retourne l'id du nouvel enregistrement inséré, ou -1 en cas d'erreur

        return db.insert("Courses",null,values);
    }

    public int modElemCourse(MesCourses mesCourses) {
        // modification d'un enregistrement
        // valeur de retour : (int) nombre de lignes affectées par la requête

        ContentValues values = new ContentValues();
        values.put("nomMagasin", mesCourses.getNomMagasin());
        values.put("nomProduit", mesCourses.getNomProduit());

        String where = "idCourse"+" = ?";
        String[] whereArgs = {mesCourses.getIdCourses()+""};

        return db.update("Courses", values, where, whereArgs);
    }

    public void supElemCourseParProduit(String produit) {
        db.delete("Courses", "nomProduit" + " = ?", new String[]{String.valueOf(produit)});
    }

    public int suppTout(){
        return db.delete("Courses", null, null);
    }

    public ArrayList<String> getListePourMagasin(String nomMagasin) {
        // Retourne la ligne dont l'id est passé en paramètre

        ArrayList<String> listePourMagasin = new ArrayList<>();

        Cursor c = db.rawQuery("SELECT * FROM Courses", null);
        if (c.moveToFirst()) {
            do {
                if(c.getString(c.getColumnIndex("nomMagasin")).equals(nomMagasin)) {
                    listePourMagasin.add(c.getString(c.getColumnIndex("nomProduit")));
                }
            } while (c.moveToNext());

        }
        c.close();
        return listePourMagasin;
    }

    public Cursor getMagasinListeCourses() {
        // sélection de tous les enregistrements de la table
        return db.rawQuery("SELECT DISTINCT(nomMagasin) FROM Courses", null);
    }

    public Cursor getAllListeCourses() {
        // sélection de tous les enregistrements de la table
        return db.rawQuery("SELECT nomMagasin, nomProduit FROM Courses", null);
    }

    public void supprimerListeCourse(String nomMag){
        db.delete("Courses", "nomMagasin" + " = ?", new String[]{String.valueOf(nomMag)});
    }
}

