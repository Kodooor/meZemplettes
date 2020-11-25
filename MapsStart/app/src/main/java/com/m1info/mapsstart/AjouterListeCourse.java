package com.m1info.mapsstart;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.util.ArrayList;

public class AjouterListeCourse extends Activity {

    ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajouter_liste_course);

        Intent intent = getIntent();
        String storeName = intent.getStringExtra("storeName");
        EditText nomCommerce = (EditText) findViewById(R.id.nomCommerce);
        nomCommerce.setText(storeName);

        listview = findViewById(R.id.elemCourse);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(AjouterListeCourse.this, android.R.layout.simple_list_item_1);
        listview.setAdapter(adapter);
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View v, int position, long id) {

                new AlertDialog.Builder(AjouterListeCourse.this)
                        .setTitle(getText(R.string.Alerte))
                        .setMessage(getText(R.string.SuppItemListCourse))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String item = (String) listview.getItemAtPosition(position);
                                MesCoursesManager mcm = new MesCoursesManager(AjouterListeCourse.this);
                                mcm.open();
                                mcm.supElemCourseParProduit(item);
                                lireElements();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                return true;
            }
        });
        lireElements();
    }

    public void ajouterElement(View v) {
        EditText nomCommerce = (EditText) findViewById(R.id.nomCommerce);
        EditText produitCommerce = (EditText) findViewById(R.id.produitCommerce);

        MesCoursesManager mcm = new MesCoursesManager(this);
        mcm.open();
        long intReturn = mcm.addElemCourse(new MesCourses(0, nomCommerce.getText().toString(), produitCommerce.getText().toString()));
        // PENSER A REGARDER QUE CEST AUSIS LE MEME MAGASIN
        if(intReturn == -2){
            Toast.makeText(AjouterListeCourse.this, getText((R.string.ArticleDejaPresent)), Toast.LENGTH_LONG).show();
        }
        // NE FONCTIONNE PAS QUAND L'ARTICLE VIDE EST LE 1ER ARTICLE AJOUTE
        if(intReturn == -3){
            Toast.makeText(AjouterListeCourse.this, getText((R.string.ProduitNonRenseigné)), Toast.LENGTH_LONG).show();
        }

        lireElements();
        mcm.close();

    }

    public void lireElements() {
        listview = findViewById(R.id.elemCourse);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(AjouterListeCourse.this, android.R.layout.simple_list_item_1);
        listview.setAdapter(adapter);

        MesCoursesManager mcm = new MesCoursesManager(this);
        mcm.open();

        Intent intent = getIntent();

        if(intent.hasExtra("Magasin")) {
            String donnee = intent.getStringExtra("Magasin"); // on récupère la valeur associée à la clé
            // Listing des enregistrements de la table
            ArrayList<String> listePourMagasin = mcm.getListePourMagasin(donnee);
            for (int i = 0; i < listePourMagasin.size(); ++i) {
                adapter.add(listePourMagasin.get(i));
            }
        }else {
            if (intent.hasExtra("storeName")) {
                String donnee = intent.getStringExtra("storeName"); // on récupère la valeur associée à la clé
                // Listing des enregistrements de la table
                ArrayList<String> listePourMagasin = mcm.getListePourMagasin(donnee);
                for (int i = 0; i < listePourMagasin.size(); ++i) {
                    adapter.add(listePourMagasin.get(i));
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    public void validerListeCourse(View v){
        Intent intent = new Intent(AjouterListeCourse.this, ListesCourse.class);
        startActivity(intent);

    }
}