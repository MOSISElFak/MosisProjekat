package com.example.marija.mosisproj;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

    private SeekBar seek;
    private TextView value;
    private DatePicker date1;
    private DatePicker date2;
    private Button filterButton;
    double textViewValue;


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

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(ShowChallengesActivity.this);
                final View mView = getLayoutInflater().inflate(R.layout.dialog_filter, null);

                seek = (SeekBar) mView.findViewById(R.id.radius);
                value = (TextView) mView.findViewById(R.id.value);
                date1 = (DatePicker) mView.findViewById(R.id.datefrom);
                date2 = (DatePicker) mView.findViewById((R.id.dateto));
                filterButton = (Button) mView.findViewById(R.id.filterButton);

                if (tip.equals("1") || tip.equals("2"))
                {
                    date1.setVisibility(View.GONE);
                    date2.setVisibility(View.GONE);

                }


                mBuilder.setView(mView);
                final AlertDialog dialog=mBuilder.show();
                dialog.show();

                filterButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                       if(tip.equals("1"))
                           showUsers();
                        else if(tip.equals("2"))
                            showFriends();
                        else if(tip.equals("3"))
                            showQuestion();


                        dialog.cancel();

                    }
                });

                seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seek, int progress, boolean fromUser) {

                        value.setText(String.valueOf(progress));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seek) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seek) {

                    }
                });



            }
        });




    }

    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        LatLng center = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 15));

        centerOptions = new MarkerOptions().position(center).title("My location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        mMap.addMarker(centerOptions);



        if (tip.equals("3"))
            showQuestion();
        else if (tip.equals("2"))
            showFriends();
        else if (tip.equals("1"))
            showUsers();
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
    private void showUsers()
    {
        if(value!=null)
            textViewValue = Double.parseDouble(value.getText().toString());
        else
            textViewValue=20000.0;

        db = FirebaseDatabase.getInstance().getReference("user");
        db.addValueEventListener(new ValueEventListener() {
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


                    tasklocation.setLatitude(k.getLatitude());
                    tasklocation.setLongitude(k.getLongitude());

                    double distance = location.distanceTo(tasklocation);


                    if (distance < textViewValue) {

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
    private void showFriends()
    {
        if(value!=null)
            textViewValue = Double.parseDouble(value.getText().toString());
        else
            textViewValue=20000.0;

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


                        tasklocation.setLatitude(k.getLatitude());
                        tasklocation.setLongitude(k.getLongitude());

                        double distance = location.distanceTo(tasklocation);


                        if (distance < textViewValue) {

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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });



    }
    private void showQuestion()
    {
        if(value!=null)
            textViewValue = Double.parseDouble(value.getText().toString());
        else
            textViewValue=20000.0;

        db = FirebaseDatabase.getInstance().getReference("challenge_questions");
        db.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
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

                        tasklocation.setLatitude(cq.getLat());
                        tasklocation.setLongitude(cq.getLng());

                        double distance = location.distanceTo(tasklocation);

                        ; // suppose you create this type of date as string then
                        Date from = new Date();
                        Date date = new Date();
                        Date to = new Date();


                        if (date1 != null && date2 != null) {

                            int day1 = date1.getDayOfMonth();
                            int month1 = date1.getMonth();
                            int year1 = date1.getYear();

                            SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
                            String formatedDate1 = sdf1.format(new Date(day1, month1, year1));

                            int day2 = date2.getDayOfMonth();
                            int month2 = date2.getMonth();
                            int year2 = date2.getYear();

                            SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy");
                            String formatedDate2 = sdf2.format(new Date(day2, month2, year2));

                            String[] dateString = cq.getPostDate().split("-");

                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                            String formatedDate = sdf.format(new Date(Integer.parseInt(dateString[0]), Integer.parseInt(dateString[1]), Integer.parseInt(dateString[2])));


                            try {
                                date = sdf.parse(formatedDate);
                                from = sdf1.parse(formatedDate1);
                                to = sdf2.parse(formatedDate2);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }



                        if(distance<textViewValue  && date.compareTo(from)>=0 && date.compareTo(to)<=0) {

                            LatLng cqLatLng = new LatLng(cq.getLat(), cq.getLng());

                            MarkerOptions cqOptions = new MarkerOptions()
                                    .position(cqLatLng)
                                    .title(cq.getTekst())
                                    .snippet(cq.getLat() + "," + cq.getLng())
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));

                            Marker marker = mMap.addMarker(cqOptions);


                            mHashMap.put(marker, cq.getTacanOdgovor());
                        }

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
                                // distance<pom

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

    }

}