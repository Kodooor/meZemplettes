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

// Classe regroupant toutes les fonctions intéragissant avec la base de données
public class MesCoursesManager {

    // Le nom de notre table de données
    public static final String TABLE_NAME = "Courses";

    // Nos colonnes
    public static final String KEY_ID_COURSES="idCourse";
    public static final String KEY_NOM_MAGASIN="nomMagasin";
    public static final String KEY_NOM_PRODUIT="nomProduit";
    public static final String KEY_NOM_RAYON="rayon";

    // La création de notre table avec nos colonnes
    public static final String CREATE_TABLE_COURSES = "CREATE TABLE "+TABLE_NAME+
            " (" +
            " "+KEY_ID_COURSES +" INTEGER primary key," +
            " "+KEY_NOM_MAGASIN +" TEXT," +
            " "+KEY_NOM_PRODUIT +" TEXT," +
            " "+KEY_NOM_RAYON +" TEXT" +
            ");";
    private MySQLite maBaseSQLite; // notre gestionnaire du fichier SQLite

    // Notre variable permettant d'intéragir avec la base de données
    public SQLiteDatabase db;

    // Constructeur
    public MesCoursesManager(Context context)
    {
        maBaseSQLite = MySQLite.getInstance(context);
    }

    // Fonction d'ouverture de la base de données
    public void open()
    {
        db = maBaseSQLite.getWritableDatabase();

    }

    // Fonction de fermeture de la base de données
    public void close()
    {
        db.close();
    }


    // Fonction permettant d'ajouter un élément à notre base de données
    public long addElemCourse(MesCourses mesCourses) {
        // Tous les produits
        Cursor c = getAllListeCourses();

        // Ajout d'un enregistrement dans la table
        ContentValues values = new ContentValues();
        values.put("nomMagasin", mesCourses.getNomMagasin());
        values.put("nomProduit", mesCourses.getNomProduit());
        values.put("rayon", mesCourses.getRayon());
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
                if (values.get("rayon").toString().equals("")) {
                    return -4;
                }
            }
            while (c.moveToNext());
        }

        // insert() retourne l'id du nouvel enregistrement inséré, ou -1 en cas d'erreur
        return db.insert("Courses",null,values);
    }

    // Fonction qui supprime un produit dans une liste de course
    public void supElemCourseParProduit(String produit) {
        db.delete("Courses", "nomProduit" + " = ?", new String[]{String.valueOf(produit)});
    }

    // Fonction qui retourne les éléments d'une liste de course en fonction du magasin passé en paramètre
    public ArrayList<String> getListePourMagasin(String nomMagasin) {
        // Retourne la ligne dont l'id est passé en paramètre

        ArrayList<String> listePourMagasin= new ArrayList<>();

        Cursor c = db.rawQuery("SELECT * FROM Courses", null);
        if (c.moveToFirst()) {
            do {
                if(c.getString(c.getColumnIndex("nomMagasin")).equals(nomMagasin)){
                    listePourMagasin.add(c.getString(c.getColumnIndex("nomProduit")));
                }
            } while (c.moveToNext());
        }
        c.close();
        return listePourMagasin;
    }

    // Fonction qui retourne les éléments d'une liste de course en fonction du magasin et du filtre appliqué passé en paramètre
    public ArrayList<String> getListePourMagasinFiltre(String nomMagasin, String filtre) {
        // Retourne la ligne dont l'id est passé en paramètre

        ArrayList<String> listePourMagasin= new ArrayList<>();

        Cursor c = db.rawQuery("SELECT * FROM Courses WHERE rayon = ? ", new String[] {String.valueOf(filtre)});
        if (c.moveToFirst()) {
            do {
                if(c.getString(c.getColumnIndex("nomMagasin")).equals(nomMagasin)){
                    listePourMagasin.add(c.getString(c.getColumnIndex("nomProduit")));
                }
            } while (c.moveToNext());
        }
        c.close();
        return listePourMagasin;
    }

    // Fonction qui retourne tous les magasins pour lesquels une liste de course est crée
    public Cursor getMagasinListeCourses() {
        // sélection de tous les enregistrements de la table
        return db.rawQuery("SELECT DISTINCT(nomMagasin) FROM Courses", null);
    }

    // Fonction qui retourne toutes les lignes et toutes les colonnes de la base de données
    public Cursor getAllListeCourses() {
        // sélection de tous les enregistrements de la table
        return db.rawQuery("SELECT nomMagasin, nomProduit, rayon FROM Courses", null);
    }

    // Fonction qui supprime une liste de course par rapport au magasin
    public void supprimerListeCourse(String nomMag){
        db.delete("Courses", "nomMagasin" + " = ?", new String[]{String.valueOf(nomMag)});
    }
}

