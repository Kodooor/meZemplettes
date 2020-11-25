package com.m1info.mapsstart;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class InfoMarker implements Serializable {
    private String _titre;
    private String _address;
    private String _phone;
    private String _rating;
    private String _website;
    private String _businessStatus;
    private List<String> _opHours;


    public InfoMarker(String infos[], List<String> opHours) {
        _titre = infos[0];
        _address = infos[1];
        _phone = infos[2];
        _rating = infos[3];
        _website = infos[4];
        _businessStatus = infos[5];
        _opHours = opHours;
    }

    public String getTitre() {
        return _titre;
    }

    public String getAddress() {
        return _address;
    }

    public String getPhone() {
        return _phone;
    }

    public String getRating() {
        return _rating;
    }

    public String getWebsite() {
        return _website;
    }

    public String getBusinessStatus() {
        return _businessStatus;
    }

    public List<String> getOpHours(){
        return _opHours;
    }
}
