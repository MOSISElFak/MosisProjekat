package com.example.marija.mosisproj;

import android.content.Intent;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;

public class AddSpotActivity extends AppCompatActivity {

    DatabaseReference dref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_spot);

        final EditText headerET = (EditText) findViewById(R.id.editTextHeader);
        final EditText descET = (EditText) findViewById(R.id.editTextDesc);
        final EditText latET = (EditText) findViewById(R.id.editTextLat);
        final EditText lonET= (EditText) findViewById(R.id.editTextLon);

        Button buttonMap = (Button) findViewById(R.id.buttonMap);
        Button buttonDodaj = (Button) findViewById(R.id.buttonDodaj);

        buttonDodaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //user = FirebaseAuth.getInstance().getCurrentUser();

                final DatabaseReference mDatabase;

                mDatabase = FirebaseDatabase.getInstance().getReference();

                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String header = headerET.getText().toString();
                        String desc = descET.getText().toString();
                        String lat=latET.getText().toString();
                        String lon=lonET.getText().toString();

                        Spot spot = new Spot();

                        spot.setHeader(header);
                        spot.setDesc(desc);
                        spot.setLatitude(Double.parseDouble(lat));
                        spot.setLongitude(Double.parseDouble(lon));

                        mDatabase.child("spot").push().setValue(spot);

                        Toast.makeText(AddSpotActivity.this, "Uspesno ste dodali spot", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(AddSpotActivity.this, MainActivity.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }) ;
            }
        });
            }
}