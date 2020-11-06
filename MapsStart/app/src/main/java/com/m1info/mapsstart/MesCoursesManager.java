package com.m1info.mapsstart;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

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
        // Ajout d'un enregistrement dans la table
        ContentValues values = new ContentValues();
        values.put("nomMagasin", mesCourses.getNomMagasin());
        values.put("nomProduit", mesCourses.getNomProduit());

        // insert() retourne l'id du nouvel enregistrement inséré, ou -1 en cas d'erreur
        Log.d("TEEEEEEST", "Valeurs: \n" + values.toString());

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

    public MesCourses getElement(int id) {
        // Retourne la ligne dont l'id est passé en paramètre

        MesCourses mc=new MesCourses(0,"","");

        Cursor c = db.rawQuery("SELECT * FROM Courses WHERE "+KEY_ID_COURSES+"="+id, null);
        if (c.moveToFirst()) {
            mc.setIdCourses(c.getInt(c.getColumnIndex("idCourse")));
            mc.setNomMagasin(c.getString(c.getColumnIndex("nomMagasin")));
            mc.setNomProduit(c.getString(c.getColumnIndex("nomProduit")));
            c.close();
        }

        return mc;
    }

    public Cursor getAllListeCourses() {
        // sélection de tous les enregistrements de la table
        return db.rawQuery("SELECT * FROM Courses", null);
    }

}

