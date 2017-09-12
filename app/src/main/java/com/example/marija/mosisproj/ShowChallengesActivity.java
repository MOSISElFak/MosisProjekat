package com.example.marija.mosisproj;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class ShowChallengesActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private HashMap<String, String> markersMap;
    private DatabaseReference db;

    private double longitude;
    private double latitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_challenges);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        latitude = Double.parseDouble(getIntent().getStringExtra("latitude"));
        longitude = Double.parseDouble(getIntent().getStringExtra("longitude"));


    }

    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        LatLng center = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 15));

        MarkerOptions centerOptions = new MarkerOptions().position(center).title("My location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        mMap.addMarker(centerOptions);

        db = FirebaseDatabase.getInstance().getReference("challenge_questions");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mMap.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    for(DataSnapshot challenge : postSnapshot.getChildren()) {
                        //Getting the data from snapshot

                        ChalengeQuestion cq = challenge.getValue(ChalengeQuestion.class);


                        final String key = postSnapshot.getKey();
                        if (markersMap == null)
                            markersMap = new HashMap<String, String>();
                        markersMap.put(cq.getTekst(), key);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }


        });
    }
}