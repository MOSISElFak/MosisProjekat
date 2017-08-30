package com.example.marija.mosisproj;

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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static com.example.marija.mosisproj.R.id.editTextName;
import static com.example.marija.mosisproj.R.id.fab;

public class ProfileActivity extends AppCompatActivity {

   // private static final String TAG = MainActivity.class.getSimpleName();
    private DatabaseReference dref;
    private FirebaseUser user;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        user = FirebaseAuth.getInstance().getCurrentUser();

        if(user!=null) {



            dref=FirebaseDatabase.getInstance().getReference("user").child(user.getUid());
            dref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(final DataSnapshot dataSnapshot) {
                                Korisnik t=dataSnapshot.getValue(Korisnik.class);
                                TextView Name=(TextView) findViewById(R.id.textViewName);
                                Name.setText(t.firstname);
                                TextView LastName=(TextView) findViewById(R.id.textViewLastName);
                                LastName.setText(t.lastname);
                                TextView Email=(TextView) findViewById(R.id.textViewEmail);
                                Email.setText(t.email);
                                TextView PhoneNumber=(TextView) findViewById(R.id.textViewPhone);
                                PhoneNumber.setText(t.phonenumber);

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
                                        ImageView image=(ImageView) findViewById(R.id.profile_picture);
                                        image.setImageBitmap(myBitmap);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle any errors
                                    }
                                });

                               //          GOTOVO

                             /*   ImageView imageView = (ImageView) findViewById(R.id.imageView);
                                imageView.setDrawingCacheEnabled(true);
                                imageView.buildDrawingCache();
                                Bitmap bitmap = imageView.getDrawingCache();
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                byte[] data = baos.toByteArray();

                                UploadTask uploadTask = sRef.putBytes(data);
                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle unsuccessful uploads
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                        @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                    }
                                });*/


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
