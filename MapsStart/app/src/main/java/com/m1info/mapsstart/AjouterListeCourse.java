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
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.Collections;

public class AjouterListeCourse extends Activity {

    ListView listview;
    Spinner mesRayons;
    Spinner monFiltre;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajouter_liste_course);

        Intent intent = getIntent();
        String storeName = intent.getStringExtra("storeName");
        EditText nomCommerce = (EditText) findViewById(R.id.nomCommerce);
        nomCommerce.setText(storeName);

        //Spinner Rayons (dropdown)
        mesRayons = findViewById(R.id.rayon);
        String[] rayons = new String[]{"", getString(R.string.FruitLegumes), getString(R.string.Surgelés), getString(R.string.Boulangerie), getString(R.string.Viande), getString(R.string.Poissons), getString(R.string.Autres)};
        ArrayAdapter<String> adapterRayon = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, rayons);
        mesRayons.setAdapter(adapterRayon);

        //Spinner Filtre(dropdown)
        monFiltre = findViewById(R.id.filtre);
        String[] filtre = new String[]{"",getString(R.string.FruitLegumes), getString(R.string.Surgelés), getString(R.string.Boulangerie), getString(R.string.Viande), getString(R.string.Poissons), getString(R.string.Crémerie), getString(R.string.HygièneBeauté), getString(R.string.Autres)};
        ArrayAdapter<String> adapterFiltre = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, rayons);
        monFiltre.setAdapter(adapterRayon);

        // ListView des éléments
        listview = findViewById(R.id.elemCourse);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(AjouterListeCourse.this, android.R.layout.simple_list_item_1);
        listview.setAdapter(adapter);
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View v, int position, long id) {

                new AlertDialog.Builder(AjouterListeCourse.this)
                        .setTitle(R.string.Alerte)
                        .setMessage(R.string.SuppItemListCourse)
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
        monFiltre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                lireElements();
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        lireElements();
    }

    public void ajouterElement(View v) {
        EditText nomCommerce = (EditText) findViewById(R.id.nomCommerce);
        EditText produitCommerce = (EditText) findViewById(R.id.produitCommerce);
        Spinner monRayon = (Spinner) findViewById(R.id.rayon);

        MesCoursesManager mcm = new MesCoursesManager(this);
        mcm.open();
        Log.d("places", monRayon.getSelectedItem().toString());
        long intReturn = mcm.addElemCourse(new MesCourses(0, nomCommerce.getText().toString(), produitCommerce.getText().toString(), monRayon.getSelectedItem().toString()));
        // PENSER A REGARDER QUE CEST AUSIS LE MEME MAGASIN
        if(intReturn == -2){
            Toast.makeText(AjouterListeCourse.this, R.string.ArticleDejaPresent, Toast.LENGTH_LONG).show();
        }
        // NE FONCTIONNE PAS QUAND L'ARTICLE VIDE EST LE 1ER ARTICLE AJOUTE
        if(intReturn == -3){
            Toast.makeText(AjouterListeCourse.this, R.string.ProduitNonRenseigné, Toast.LENGTH_LONG).show();
        }
        // NE FONCTIONNE PAS QUAND LE CHAMP RAYON N EST PAS RENSEIGNE
        if(intReturn == -4){
            Toast.makeText(AjouterListeCourse.this, R.string.SelectionnerRayon, Toast.LENGTH_LONG).show();
        }

        lireElements();
        // On réinitialise l'édit text contenant le produit
        ((EditText) findViewById(R.id.produitCommerce)).getText().clear();
        ((Spinner) findViewById(R.id.rayon)).setSelection(0);
        mcm.close();

    }

    public void lireElements() {
        listview = findViewById(R.id.elemCourse);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(AjouterListeCourse.this, android.R.layout.simple_list_item_1);
        listview.setAdapter(adapter);

        Spinner monFiltre = (Spinner) findViewById(R.id.filtre);
        Log.d("TUUUUUUUUUUUPZPPZPZPZPZ", monFiltre.getSelectedItem().toString());
        MesCoursesManager mcm = new MesCoursesManager(this);
        mcm.open();

        Intent intent = getIntent();

        if(intent.hasExtra("Magasin")){
            String donnee = intent.getStringExtra("Magasin"); // on récupère la valeur associée à la clé
            if(!monFiltre.getSelectedItem().toString().equals("")){
                ArrayList<String> listePourMagasin = mcm.getListePourMagasinFiltre(donnee, monFiltre.getSelectedItem().toString());
                for (int i = 0; i < listePourMagasin.size(); ++i) {
                    adapter.add(listePourMagasin.get(i));
                }
            }
            else{
                // Listing des enregistrements de la table
                ArrayList<String> listePourMagasin = mcm.getListePourMagasin(donnee);
                for (int i = 0; i < listePourMagasin.size(); ++i) {
                    adapter.add(listePourMagasin.get(i));
                }
            }
        }
        if(intent.hasExtra("storeName")){
            String donnee = intent.getStringExtra("storeName"); // on récupère la valeur associée à la clé

            if(!monFiltre.getSelectedItem().toString().equals("")){
                ArrayList<String> listePourMagasin = mcm.getListePourMagasinFiltre(donnee, monFiltre.getSelectedItem().toString());
                for (int i = 0; i < listePourMagasin.size(); ++i) {
                    adapter.add(listePourMagasin.get(i));
                }
            }
            else{
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