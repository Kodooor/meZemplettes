package com.m1info.mapsstart;

public class MesMarkers {

    private int idMarker;
    private String nomMagasin;
    private String latMarker;
    private String lngMarker;
    private String adresse;

    // Constructeur
    public MesMarkers(int idMarker, String nomMagasin, String latMarker, String lngMarker, String adresse) {
        this.idMarker=idMarker;
        this.nomMagasin=nomMagasin;
        this.latMarker=latMarker;
        this.lngMarker = lngMarker;
        this.adresse = adresse;
    }

    public int getIdMarker() {
        return idMarker;
    }

    public void setIdMarker(int id) {
        this.idMarker = id;
    }

    public String getNomMagasin() {
        return nomMagasin;
    }

    public void setNomMagasin(String nomMagasin) {
        this.nomMagasin = nomMagasin;
    }

    public String getLatMarker() {
        return latMarker;
    }

    public void setLatMarker(String latMarker) {
        this.latMarker = latMarker;
    }

    public String getLngMarker(){return lngMarker;}

    public void setLngMarker(String lngMarker){this.lngMarker = lngMarker;}

    public String getAdresse(){return adresse;}

    public void setAdresse(String adresse){this.adresse = adresse;}
}

