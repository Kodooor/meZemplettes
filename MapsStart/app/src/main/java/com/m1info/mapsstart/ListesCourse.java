package com.m1info.mapsstart;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ListesCourse extends Activity {

    ListView listesCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_course);

        MesCoursesManager mcm = new MesCoursesManager(this);
        listesCourse = findViewById(R.id.listesCourse);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(ListesCourse.this, android.R.layout.simple_list_item_1);
        listesCourse.setAdapter(adapter);
        listesCourse.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
                String item = (String) listesCourse.getItemAtPosition(position);
                Intent intent = new Intent(ListesCourse.this, AjouterListeCourse.class);
                intent.putExtra("storeName", item);
                startActivityForResult(intent, 0);
            }
        });

        afficherLesListesCourses();

    };

    public void afficherLesListesCourses() {

        listesCourse = findViewById(R.id.listesCourse);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(ListesCourse.this, android.R.layout.simple_list_item_1);
        listesCourse.setAdapter(adapter);
        // Listing des enregistrements de la table
        MesCoursesManager mcm = new MesCoursesManager(this);
        mcm.open();
        Cursor c = mcm.getMagasinListeCourses();
        if (c.moveToFirst()) {
            do {
                adapter.add(c.getString(c.getColumnIndex(MesCoursesManager.KEY_NOM_MAGASIN)));
            }
            while (c.moveToNext());
        }
        adapter.notifyDataSetChanged();
        c.close(); // fermeture du curseur
    }


}
