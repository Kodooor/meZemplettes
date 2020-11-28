package com.m1info.mapsstart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.text.IDNA;
import android.media.Rating;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class InfoMarkerActivity extends AppCompatActivity implements Serializable{

    private String TAG = "InfoMarkerActivity";

    ListView mListView,listOpHours;
    ImageView markerImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_marker);


        mListView = (ListView) findViewById(R.id.listView);
        Bundle extras = getIntent().getExtras();

        // Clé d'API Google
        String apiKey = "AIzaSyCAg3UDXwGUIvN8FPK8hk8IN8Db0JF6NIw";
        // Initialisation des places.
        Places.initialize(getApplicationContext(), apiKey);
        PlacesClient placesClient = Places.createClient(this);


        if (extras != null) {
            String titre = extras.getString("titleMarker");


            // On récupère l'intent et les données passées en Extra
            Intent intent = getIntent();
            @SuppressWarnings("unchecked")
            HashMap<String,String> myInfoMarker = (HashMap<String, String>)intent.getSerializableExtra("listInfoMarker");

            String id = myInfoMarker.get(titre);
            // Définir l'ID de la place à fetch
            final String placeId = id;
            // Spécifier les champs à fetch
            final List<Place.Field> fields = Arrays.asList(
                    Place.Field.NAME,
                    Place.Field.ADDRESS,
                    Place.Field.PHONE_NUMBER,
                    Place.Field.RATING,
                    Place.Field.WEBSITE_URI,
                    Place.Field.OPENING_HOURS,
                    Place.Field.BUSINESS_STATUS,
                    Place.Field.PHOTO_METADATAS);

            // Fetch la Place avec les données spécifiées
            final FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance(placeId, fields);

            placesClient.fetchPlace(placeRequest).addOnSuccessListener((response) -> {
                final Place place = response.getPlace();

                // Récupérations de chacune des données
                String name = place.getName();
                // Titre de l'activity
                setTitle(name);

                String address = place.getAddress();
                String phone = place.getPhoneNumber();
                String rating=null;
                if(place.getRating()!=null)
                    rating = String.valueOf(place.getRating());

                String website = null;
                String businessStatus = null;

                if(place.getWebsiteUri() != null)
                    website = place.getWebsiteUri().toString();
                if(place.getBusinessStatus() != null) {
                    businessStatus = place.getBusinessStatus().toString();
                    //String status[] = {"CLOSED_PERMANENTLY","CLOSED_TEMPORARILY","OPERATIONAL"};
                    switch (businessStatus) {
                        case "CLOSED_PERMANENTLY":
                            businessStatus = getString(R.string.CLOSED_PERMANENTLY);
                            break;
                        case "CLOSED_TEMPORARILY":
                            businessStatus = getString(R.string.CLOSED_TEMPORARILY);
                            break;
                        case "OPERATIONAL":
                            businessStatus = getString(R.string.OPERATIONAL);
                            break;
                        default:
                            break;
                    }
                }

                // A partir de là on va traiter nos données pour remplir notre layout
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
                List<String> opHours;
                if(place.getOpeningHours() != null) {
                    opHours = place.getOpeningHours().getWeekdayText();
                    listOpHours = findViewById(R.id.listOpHours);
                    // Remplir le TextView avant la liste pour lui donner un titre
                    TextView opHoursTitle = findViewById(R.id.opHours);
                    opHoursTitle.setText(R.string.opHours);

                    // Créer l'adapter pour remplir la listview
                    ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(
                            this,
                            android.R.layout.simple_list_item_1,
                            opHours);

                    listOpHours.setAdapter(arrayAdapter1);
                }


                // Ajouter l'image
                markerImage = (ImageView) findViewById(R.id.MarkerImage);

                // Get the photo metadata.
                final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
                if (metadata == null || metadata.isEmpty()) {
                    Log.w(TAG, "No photo metadata.");
                    return;
                }
                final PhotoMetadata photoMetadata = metadata.get(0);

                // Get the attribution text.
                final String attributions = photoMetadata.getAttributions();

                // Create a FetchPhotoRequest.
                final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
//                        .setMaxWidth(500) // Optional.
//                        .setMaxHeight(300) // Optional.
                        .build();
                placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                    Bitmap bitmap = fetchPhotoResponse.getBitmap();
                    markerImage.setImageBitmap(bitmap);
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        final ApiException apiException = (ApiException) exception;
                        Log.e(TAG, "Place not found: " + exception.getMessage());
                        final int statusCode = apiException.getStatusCode();
                        // TODO: Handle error with given status code.
                    }
                });
            });

        } // if Extras !null
    } // onCreate()
}