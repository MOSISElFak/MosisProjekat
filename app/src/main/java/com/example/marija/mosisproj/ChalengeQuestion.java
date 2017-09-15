package com.example.marija.mosisproj;

import android.icu.text.DateFormat;
import android.icu.util.Calendar;
import android.widget.DatePicker;

import java.util.Date;

/**
 * Created by Marija on 8/31/2017.
 */

public class ChalengeQuestion {
    public String tekst;
    public String tacanOdgovor;
    public Double lat;
    public Double lng;
    public String postDate;


    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getPostDate() {

        return postDate;
    }

    public ChalengeQuestion(String t, String to)
    {
        this.tekst=t;
        this.tacanOdgovor=to;
    }

    public Double getLng() {
        return lng;
    }

    public Double getLat() {

        return lat;
    }

    public void setLng(Double lng) {

        this.lng = lng;
    }

    public void setLat(Double lat) {

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
