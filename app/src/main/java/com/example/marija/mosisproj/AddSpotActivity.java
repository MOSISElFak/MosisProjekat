package com.example.marija.mosisproj;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;

import static android.R.attr.data;
import static com.example.marija.mosisproj.R.id.map;

public class AddSpotActivity extends AppCompatActivity {

    DatabaseReference dref;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private Uri selectedImage;
    private ImageView imageView;
    private static int RESULT_LOAD_IMAGE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_spot);

        final EditText headerET = (EditText) findViewById(R.id.editTextHeader);
        final EditText descET = (EditText) findViewById(R.id.editTextDesc);
        final EditText latET = (EditText) findViewById(R.id.editTextLat);
        final EditText lonET= (EditText) findViewById(R.id.editTextLon);

  //      img1=(ImageView)findViewById(R.id.imageView2);
    //    final ImageView img2=(ImageView)findViewById(R.id.imageView3);
        imageView = (ImageView) findViewById(R.id.imageView2);



        final Button buttonMap = (Button) findViewById(R.id.buttonMap);
        final Button buttonDodaj = (Button) findViewById(R.id.buttonDodaj);
        final Button buttonIzaberiSlike=(Button)findViewById(R.id.buttonSelectPhotos);

        buttonIzaberiSlike.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

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

                        //Dodavanje spota

                        String header = headerET.getText().toString();
                        String desc = descET.getText().toString();
                        String lat=latET.getText().toString();
                        String lon=lonET.getText().toString();

                        Spot spot = new Spot();

                        spot.setHeader(header);
                        spot.setDesc(desc);
                        spot.setLatitude(Double.parseDouble(lat));
                        spot.setLongitude(Double.parseDouble(lon));

                        final String keyID=mDatabase.child("spot").push().getKey();
                        mDatabase.child("spot").child(keyID).setValue(spot);

                        Toast.makeText(AddSpotActivity.this, "Uspesno ste dodali spot", Toast.LENGTH_SHORT).show();

                        storage = FirebaseStorage.getInstance();
                        storageRef = storage.getReference();
                        StorageReference imageRef = storageRef.child(keyID + "1.jpg");


                    //    ImageView imageView = (ImageView) findViewById(R.id.imageView2);
                        //imageView.setDrawingCacheEnabled(true);

                        //Bitmap bitmap = imageView.getDrawingCache();
                        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                        Bitmap bitmap = drawable.getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG,50, baos);

                        int i=baos.toByteArray().length;
                        byte[] data = baos.toByteArray();

                        UploadTask uploadTask = imageRef.putBytes(data);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            }
                        });


                        startActivity(new Intent(AddSpotActivity.this, MainActivity.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }) ;
            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

                if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
                    selectedImage = data.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    imageView.setImageURI(selectedImage);

                }

            }
}