package com.example.marija.mosisproj;

import java.util.List;

/**
 * Created by Marija on 8/23/2017.
 */

public class Question {
    public String tip;
    public String tekst;
    public String ponudjeniOdg;
    public String tacniOdg;

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getTekst() {
        return tekst;
    }

    public void setTekst(String tekst) {
        this.tekst = tekst;
    }

    public String getPonudjeniOdg() {
        return ponudjeniOdg;
    }

    public void setPonudjeniOdg(String ponudjeniOdgovori) {
        this.ponudjeniOdg = ponudjeniOdgovori;
    }

    public String getTacniOdg() {
        return tacniOdg;
    }

    public void setTacniOdg(String tacniOdgovori) {
        this.tacniOdg = tacniOdgovori;
    }
}
