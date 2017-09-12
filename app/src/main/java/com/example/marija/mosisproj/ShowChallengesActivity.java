package com.example.marija.mosisproj;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;

public class ShowChallengesActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference db;

    private double longitude;
    private double latitude;
    MarkerOptions centerOptions;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_challenges);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        gson=new Gson();

        latitude = Double.parseDouble(getIntent().getStringExtra("latitude"));
        longitude = Double.parseDouble(getIntent().getStringExtra("longitude"));


    }

    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        LatLng center = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 15));

        centerOptions = new MarkerOptions().position(center).title("My location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        mMap.addMarker(centerOptions);

        db = FirebaseDatabase.getInstance().getReference("challenge_questions");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mMap.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    for(DataSnapshot challenge : postSnapshot.getChildren()) {
                        //Getting the data from snapshot
                        //   ChalengeQuestion cq = challenge.getValue(ChalengeQuestion.class);

                        Object  x= challenge.getValue();
                        String json=gson.toJson(x);
                        ChalengeQuestion cq=gson.fromJson(json,ChalengeQuestion.class);


                        LatLng cqLatLng = new LatLng(Double.parseDouble(cq.getLat()), Double.parseDouble(cq.getLng()));

                        MarkerOptions cqOptions=new MarkerOptions().position(cqLatLng)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                        mMap.addMarker(cqOptions);


                        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                            @Override
                            public boolean onMarkerClick(Marker marker) {
                                // if marker source is clicked
                                //   Toast.makeText(MapsActivity.this, marker.getTitle(), Toast.LENGTH_SHORT).show();// display toast

                                Intent intent = new Intent(ShowChallengesActivity.this.getApplicationContext(), ChallengeQuestionActivity.class);
                                // String kljuc = markersMap.get(marker.getSnippet());
                                // intent1.putExtra("kljuc", kljuc);
                                startActivity(intent);
                                return true;


                            }
                        });

                    }
                }
                mMap.addMarker(centerOptions);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }


        });
    }
}