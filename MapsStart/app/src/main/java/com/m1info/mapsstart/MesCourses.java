package com.m1info.mapsstart;

// Notre classe MesCourses, représentant notre Entité "MesCourses" et ses attributs (idCourses, nomMagasin, nomProduit, rayon)
public class MesCourses {

    private int idCourses;
    private String nomMagasin;
    private String nomProduit;
    private String rayon;

    // Constructeur
    public MesCourses(int idCourses, String nomMagasin, String nomProduit, String rayon) {
        this.idCourses=idCourses;
        this.nomMagasin=nomMagasin;
        this.nomProduit=nomProduit;
        this.rayon = rayon;
    }

    // Getteurs / setteurs
    public int getIdCourses() {
        return idCourses;
    }

    public void setIdCourses(int id) {
        this.idCourses = id;
    }

    public String getNomMagasin() {
        return nomMagasin;
    }

    public void setNomMagasin(String nomMagasin) {
        this.nomMagasin = nomMagasin;
    }

    public String getNomProduit() {
        return nomProduit;
    }

    public void setNomProduit(String nomProduit) {
        this.nomProduit = nomProduit;
    }

    public String getRayon(){return rayon;}

    public void setRayon(String rayon){this.rayon = rayon;}
}

