package com.example.marija.mosisproj;

import java.util.List;

/**
 * Created by Marija on 8/16/2017.
 */

class Korisnik {
    public String email;
    public String firstname;
    public String lastname;
    public double latitude;
    public double longitude;
    public String phonenumber;
    public String places;
    public Integer score;
    public List<String> friends;


    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public List<String> getFriends() {
        return friends;
    }




    public void setPlaces(String places) {
        this.places = places;
    }

    public String getPlaces() {

        return places;
    }


    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public Integer getScore() {
        return score;
    }

//    public List<String> getFriends() {
//        return friends;
//    }

    //   public String getPicture() {
    //       return picture;
    //   }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    //   public void setFriends(List<String> friends) {
    //       this.friends = friends;
//    }

//    public void addFriend(String friend){
//        this.friends.add(friend);
//    }

    //   public void setPicture(String picture) {
//        this.picture = picture;
//    }
    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getEmail() {
        return email;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}