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
    private float DEFAULT_ZOOM = 16;
    private LatLng mDefaultLocation ;
    private PlacesClient mPlacesClient;
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
        mDefaultLocation = new LatLng(47,1.9);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);




        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



    }

    public void onButtonClick(View view) {

        Intent intent=new Intent(MapsActivity.this,AjouterListeCourse.class);
        intent.putExtra("storeName", storeName);
        startActivityForResult(intent, 2);// Activity is started with requestCode 2
    }

    public void onSecondButtonClick(View view) {

        Intent intent=new Intent(MapsActivity.this,ListesCourse.class);
        startActivityForResult(intent, 2);// Activity is started with requestCode 2
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
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
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

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



    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        mLastKnownLocation = (Location) task.getResult();
                        Log.d(TAG,mLastKnownLocation.getLatitude()+" "+mLastKnownLocation.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(new LatLng(mLastKnownLocation.getLatitude(),
                                mLastKnownLocation.getLongitude())).title("Votre position").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
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






    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        updateLocationUI();

        // Get the current location of the device and set the position of the map.
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

            // Use the builder to create a FindCurrentPlaceRequest.
            FindCurrentPlaceRequest request =
                    FindCurrentPlaceRequest.newInstance(placeFields);



            // Get the likely places - that is, the businesses and other points of interest that
            // are the best match for the device's current location.
            String apiKey = "AIzaSyCAg3UDXwGUIvN8FPK8hk8IN8Db0JF6NIw";
            Places.initialize(getApplicationContext(), apiKey);
            PlacesClient mPlacesClient = Places.createClient(this);


            @SuppressWarnings("MissingPermission") final
            Task<FindCurrentPlaceResponse> placeResult =
                    mPlacesClient.findCurrentPlace(request);
            Log.d(TAG,request.toString());

            placeResult.addOnCompleteListener (task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    FindCurrentPlaceResponse likelyPlaces = task.getResult();



                    // Set the count, handling cases where less than 5 entries are returned.
                    Log.d(TAG, "Taille likelyPlaces : " + likelyPlaces.getPlaceLikelihoods().size());
                    Log.d(TAG, "===============================" );

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

                    for (PlaceLikelihood placeLikelihood : likelyPlaces.getPlaceLikelihoods()) {
                        List<Place.Type> typesP = placeLikelihood.getPlace().getTypes();

                       if(placeLikelihood.getPlace().getTypes().contains(supermarketCheck) || placeLikelihood.getPlace().getTypes().contains(storeCheck)) {
                            // Build a list of likely places to show the user.
                               Log.d(TAG, "Type : " + typesP);
                               Log.d(TAG, "Nom : " + placeLikelihood.getPlace().getName());
                               Log.d(TAG, "Adresse :  " + placeLikelihood.getPlace().getAddress());
                               Log.d(TAG, "Attributions : " + placeLikelihood.getPlace().getAttributions());
                               Log.d(TAG, "LatLng :  " + placeLikelihood.getPlace().getLatLng());
                               Log.d(TAG, "phone :  " + placeLikelihood.getPlace().getPhoneNumber());
                               Log.d(TAG, "rating :  " + placeLikelihood.getPlace().getRating());
                               Log.d(TAG, "Website :  " + placeLikelihood.getPlace().getWebsiteUri());
                               Log.d(TAG, "Business Status :  " + placeLikelihood.getPlace().getBusinessStatus());
                               Log.d(TAG, "===============================");
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


                           mMap.addMarker(new MarkerOptions().position(mLikelyPlaceLatLngs[i]).title(mLikelyPlaceNames[i]).snippet(mLikelyPlaceAddresses[i]));

                           mMap.addMarker(new MarkerOptions().position(mLikelyPlaceLatLngs[i]).title(mLikelyPlaceNames[i]).snippet("Supermarché au : " + mLikelyPlaceAddresses[i]));

                           // On remplit les informations collectées via la requête dans un tableau, afin de remplir notre listInfoMarker
                           String[] infos = {mLikelyPlaceNames[i], mLikelyPlaceAddresses[i],mLikelyPlacePhone[i],mLikelyPlaceRating[i],mLikelyPlaceWebsite[i],mLikelyPlaceBusinessStatus[i]};
                           listInfoMarker.put(mLikelyPlaceNames[i],new InfoMarker(infos,mLikelyPlaceOpHours[i]));

                            MarkerOptions markerOptions = new MarkerOptions().position(mLikelyPlaceLatLngs[i]).title(mLikelyPlaceNames[i]).snippet(mLikelyPlaceAddresses[i]);
                            mMap.addMarker(markerOptions);
                            mMap.setOnMarkerClickListener(this);
                            i++;
                            if (i > (count - 1)) {
                                break;
                            }
                       }

                    }

                    // Show a dialog offering the user the list of likely places, and add a
                    // marker at the selected place.
                }
                else {
                    Log.e(TAG, "Exception: %s", task.getException());
                }
            });
        } else {
            // The user has not granted permission.
            Log.i(TAG, "The user did not grant location permission.");

            // Add a default marker, because the user hasn't selected a place.
            mMap.addMarker(new MarkerOptions()
                    .title(getString(R.string.default_info_title))
                    .position(mDefaultLocation)
                    .snippet(getString(R.string.default_info_snippet)));

            // Prompt the user for permission.
            getLocationPermission();
        }
    }

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

//        Toast.makeText(this, "Info window clicked",
//                Toast.LENGTH_SHORT).show();
    }

}

