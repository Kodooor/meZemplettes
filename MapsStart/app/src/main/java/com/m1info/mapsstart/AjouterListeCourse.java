package com.m1info.mapsstart;

import android.app.Activity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class AjouterListeCourse extends Activity {

    ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_course);

        listview = findViewById(R.id.elemCourse);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(AjouterListeCourse.this, android.R.layout.simple_list_item_1);
        listview.setAdapter(adapter);
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View v, int position, long id) {

                new AlertDialog.Builder(AjouterListeCourse.this)
                        .setTitle("Alerte")
                        .setMessage("Voulez vous supprimer cet élément de votre liste de course ?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String item = (String) listview.getItemAtPosition(position);
                                MesCoursesManager mcm = new MesCoursesManager(AjouterListeCourse.this);
                                mcm.open();
                                mcm.supElemCourseParProduit(item);
                                lireElements(v);                            }
                            })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                return true;
            }
        });

    }

    public void ajouterElement(View v) {
        EditText nomCommerce = (EditText) findViewById(R.id.nomCommerce);
        EditText produitCommerce = (EditText) findViewById(R.id.produitCommerce);

        MesCoursesManager mcm = new MesCoursesManager(this);
        mcm.open();
        mcm.addElemCourse(new MesCourses(0, nomCommerce.getText().toString(), produitCommerce.getText().toString()));
        lireElements(v);
        mcm.close();

    }

    public void supprimerTout(View v){
        MesCoursesManager mcm = new MesCoursesManager(this);
        mcm.open();
        mcm.suppTout();
        lireElements(v);
        mcm.close();
    }

    public void lireElements(View v) {
        listview = findViewById(R.id.elemCourse);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(AjouterListeCourse.this, android.R.layout.simple_list_item_1);
        listview.setAdapter(adapter);

        // Listing des enregistrements de la table
        MesCoursesManager mcm = new MesCoursesManager(this);
        mcm.open();
        Cursor c = mcm.getAllListeCourses();
        if (c.moveToFirst()) {
            do {
                adapter.add(c.getString(c.getColumnIndex(MesCoursesManager.KEY_NOM_PRODUIT)));
            }
            while (c.moveToNext());
        }
        adapter.notifyDataSetChanged();
        c.close(); // fermeture du curseur
    }

}