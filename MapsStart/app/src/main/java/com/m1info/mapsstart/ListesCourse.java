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
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

public class ListesCourse extends Activity {

    ArrayList<String> mesCourses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_course);
        mesCourses = new ArrayList<String>();
        MesCoursesManager mcm = new MesCoursesManager(this);
        RecyclerView rvMesCourses = (RecyclerView) findViewById(R.id.listesCourse);

        mcm.open();
        Cursor c = mcm.getMagasinListeCourses();
        if (c.moveToFirst()) {
            do {
                mesCourses.add(c.getString(c.getColumnIndex(MesCoursesManager.KEY_NOM_MAGASIN)));
            }
            while (c.moveToNext());
        }
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(mesCourses);
        rvMesCourses.setAdapter(adapter);

        rvMesCourses.setLayoutManager(new LinearLayoutManager(this));
        adapter.setOnItemClickListener(new RecyclerViewAdapter.ClickListener(){
            @Override
            public void onItemClick(int position, View v) {
                int itemPosition =  rvMesCourses.getChildLayoutPosition(v);
                String item = mesCourses.get(itemPosition);
                Intent intent = new Intent(ListesCourse.this, AjouterListeCourse.class);
                intent.putExtra("storeName", item);
                startActivityForResult(intent, 0);
                Log.d("HEYHEYHEY", "onItemClick position: " + position);
            }

            @Override
            public void onItemLongClick(int position, View v) {
                new AlertDialog.Builder(ListesCourse.this)
                        .setTitle("Alerte")
                        .setMessage("Voulez vous supprimer cette liste de course ?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                int itemPosition =  rvMesCourses.getChildLayoutPosition(v);
                                String item = mesCourses.get(itemPosition);
                                MesCoursesManager mcm = new MesCoursesManager(ListesCourse.this);
                                mcm.open();
                                mcm.supprimerListeCourse(item);
                                afficherLesListesCourses();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                Log.d("HOHOHOHOHOH", "onItemLongClick pos = " + position);
            }
        });
        // Drag and drop / Swipe
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
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                String item = mesCourses.get(position);
                MesCoursesManager mcm = new MesCoursesManager(ListesCourse.this);
                mcm.open();
                mcm.supprimerListeCourse(item);
                adapter.mesCourses.remove(position);
                adapter.notifyItemRemoved(position);
                Toast toast = new Toast(ListesCourse.this);
                toast.makeText(ListesCourse.this, "Element supprimé", Toast.LENGTH_LONG).show();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemToucherHelperCallback);
        itemTouchHelper.attachToRecyclerView(rvMesCourses);
        afficherLesListesCourses();
    }



    public void afficherLesListesCourses() {

        mesCourses = new ArrayList<String>();
        MesCoursesManager mcm = new MesCoursesManager(this);
        mcm.open();

        RecyclerView rvMesCourses = (RecyclerView) findViewById(R.id.listesCourse);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(mesCourses);
        rvMesCourses.setAdapter(adapter);

        Cursor c = mcm.getMagasinListeCourses();

        if (c.moveToFirst()) {
            do {
                mesCourses.add(c.getString(c.getColumnIndex(MesCoursesManager.KEY_NOM_MAGASIN)));
                Log.d("TUTUTUTUTUTUTUTUTU",c.getString(c.getColumnIndex(MesCoursesManager.KEY_NOM_MAGASIN)));
            }
            while (c.moveToNext());
        }
        adapter.notifyDataSetChanged();
        c.close(); // fermeture du curseur
    }


}
