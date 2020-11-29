package com.m1info.mapsstart;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

// Fonction qui gère la recyclerView de la vue ListesCourse.java
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    // Notre variable qui stockera nos élement
    public ArrayList<String> mesCourses;
    // Notre variable du type de l'interface pour définir onItemClick et onItemLongClick
    private static ClickListener clickListener;

    // Setteur
    public void setOnItemClickListener(ClickListener clickListener) {
        RecyclerViewAdapter.clickListener = clickListener;
    }

    // Interface pour définir les fonction onClick onLongClick
    public interface ClickListener {
        void onItemClick(int position, View v);

        void onItemLongClick(int position, View v);
    }

    // Constructeur
    public RecyclerViewAdapter(ArrayList<String> mesCoursess) {
        mesCourses = mesCoursess;
    }


    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.item_listes_course, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Fonction qui gère l'affichage de la recyclerView
    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {

        String course = mesCourses.get(position);

        TextView textView = holder.magasin;
        textView.setText("Liste pour le magasin " + course);
    }

    // Retourne le nombre d'éléments de la liste de courses
    @Override
    public int getItemCount() {
        return mesCourses.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView magasin;

        public ViewHolder(View itemView) {

            super(itemView);

            // Le magasin est stocké dans un TextView
            magasin = (TextView) itemView.findViewById(R.id.magasin);

            // On set au magasin un onClick et un onLongClick
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        // Ce qui sera fait dans le onCLick et onLongClick est défini dans ListesCourses.java, ici
        // on défini juste les fonction sur l'adapteur
        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }

        @Override
        public boolean onLongClick(View v) {
            clickListener.onItemLongClick(getAdapterPosition(), v);
            return false;
        }


    }

}