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

// Class gérant l'édition d'une liste de course, qu'on y accede par le "+" de la Map ou bien par
// les listes de liste de course
public class AjouterListeCourse extends Activity {

    // Attributs
    ListView listview;
    Spinner mesRayons;
    Spinner monFiltre;
    MesCoursesManager mcm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajouter_liste_course);

        // On récupère le magasin sur lequel on a décidé de faire la liste de course dans MapsActivity
        Intent intent = getIntent();
        String storeName = intent.getStringExtra("storeName");
        EditText nomCommerce = (EditText) findViewById(R.id.nomCommerce);
        // On le set dans le champ nomMagasin de cette vue
        nomCommerce.setText(storeName);

        // On instancie notre gestionnaire de base de données
        mcm = new MesCoursesManager(AjouterListeCourse.this);
        mcm.open();

        //Spinner Rayons (dropdown)
        mesRayons = findViewById(R.id.rayon);
        String[] rayons = new String[]{"", "Fruits & Légumes", getString(R.string.Surgelés), getString(R.string.Boulangerie), getString(R.string.Viande), getString(R.string.Poissons), getString(R.string.Crémerie), getString(R.string.HygièneBeauté), getString(R.string.Autres)};
        ArrayAdapter<String> adapterRayon = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, rayons);
        mesRayons.setAdapter(adapterRayon);

        //Spinner Filtre(dropdown)
        monFiltre = findViewById(R.id.filtre);
        String[] filtre = new String[]{"Filtrer",getString(R.string.FruitLegumes), getString(R.string.Surgelés), getString(R.string.Boulangerie), getString(R.string.Viande), getString(R.string.Poissons), getString(R.string.Crémerie), getString(R.string.HygièneBeauté), getString(R.string.Autres)};
        ArrayAdapter<String> adapterFiltre = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, filtre);
        monFiltre.setAdapter(adapterFiltre);

        // ListView des éléments
        listview = findViewById(R.id.elemCourse);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(AjouterListeCourse.this, android.R.layout.simple_list_item_1);
        listview.setAdapter(adapter);
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            // Au longClick sur un élément de la listeview, on a une alerte avec possibilité de supprime l'élément
            public boolean onItemLongClick(AdapterView<?> arg0, View v, int position, long id) {

                new AlertDialog.Builder(AjouterListeCourse.this)
                        .setTitle(R.string.Alerte)
                        .setMessage(R.string.SuppItemListCourse)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Au click sur "Ok", on récupère l'item sélectionné
                                String item = (String) listview.getItemAtPosition(position);
                                // On le supprime
                                mcm.supElemCourseParProduit(item);
                                // On actualise la vue
                                lireElements();
                            }
                        })
                        // Au clic sur le bouton annuler on ne fait rien
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                return true;
            }

        });
        // A la selection d'un item du filtre
        monFiltre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                // On appelle la fonction lireElement également, qui gère le filtre
                lireElements();
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        // Dans tous les cas, on lis les elements
        lireElements();
    }

    // Fonction qui permet d'ajouter un élément à notre liste de course (listview)
    public void ajouterElement(View v) {
        // Nos 3 champs
        EditText nomCommerce = (EditText) findViewById(R.id.nomCommerce);
        EditText produitCommerce = (EditText) findViewById(R.id.produitCommerce);
        Spinner monRayon = (Spinner) findViewById(R.id.rayon);

        // On appelle la fonction d'ajout et en fonction du résultat, si il y a une erreur/incohérence, on fait des Toast
        // Pour l'indiquer à l'utilisateur
        long intReturn = mcm.addElemCourse(new MesCourses(0, nomCommerce.getText().toString(), produitCommerce.getText().toString(), monRayon.getSelectedItem().toString()));
        // Toutes les erreurs/incohérences possibles ici
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

        // On appelle lireElements
        lireElements();
        // On réinitialise l'édit text contenant le produit, ainsi que le Spinner du rayon
        ((EditText) findViewById(R.id.produitCommerce)).getText().clear();
        ((Spinner) findViewById(R.id.rayon)).setSelection(0);
      //  mcm.close();

    }

    // Fonction qui permet d'afficher la bonne listView en fonction des élements ajoutés, et d'un éventuel filtre
    public void lireElements() {
        // On redéfinit notre listview car à chaque changement (ajout d'un produit ou un filtre) on la recalcule entièrement
        listview = findViewById(R.id.elemCourse);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(AjouterListeCourse.this, android.R.layout.simple_list_item_1);
        listview.setAdapter(adapter);

        // On crée une variable donnee qui nous servira en fonction de quel page on vient
        String donnee = "";
        // On get l'intent
        Intent intent = getIntent();

        // Si on vient de la vue de nos listes de listes de course
        if(intent.hasExtra("Magasin")) {
            donnee = intent.getStringExtra("Magasin"); // on récupère la valeur associée à la clé
        }
        // Si on vient de la maps
        if(intent.hasExtra("storeName")) {
            donnee = intent.getStringExtra("storeName"); // on récupère la valeur associée à la clé
        }
        // Désormais, on regarde si on active un filtre
        // Si c'est le cas
        if(!monFiltre.getSelectedItem().toString().equals("Filtrer")){
            // On get notre liste de course pour le magasin "@variable donnee" avec le filtre dans la clause where
            ArrayList<String> listePourMagasin = mcm.getListePourMagasinFiltre(donnee, monFiltre.getSelectedItem().toString());
            for (int i = 0; i < listePourMagasin.size(); ++i) {
                adapter.add(listePourMagasin.get(i));
            }
        }
        // Si ce n'est pas le cas
        else{
            // On get notre liste de course pour le magasin "@variable donnee" avec aucun filtre dans la clause where
            ArrayList<String> listePourMagasin = mcm.getListePourMagasin(donnee);
            for (int i = 0; i < listePourMagasin.size(); ++i) {
                adapter.add(listePourMagasin.get(i));
            }
        }
        // On notifie l'adapter du changement
        adapter.notifyDataSetChanged();
    }

    // Fonction qui nous redirige vers la page de listes de liste de course lorsque on clique sur le bouton "Valider"
    // On en profite pour close le gestionnaire de bdd, qui sera ouvert dans la vue suivante
    public void validerListeCourse(View v){
        mcm.close();
        Intent intent = new Intent(AjouterListeCourse.this, ListesCourse.class);
        startActivity(intent);

    }
}