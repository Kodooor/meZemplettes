package com.m1info.mapsstart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class InfoMarkerActivity extends AppCompatActivity implements Serializable{

    private String TAG = "InfoMarkerActivity";

    ListView mListView,listOpHours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_marker);


        mListView = (ListView) findViewById(R.id.listView);
        Bundle extras = getIntent().getExtras();


        if (extras != null) {
            String titre = extras.getString("titleMarker");


            // On récupère l'intent et les données passées en Extra
            Intent intent = getIntent();
            @SuppressWarnings("unchecked")
            HashMap<String,InfoMarker> myListInfoMarker = (HashMap<String, InfoMarker>)intent.getSerializableExtra("listInfoMarker");

            InfoMarker myInfoMarker = myListInfoMarker.get(titre);
//            Log.d(TAG, "It Works : " + myInfoMarker.getAddress());

            // Titre de l'activity
            setTitle(myInfoMarker.getTitre());


//            public InfoMarker(String infos[], List<String> opHours) {
//                _titre = infos[0];
//                _address = infos[1];
//                _phone = infos[2];
//                _rating = infos[3];
//                _website = infos[4];
//                _businessStatus = infos[5];
//                _opHours = opHours;
//            }
            // On extrait les données de la Hashmap
            String address = myInfoMarker.getAddress();
            String phone = myInfoMarker.getPhone();
            String rating = myInfoMarker.getRating();
            String website = myInfoMarker.getWebsite();
            String businessStatus = myInfoMarker.getBusinessStatus();


            int tabSize = 5;
            String description[] = {getString(R.string.adress),getString(R.string.phone),getString(R.string.rating),getString(R.string.website),getString(R.string.businessStatus)};
            String values[] = {address,phone,rating,website,businessStatus};

            // On créé et remplit une HashMap de pairs de descriptions/valeurs en String pour être affichées
            HashMap<String,String> myHashMap = new HashMap<>();
            for(int i=0; i<tabSize; i++){
                if(values[i] != null) {
                    myHashMap.put(description[i], values[i]);
                }
            }

            // Remplir une listview avec des items et des subitems
            List<HashMap<String,String>> myList = new ArrayList<>();

            SimpleAdapter adapter = new SimpleAdapter(this,myList,R.layout.items_liste_infomarker,
                    new String[]{"First Line", "Second Line"},
                    new int[]{R.id.text1,R.id.text2});

            Iterator it = myHashMap.entrySet().iterator();

            while(it.hasNext()){
                HashMap<String, String> resultMap = new HashMap<>();
                Map.Entry pair = (Map.Entry)it.next();
                resultMap.put("First Line", pair.getKey().toString());
                resultMap.put("Second Line", pair.getValue().toString());
                myList.add(resultMap);
            }
            mListView.setAdapter(adapter);


            // Remplissage de la listView des horaires d'ouvertures
            List<String> opHours = myInfoMarker.getOpHours();
            listOpHours = (ListView) findViewById(R.id.listOpHours);

            if(opHours != null){
                // Remplir le TextView avant la liste pour lui donner un titre
                TextView opHoursTitle = (TextView)findViewById(R.id.opHours);
                opHoursTitle.setText(R.string.opHours);

                // Créer l'adapter pour remplir la listview
                ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_list_item_1,
                        opHours);

                listOpHours.setAdapter(arrayAdapter1);
            }



//            List<String> valNotNull = new ArrayList<String>();
//
//            for (String elem : values ){
//                if(elem != null){
//                    valNotNull.add(elem);
//                }
//            }
//
//            List<String> desc = Arrays.asList(description);
////            List<String> val = Arrays.asList(values);
//
//            ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(
//                    this,
//                    android.R.layout.simple_list_item_1,
//                    valNotNull );
//
//            mListView.setAdapter(arrayAdapter1);
        } // if Extras !null
    } // onCreate()
}