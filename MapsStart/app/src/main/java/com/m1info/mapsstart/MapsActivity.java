package com.m1info.mapsstart;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.Rating;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    private GoogleMap mMap;
    private String TAG = "places";
    private boolean mLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private float DEFAULT_ZOOM = 1;
    private LatLng mDefaultLocation ;
    private String[] mLikelyPlaceAddresses;
    private String[] mLikelyPlaceNames;
    private List[] mLikelyPlaceAttributions;
    private LatLng[] mLikelyPlaceLatLngs;
    private List[] mLikelyPlaceTypes;
    private HashMap<String,InfoMarker> listInfoMarker = new HashMap<String,InfoMarker>();
    private String storeName;
    private String[] mLikelyPlacePhone;
    private String[] mLikelyPlaceWebsite;
    private String[] mLikelyPlaceBusinessStatus;
    private String[] mLikelyPlaceRating;
    private List<String>[] mLikelyPlaceOpHours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Coordonées par défaut de l'application sur Berlin.
        mDefaultLocation = new LatLng(52.530644,	13.383068);

        // Construction du client permettant d'utiliser le FusedLocationProvider
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtention du SupportFragmentManager permettant de placer la map sur l'application.
        // L'application attend aussi que la map soit synchronisée.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    // Fonction permettant la prise d'information sur le lieu actuellement selectionné
    // et la création d'un intent vers l'activité AjouterListeCourse.
    // L'intent prend le nom de ses extras permettant un saisie plus rapide d'une liste
    // de course sur le lieu.
    public void onButtonClick(View view) {

        Intent intent=new Intent(MapsActivity.this,AjouterListeCourse.class);
        intent.putExtra("storeName", storeName);
        startActivityForResult(intent, 2);

    }

    public void onSecondButtonClick(View view) {

        Intent intent=new Intent(MapsActivity.this,ListesCourse.class);
        startActivityForResult(intent, 2);// Activity is started with requestCode 2
    }

    // Requete permettant d'avoir la permission d'utiliser la localisation de l'appareil
    // de l'utilisateur.
    // Le resultat de cette permission est gardé par onRequestPermissionsResult.
    private void getLocationPermission() {

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    // Resultat de la permission obtenue (ou pas) grace à la fonction précédante.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();

    }

    // Fonction permettant d'afficher la carte si et seulement si la permission
    // d'utiliser la localisation de l'appareil a été accordée.
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    // Fonction permettant de marquer avec un Marker l'endroit sur lequel l'utilisateur se trouve.
    private void getDeviceLocation() {

        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        mLastKnownLocation = (Location) task.getResult();

                        // Recuperation des coordonées dans un objet de type LatLng
                        LatLng maPos = new LatLng(mLastKnownLocation.getLatitude(),
                                mLastKnownLocation.getLongitude());
                        // Ajout du Marker a la position maPos
                        mMap.addMarker(new MarkerOptions().position(maPos).title("Votre position").icon(BitmapDescriptorFactory.fromResource(R.drawable.me)));
                        // Animation de camera lors de l'ouverture de l'application. La caméra se dirige donc vers
                        // les coordonées si toutes les permissions ont été accordées.
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude()), 14f));

                        /*
                        mMap.addMarker(new MarkerOptions().position(new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude()+0.008)).title("Your are here !").snippet("and snippet").icon(BitmapDescriptorFactory.fromResource(R.drawable.supermarket)));
                        mMap.addMarker(new MarkerOptions().position(new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude()-0.008)).title("Your are here !").snippet("and snippet").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                        mMap.addMarker(new MarkerOptions().position(new LatLng(mLastKnownLocation.getLatitude()+0.008,mLastKnownLocation.getLongitude())).title("Your are here !").snippet("and snippet").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                        mMap.addMarker(new MarkerOptions().position(new LatLng(mLastKnownLocation.getLatitude()-0.008,mLastKnownLocation.getLongitude())).title("Your are here !").snippet("and snippet").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                        */

                    } else {
                        Log.d(TAG, "Localisation null, utilisatation de la valeur par défaut");
                        Log.e(TAG, "Exception: %s", task.getException());
                        mLastKnownLocation.setAltitude(mDefaultLocation.latitude);
                        mLastKnownLocation.setLongitude(mDefaultLocation.longitude);

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                        mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    // Fonction permettant de gerer toutes actions faites sur la map.
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        updateLocationUI();
        getDeviceLocation();
        showCurrentPlace();
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng arg0)
            {
                ImageButton ajouterButton = (ImageButton) findViewById(R.id.ajouter);
                ajouterButton.setVisibility(View.INVISIBLE);
            }
        });
        mMap.setOnInfoWindowClickListener(this);
    }

    // Fonction permettant de gerer tous les supermarchés les plus proches dans lesquels
    // l'utilisateur serait suceptible de faire ses courses.
    private void showCurrentPlace() {
        if (mMap == null) {
            return;
        }

        if (mLocationPermissionGranted) {
            // Use fields to define the data types to return.
            List<Place.Field> placeFields = Arrays.asList(
                    Place.Field.NAME,
                    Place.Field.ADDRESS,
                    Place.Field.TYPES ,
                    Place.Field.LAT_LNG,
//                    Place.Field.PHONE_NUMBER,
                    Place.Field.RATING,
//                    Place.Field.WEBSITE_URI,
//                    Place.Field.OPENING_HOURS,
                    Place.Field.BUSINESS_STATUS
            );

            // Création du la requete FindCurrentPlace
            FindCurrentPlaceRequest request =
                    FindCurrentPlaceRequest.newInstance(placeFields);

            // Clé d'API Google
            String apiKey = "AIzaSyCAg3UDXwGUIvN8FPK8hk8IN8Db0JF6NIw";
            // Initialisation des places.
            Places.initialize(getApplicationContext(), apiKey);
            PlacesClient mPlacesClient = Places.createClient(this);

            @SuppressWarnings("MissingPermission") final
            Task<FindCurrentPlaceResponse> placeResult = mPlacesClient.findCurrentPlace(request);
            /*Log.d(TAG,request.toString());*/

            placeResult.addOnCompleteListener (task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    // Les likelyPlaces sont les points d'interet qui "match" le plus avec
                    // la localisation de l'appareil.
                    FindCurrentPlaceResponse likelyPlaces = task.getResult();

                    /*Log.d(TAG, "Taille likelyPlaces : " + likelyPlaces.getPlaceLikelihoods().size());
                    Log.d(TAG, "===============================" );*/

                    // Permet d'avoir moins de 20 lieux autour de sa position, afin de ne pas
                    // surcharger la map/
                    int count;
                    if (likelyPlaces.getPlaceLikelihoods().size() < 20) {
                        count = likelyPlaces.getPlaceLikelihoods().size();
                    } else {
                        count = 20;
                    }
                    int i = 0;
                    mLikelyPlaceNames = new String[count];
                    mLikelyPlaceTypes = new List[count];
                    mLikelyPlaceAddresses = new String[count];
                    mLikelyPlaceAttributions = new List[count];
                    mLikelyPlaceLatLngs = new LatLng[count];

                    mLikelyPlaceOpHours = new ArrayList[count];
                    mLikelyPlaceWebsite = new String[count];
                    mLikelyPlaceBusinessStatus = new String[count];
                    mLikelyPlaceRating = new String[count];
                    mLikelyPlacePhone = new String[count];

                    Place.Type supermarketCheck = Place.Type.SUPERMARKET;
                    Place.Type storeCheck = Place.Type.STORE;

                    // Parcours des differents lieux trouvés
                    for (PlaceLikelihood placeLikelihood : likelyPlaces.getPlaceLikelihoods()) {

//                       if(placeLikelihood.getPlace().getTypes().contains(supermarketCheck) || placeLikelihood.getPlace().getTypes().contains(storeCheck)) {
                           /*List<Place.Type> typesP = placeLikelihood.getPlace().getTypes();
                           Log.d(TAG, "Type : " + typesP);
                           Log.d(TAG, "Nom : " + placeLikelihood.getPlace().getName());
                           Log.d(TAG, "Adresse :  " + placeLikelihood.getPlace().getAddress());
                           Log.d(TAG, "Attributions : " + placeLikelihood.getPlace().getAttributions());
                           Log.d(TAG, "LatLng :  " + placeLikelihood.getPlace().getLatLng());

                           Log.d(TAG, "phone :  " + placeLikelihood.getPlace().getPhoneNumber());
                           Log.d(TAG, "rating :  " + placeLikelihood.getPlace().getRating());
                           Log.d(TAG, "Website :  " + placeLikelihood.getPlace().getWebsiteUri());
                           Log.d(TAG, "Business Status :  " + placeLikelihood.getPlace().getBusinessStatus());
                           Log.d(TAG, "===============================");*/

                           // Construction des lieux dans leurs listes respectives.
                           mLikelyPlaceNames[i] = placeLikelihood.getPlace().getName();
                           mLikelyPlaceTypes[i] = placeLikelihood.getPlace().getTypes();
                           mLikelyPlaceAddresses[i] = placeLikelihood.getPlace().getAddress();
                           mLikelyPlaceAttributions[i] = placeLikelihood.getPlace().getAttributions();
                           mLikelyPlaceLatLngs[i] = placeLikelihood.getPlace().getLatLng();


                           mLikelyPlacePhone[i] = placeLikelihood.getPlace().getPhoneNumber();

                           if(placeLikelihood.getPlace().getOpeningHours() != null)
                               mLikelyPlaceOpHours[i] = placeLikelihood.getPlace().getOpeningHours().getWeekdayText();
                           if(placeLikelihood.getPlace().getRating() != null)
                               mLikelyPlaceRating[i] = String.valueOf(placeLikelihood.getPlace().getRating());
                           if(placeLikelihood.getPlace().getWebsiteUri() != null)
                               mLikelyPlaceWebsite[i] = placeLikelihood.getPlace().getWebsiteUri().toString();
                           if(placeLikelihood.getPlace().getBusinessStatus() != null) {
                               mLikelyPlaceBusinessStatus[i] = placeLikelihood.getPlace().getBusinessStatus().toString();
                               //String status[] = {"CLOSED_PERMANENTLY","CLOSED_TEMPORARILY","OPERATIONAL"};
                               switch (mLikelyPlaceBusinessStatus[i]) {
                                   case "CLOSED_PERMANENTLY":
                                       mLikelyPlaceBusinessStatus[i] = getString(R.string.CLOSED_PERMANENTLY);
                                       break;
                                   case "CLOSED_TEMPORARILY":
                                       mLikelyPlaceBusinessStatus[i] = getString(R.string.CLOSED_TEMPORARILY);
                                       break;
                                   case "OPERATIONAL":
                                       mLikelyPlaceBusinessStatus[i] = getString(R.string.OPERATIONAL);
                                       break;
                                   default:
                                       break;
                               }
                           }

                           // On remplit les informations collectées via la requête dans un tableau, afin de remplir notre listInfoMarker
                           String[] infos = {mLikelyPlaceNames[i], mLikelyPlaceAddresses[i],mLikelyPlacePhone[i],mLikelyPlaceRating[i],mLikelyPlaceWebsite[i],mLikelyPlaceBusinessStatus[i]};
                           listInfoMarker.put(mLikelyPlaceNames[i],new InfoMarker(infos,mLikelyPlaceOpHours[i]));


                            // Ajout des Markers des lieux sur la map.
                            MarkerOptions markerOptions = new MarkerOptions().position(mLikelyPlaceLatLngs[i]).title(mLikelyPlaceNames[i]).snippet(mLikelyPlaceAddresses[i]).icon(BitmapDescriptorFactory.fromResource(R.drawable.supermarket));
                            mMap.addMarker(markerOptions);
                            mMap.setOnMarkerClickListener(this);
                            i++;
                            if (i > (count - 1)) {
                                break;
                            }
//                       } // endif supermarket
                    }
                }
                else {
                    Log.e(TAG, "Exception: %s", task.getException());
                }
            });
        }
        else {
            Log.i(TAG, "L'utilisateur n'a pas donné la permission de localisation");
            // Ajout de secours si l'utilisateur n'a pas donné la permission.
            mMap.addMarker(new MarkerOptions()
                    .title(getString(R.string.default_info_title))
                    .position(mDefaultLocation)
                    .snippet(getString(R.string.default_info_snippet)));
            getLocationPermission();
        }
    }

    // Fonction permettant l'apparition du bouton "+" donnant accès à l'ajout d'une liste de courses
    // quand l'utilisateur selectionne un supermarché.
    public boolean onMarkerClick(final Marker marker) {
        ImageButton ajouterButton = (ImageButton) findViewById(R.id.ajouter);
        ajouterButton.setVisibility(View.INVISIBLE);
        if(!marker.getTitle().equals("Votre position")) {
            ajouterButton.setVisibility(View.VISIBLE);
            storeName = marker.getTitle();
            Log.d(TAG, "" + marker.getTitle());
        }
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        Intent intent = new Intent(MapsActivity.this, InfoMarkerActivity.class);
        if(!marker.getTitle().equals("Votre position")) {
            intent.putExtra("titleMarker", marker.getTitle());
            intent.putExtra("listInfoMarker", listInfoMarker);
            startActivity(intent);
        }
    }

}

