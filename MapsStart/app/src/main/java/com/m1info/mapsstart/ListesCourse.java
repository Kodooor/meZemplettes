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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

// Classe gérant la vue de nos listes de course crées
// Nos listes de course sont stockées dans des recyclerView (voir RecyclerViewAdapter.java)
public class ListesCourse extends Activity {

    // Nos listes : on stocke seulement le nom du magasin dans cette liste car dans la recyclerView on
    // affiche seulement cela
    ArrayList<String> mesCourses;
    MesCoursesManager mcm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_course);

        // On instancie notre liste
        mesCourses = new ArrayList<String>();

        // On instancie notre gestionnaire de base de données
        mcm = new MesCoursesManager(this);
        mcm.open();

        // On va chercher notre recyclerView du XML
        RecyclerView rvMesCourses = (RecyclerView) findViewById(R.id.listesCourse);

        // On récupère toutes les listes de course crées (seulement le nom du magasin)
        Cursor c = mcm.getMagasinListeCourses();
        if (c.moveToFirst()) {
            do {
                // On les ajoute à notre liste
                mesCourses.add(c.getString(c.getColumnIndex(MesCoursesManager.KEY_NOM_MAGASIN)));
            }
            while (c.moveToNext());
        }

        // On instancie notre adapter de recyclerView
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(mesCourses);
        // On la set à notre recyclerView
        rvMesCourses.setAdapter(adapter);
        // Puis on lui set un layout
        rvMesCourses.setLayoutManager(new LinearLayoutManager(this));

        // Au clic sur élément de notre recyclerView
        adapter.setOnItemClickListener(new RecyclerViewAdapter.ClickListener(){
            @Override
            public void onItemClick(int position, View v) {
                // On récupère le nom du magasin sur lequel l'utilisateur à appuyé
                int itemPosition =  rvMesCourses.getChildLayoutPosition(v);
                String item = mesCourses.get(itemPosition);

                // Puis on redirige vers la vue AjouterListeCourse pour qu'il puisse la consulter, et, si il le souhaite,
                // éditer la liste de course.
                // On oubliera pas de passer le nom du magasin dans l'intent pour le réaffiché dans le textView
                Intent intent = new Intent(ListesCourse.this, AjouterListeCourse.class);
                intent.putExtra("storeName", item);
                startActivityForResult(intent, 0);
            }

            @Override
            // Si on fait un longClick sur un élement de la recyclerView
            public void onItemLongClick(int position, View v) {
                // On créer une boite de dialogue pour lui donner la possibilite de supprimer cette liste de course
                new AlertDialog.Builder(ListesCourse.this)
                        .setTitle(R.string.Alerte)
                        .setMessage(R.string.SuppListCourse)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Si l'utilisateur clique sur "ok"
                                // On récupère la liste sur laquelle il a cliqué
                                int itemPosition =  rvMesCourses.getChildLayoutPosition(v);
                                String item = mesCourses.get(itemPosition);
                                // Et on la supprime
                                mcm.supprimerListeCourse(item);
                                // On actualise la page
                                afficherLesListesCourses();
                            }
                        })
                        // Sinon on ne fait rien : on lui réaffiche la page qui n'a pas bougé
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
        // Fonctionnalité Swipe pour supprimer un élement
        ItemTouchHelper.Callback itemToucherHelperCallback = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Collections.swap(adapter.mesCourses, viewHolder.getAdapterPosition(), target.getAdapterPosition());
                adapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            // Fonction qui permet de supprimer une liste de course par un swipe
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // On récupère l'item touché
                int position = viewHolder.getAdapterPosition();
                String item = mesCourses.get(position);

                // On le supprime
                mcm.supprimerListeCourse(item);

                // On le remove de la recyclerView
                adapter.mesCourses.remove(position);
                adapter.notifyItemRemoved(position);

                // On en informe l'utilisateur par un toast
                Toast toast = new Toast(ListesCourse.this);
                toast.makeText(ListesCourse.this, R.string.ElemSupprimé, Toast.LENGTH_LONG).show();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemToucherHelperCallback);
        itemTouchHelper.attachToRecyclerView(rvMesCourses);
        afficherLesListesCourses();
    }



// Fonction permettant d'afficher toutes les listes crées
    public void afficherLesListesCourses() {

        // On instancie notre liste de course car on la recalcule
        mesCourses = new ArrayList<String>();

        // On réinstancie aussi notre recyclerView
        RecyclerView rvMesCourses = (RecyclerView) findViewById(R.id.listesCourse);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(mesCourses);
        rvMesCourses.setAdapter(adapter);

        // Puis avec notre fonction SQL, on remplit notre recyclerView
        Cursor c = mcm.getMagasinListeCourses();

        if (c.moveToFirst()) {
            do {
                mesCourses.add(c.getString(c.getColumnIndex(MesCoursesManager.KEY_NOM_MAGASIN)));
            }
            while (c.moveToNext());
        }
        adapter.notifyDataSetChanged();
        c.close(); // fermeture du curseur
    }


}
