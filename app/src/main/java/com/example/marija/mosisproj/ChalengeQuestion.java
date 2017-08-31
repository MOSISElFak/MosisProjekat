package com.example.marija.mosisproj;

/**
 * Created by Marija on 8/31/2017.
 */

public class ChalengeQuestion {
    public String tekst;
    public String tacanOdgovor;
    public String lat;
    public String lng;


    public ChalengeQuestion(String t,String to)
    {
        this.tekst=t;
        this.tacanOdgovor=to;
    }

    public String getLng() {
        return lng;
    }

    public String getLat() {

        return lat;
    }

    public void setLng(String lng) {

        this.lng = lng;
    }

    public void setLat(String lat) {

        this.lat = lat;
    }

    public String getTekst() {
        return tekst;
    }

    public void setTekst(String tekst) {
        this.tekst = tekst;
    }

    public void setTacanOdgovor(String tacanOdgovor) {
        this.tacanOdgovor = tacanOdgovor;
    }

    public String getTacanOdgovor() {

        return tacanOdgovor;
    }
}
