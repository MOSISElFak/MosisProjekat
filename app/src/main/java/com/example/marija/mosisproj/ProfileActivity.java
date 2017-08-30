package com.example.marija.mosisproj;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import static com.example.marija.mosisproj.R.id.editTextName;
import static com.example.marija.mosisproj.R.id.fab;

public class ProfileActivity extends AppCompatActivity {


    // mDatabase = FirebaseDatabase.getInstance().getReference();

    private DatabaseReference mDatabase;
    private static final String TAG = MainActivity.class.getSimpleName();
    private StorageReference storageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            DatabaseReference userRef = mDatabase.child("user").child(user.getUid());
            userRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(final DataSnapshot dataSnapshot) {
                                Korisnik t=dataSnapshot.getValue(Korisnik.class);
                                TextView Name=(TextView) findViewById(R.id.editTextName);
                                Name.setText(t.firstname);
                                TextView LastName=(TextView) findViewById(R.id.editTextLastName);
                                LastName.setText(t.lastname);
                                TextView Email=(TextView) findViewById(R.id.editTextEmail);
                                Email.setText(t.email);
                                TextView PhoneNumber=(TextView) findViewById(R.id.editTextPhone);
                                PhoneNumber.setText(t.phonenumber);
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                // Failed to read value
                                Log.w(TAG, "Failed to read value.", error.toException());
                            }
                        });
        }
     /* bb FloatingActionButton fab = (FloatingActionButton) findViewById(fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

}
