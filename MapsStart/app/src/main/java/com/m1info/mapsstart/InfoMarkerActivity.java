package com.m1info.mapsstart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfoMarkerActivity extends AppCompatActivity implements Serializable{

    private String TAG = "InfoMarkerActivity";

    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_marker);


        mListView = (ListView) findViewById(R.id.listView);
        Bundle extras = getIntent().getExtras();



        if (extras != null) {
            String titre = extras.getString("titleMarker");


            Intent intent = getIntent();
            @SuppressWarnings("unchecked")
            HashMap<String,InfoMarker> myListInfoMarker = (HashMap<String, InfoMarker>)intent.getSerializableExtra("listInfoMarker");

            InfoMarker myInfoMarker = myListInfoMarker.get(titre);
//            Log.d(TAG, "It Works : " + myInfoMarker.getAddress());

            setTitle(myInfoMarker.getTitre());
            //The key argument here must match that used in the other activity
            String adr = myInfoMarker.getAddress();
            String phone = myInfoMarker.getPhone();



            String description[] = {getString(R.string.adress),getString(R.string.phone)};
            String values[] = {myInfoMarker.getAddress(),myInfoMarker.getPhone()};

            List<String> valNotNull = new ArrayList<String>();

            for (String elem : values ){
                if(elem != null){
                    valNotNull.add(elem);
                }
            }

            List<String> desc = Arrays.asList(description);
//            List<String> val = Arrays.asList(values);

            ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_list_item_1,
                    valNotNull );

            mListView.setAdapter(arrayAdapter1);



        }



    }
}