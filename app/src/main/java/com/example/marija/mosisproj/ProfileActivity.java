package com.example.marija.mosisproj;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static com.example.marija.mosisproj.R.id.editTextName;
import static com.example.marija.mosisproj.R.id.fab;

public class ProfileActivity extends AppCompatActivity {

   // private static final String TAG = MainActivity.class.getSimpleName();
    private DatabaseReference dref;
    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String markerTitle= intent.getExtras().getString("markertitle");


        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        if(user!=null) {

            String s=user.getUid();
            dref=FirebaseDatabase.getInstance().getReference("user").child(s);
            dref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(final DataSnapshot dataSnapshot) {

                                Korisnik t=dataSnapshot.getValue(Korisnik.class);

                                TextView Name=(TextView) findViewById(R.id.textViewName);
                                Name.setText(t.getFirstname());
                                TextView LastName=(TextView) findViewById(R.id.textViewLastName);
                                LastName.setText(t.getLastname());
                                TextView Email=(TextView) findViewById(R.id.textViewEmail);
                                Email.setText(t.getEmail());
                                TextView PhoneNumber=(TextView) findViewById(R.id.textViewPhone);
                                PhoneNumber.setText(t.getPhonenumber());

                                // ZA UCITAVANJE SLIKE U IMAGE_VIEW

                                String userID=user.getUid();

                                storage = FirebaseStorage.getInstance();
                                storageRef = storage.getReference();

                                StorageReference sRef = storageRef.child(userID+".jpg");


                                 File localFile=null;
                                try {
                                    localFile = File.createTempFile(userID, "jpg");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                final File localFile2=localFile;

                                sRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        // Local temp file has been created
                                        Bitmap myBitmap = BitmapFactory.decodeFile(localFile2.getAbsolutePath());
                                        ImageView image=(ImageView) findViewById(R.id.imageView);
                                        image.setImageBitmap(myBitmap);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle any errors
                                    }
                                });

                               //          GOTOVO

                            }
                            @Override
                            public void onCancelled(DatabaseError error) {
                                // Failed to read value
                                Log.w("Failed to read value.", error.toException());
                            }
                        });
        }
     /*  FloatingActionButton fab = (FloatingActionButton) findViewById(fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }
}
