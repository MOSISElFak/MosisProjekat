package com.example.marija.mosisproj;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShowChallengesActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference db;

    private double longitude;
    private double latitude;
    private FloatingActionButton filter;
    private String tip;
    MarkerOptions centerOptions;
    private Gson gson;
    private ChalengeQuestion cq;

    private Location tasklocation;
    private Location location;

    private StorageReference storageRef;
    private FirebaseStorage storage;

    private HashMap<String, String> markersMap;
    private HashMap<Marker, String> mHashMap ;
    private BitmapDescriptor icon;
    private DownloadThread d;

    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_challenges);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        gson = new Gson();

        latitude = Double.parseDouble(getIntent().getStringExtra("latitude"));
        longitude = Double.parseDouble(getIntent().getStringExtra("longitude"));
        tip = getIntent().getStringExtra("tip");

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();


         tasklocation = new Location(LocationManager.GPS_PROVIDER);
         location = new Location(LocationManager.GPS_PROVIDER);
         location.setLongitude(longitude);
         location.setLatitude(latitude);

        filter = (FloatingActionButton) findViewById(R.id.floatingActionButton);

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder=new AlertDialog.Builder(ShowChallengesActivity.this);
                View mView=getLayoutInflater().inflate(R.layout.dialog_filter,null);


                mBuilder.setView(mView);
                AlertDialog dialog=mBuilder.show();
                dialog.show();
            }
        });


       /* btnShowFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showFriends();
            }
        });*/



    }

    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        LatLng center = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 15));

        centerOptions = new MarkerOptions().position(center).title("My location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        mMap.addMarker(centerOptions);

        if (tip.equals("3")) {

            db = FirebaseDatabase.getInstance().getReference("challenge_questions");
            db.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mMap.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        mHashMap = new HashMap<Marker, String>();
                        for (DataSnapshot challenge : postSnapshot.getChildren()) {
                            //Getting the data from snapshot

                            Object x = challenge.getValue();
                            String json = gson.toJson(x);
                            cq = gson.fromJson(json, ChalengeQuestion.class);

                            LatLng cqLatLng = new LatLng(Double.parseDouble(cq.getLat()), Double.parseDouble(cq.getLng()));

                            MarkerOptions cqOptions = new MarkerOptions()
                                    .position(cqLatLng)
                                    .title(cq.getTekst())
                                    .snippet(cq.getLat() + "," + cq.getLng())
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));

                            Marker marker = mMap.addMarker(cqOptions);


                            mHashMap.put(marker, cq.getTacanOdgovor());

                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                                @Override
                                public boolean onMarkerClick(Marker marker) {
                                    marker.showInfoWindow();
                                    return true;

                                }
                            });

                            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                                @Override
                                public void onInfoWindowClick(Marker marker) {

                                    String[] latlng = marker.getSnippet().split(",");

                                    tasklocation.setLatitude(Double.parseDouble(latlng[0]));
                                    tasklocation.setLongitude(Double.parseDouble(latlng[1]));

                                    double distance = location.distanceTo(tasklocation);

                                    if (distance < 50.0) {

                                        Intent intent = new Intent(ShowChallengesActivity.this.getApplicationContext(), ChallengeQuestionActivity.class);
                                        String pitanje = marker.getTitle();
                                        String tacanOdgovor = mHashMap.get(marker);
                                        intent.putExtra("pitanje", pitanje);
                                        intent.putExtra("tacanOdgovor", tacanOdgovor);
                                        startActivity(intent);

                                    } else {
                                        Toast toast = Toast.makeText(ShowChallengesActivity.this,
                                                "Ne mozete otvoriti pitanje niste dovoljno blizu, priblizite se jos " + String.valueOf(distance - 10.0) + "m",
                                                Toast.LENGTH_SHORT);
                                        toast.show();
                                    }

                                }
                            });
                        }
                    }
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }else if (tip.equals("2")) {

            user = FirebaseAuth.getInstance().getCurrentUser();

            db = FirebaseDatabase.getInstance().getReference("user");
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    //Getting the data from snapshot
                    Korisnik u = dataSnapshot.child(user.getUid()).getValue(Korisnik.class);

                    List<String> keys = u.friends;


                    if (keys != null) {

                        for (int i = 0; i < keys.size(); i++) {

                            Korisnik k = dataSnapshot.child(keys.get(i)).getValue(Korisnik.class);

                            if (markersMap == null)
                                markersMap = new HashMap<String, String>();
                            markersMap.put(k.getEmail(), keys.get(i));


                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                                @Override
                                public boolean onMarkerClick(Marker marker) {

                                    marker.showInfoWindow();
                                    return true;


                                }
                            });
                            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                                @Override
                                public void onInfoWindowClick(Marker marker) {

                                    Intent intent1 = new Intent(ShowChallengesActivity.this.getApplicationContext(), UsersProfileActivity.class);
                                    String kljuc = markersMap.get(marker.getSnippet());
                                    intent1.putExtra("kljuc", kljuc);
                                    startActivity(intent1);

                                }

                            });


                            String s = markersMap.get(k.getEmail());
                            d = new DownloadThread(s, k);
                            d.start();


                            try {
                                d.join();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });



        } else if (tip.equals("1")) {
            db = FirebaseDatabase.getInstance().getReference("user");
            db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mMap.clear();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            //Getting the data from snapshot
                            Korisnik k = postSnapshot.getValue(Korisnik.class);

                            final String key = postSnapshot.getKey();
                            if (markersMap == null)
                                markersMap = new HashMap<String, String>();
                            markersMap.put(k.getEmail(), key);


                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                                @Override
                                public boolean onMarkerClick(Marker marker) {

                                    marker.showInfoWindow();
                                    return true;


                                }
                            });
                            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

                                @Override
                                public void onInfoWindowClick(Marker marker) {

                                    Intent intent1 = new Intent(ShowChallengesActivity.this.getApplicationContext(), UsersProfileActivity.class);
                                    String kljuc = markersMap.get(marker.getSnippet());
                                    intent1.putExtra("kljuc", kljuc);
                                    startActivity(intent1);

                                }

                            });


                            String s = markersMap.get(k.getEmail());
                            d = new DownloadThread(s, k);
                            d.start();


                            try {
                                d.join();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }


                    }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }


    public class DownloadThread extends Thread {
        public String url;
        public Korisnik k;

        public DownloadThread(String url, Korisnik k) {
            this.url = url;
            this.k = k;
        }

        @Override
        public void run() {
            super.run();

            File f = null;
            StorageReference storageReference = storageRef.child(url + ".jpg");
            try {
                f = File.createTempFile(url, "jpg");
            } catch (Exception e) {

            }
            final File f2 = f;

            storageReference.getFile(f).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created

                    Bitmap myBitmap = BitmapFactory.decodeFile(f2.getAbsolutePath());
                    int width = myBitmap.getWidth();
                    int height = myBitmap.getHeight();

                    int maxWidth = 200;
                    int maxHeight = 200;

                    Log.v("Pictures", "Width and height are " + width + "--" + height);

                    if (width > height) {
                        // landscape
                        float ratio = (float) width / maxWidth;
                        width = maxWidth;
                        height = (int) (height / ratio);
                    } else if (height > width) {
                        // portrait
                        float ratio = (float) height / maxHeight;
                        height = maxHeight;
                        width = (int) (width / ratio);
                    } else {
                        // square
                        height = maxHeight;
                        width = maxWidth;
                    }

                    Bitmap myScaledBitmap = Bitmap.createScaledBitmap(myBitmap, width, height, false);
                    icon = BitmapDescriptorFactory.fromBitmap(myScaledBitmap);

                    LatLng position = new LatLng(k.getLatitude(), k.getLongitude());

                    MarkerOptions markerOptions = new MarkerOptions().position(position)
                            .title(k.getFirstname() + ' ' + k.getLastname())
                            .snippet(k.getEmail())
                            .icon(icon);


                    Marker mMarker = mMap.addMarker(markerOptions);


                }


            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }

    }
}

/*    private void showFriends() {

        db = FirebaseDatabase.getInstance().getReference("user");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mMap.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //Getting the data from snapshot
                    Korisnik k = postSnapshot.getValue(Korisnik.class);

                    final String key = postSnapshot.getKey();
                    if (markersMap == null)
                        markersMap = new HashMap<String, String>();
                    markersMap.put(k.getEmail(), key);

                    tasklocation.setLatitude(k.getLatitude());
                    tasklocation.setLongitude(k.getLongitude());

                    double distance = location.distanceTo(tasklocation);

                    double pom = Double.parseDouble(editTextDistance.getText().toString());

                    if (distance < pom) {

                        String s = markersMap.get(k.getEmail());
                        d = new DownloadThread(s, k);
                        d.start();

                    }

                    try {
                        d.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
*/