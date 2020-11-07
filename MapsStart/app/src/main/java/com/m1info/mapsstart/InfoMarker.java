package com.m1info.mapsstart;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.List;

public class InfoMarker implements Serializable {
    private String titre;
    private List type;
    private String address;
    private String _phone;
    private LatLng localisation;

    public InfoMarker(String placeName, List placeType, String placeAddress, LatLng placeLatLng) {
        titre = placeName;
        type = placeType;
        address = placeAddress;
        localisation = placeLatLng;
    }

    public InfoMarker(String placeName, List placeType, String placeAddress) {
        titre = placeName;
        type = placeType;
        address = placeAddress;
    }
    public InfoMarker(String placeName, List placeType, String placeAddress, String phone) {
        titre = placeName;
        type = placeType;
        address = placeAddress;
        _phone = phone;
    }

    public InfoMarker(String placeName) {
        titre = placeName;
    }


    public String getTitre() {
        return titre;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return _phone;
    }

    public List getType() {
        return type;
    }

    public LatLng getLocalisation() {
        return localisation;
    }
}
