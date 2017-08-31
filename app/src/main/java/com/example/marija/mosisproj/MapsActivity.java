package com.example.marija.mosisproj;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marija.mosisproj.DirectionFinder;
import com.example.marija.mosisproj.DirectionFinderListener;
import com.example.marija.mosisproj.MainActivity;
import com.example.marija.mosisproj.Route;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.concurrent.Callable;

import static android.R.attr.value;
import static android.R.id.list;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, DirectionFinderListener {

    private GoogleMap mMap;
    private Button btnFindPath;
    private Button btnShowFriend;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private double latitude;
    private double longitude;
    private DatabaseReference db;
    private EditText editTextDistance;

    private StorageReference storageRef;
    private FirebaseStorage storage;
    private File localFile;

    private double dlatitude;
    private double dlongitude;

    private Location tasklocation;
    private Location location;
    private static final String TAG = MainActivity.class.getSimpleName();
    private List<Korisnik> korisnici;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private HashMap<String, String> markersMap;
    private int ipom;
    private BitmapDescriptor icon;
    private DownloadThread d;

    BroadcastReceiver receiver;
    String GPS_FILTER = "com.example.marija.mylocationtracker.LOCATION";

    private class MyMainLocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            latitude = intent.getDoubleExtra("latitude", -1);
            longitude = intent.getDoubleExtra("longitude", -1);
            /*EditText lon = (EditText) findViewById(R.id.lon);
            EditText lat = (EditText) findViewById(R.id.lat);
            lon.setText(String.valueOf(longitude));
            lat.setText(String.valueOf(latitude));*/
            Toast.makeText(getApplicationContext(), "Maps" + String.valueOf(latitude) + " " + String.valueOf(longitude), Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mAuth=FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };


        btnFindPath = (Button) findViewById(R.id.btnFindPath);
        btnShowFriend = (Button) findViewById(R.id.btnShowUsers);
        editTextDistance = (EditText) findViewById(R.id.editTextDistance);

        localFile = null;

        tasklocation = new Location(LocationManager.GPS_PROVIDER);
        location = new Location(LocationManager.GPS_PROVIDER);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });
        btnShowFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location.setLongitude(longitude);
                location.setLatitude(latitude);
                showFriends();
            }
        });

        IntentFilter mainFilter = new IntentFilter(GPS_FILTER);
        receiver = new MyMainLocalReceiver();
        registerReceiver(receiver, mainFilter);


    }

    private void showFriends() {


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



    private void sendRequest() {
        if (latitude != 0 && longitude != 0) {

            Intent i = getIntent();
            dlatitude = Double.parseDouble(getIntent().getStringExtra("latitude"));
            dlongitude = Double.parseDouble(getIntent().getStringExtra("longitude"));

            String origin = Double.toString(latitude) + "," + Double.toString(longitude);
            String destination = Double.toString(dlatitude) + "," + Double.toString(dlongitude);

            try {
                new DirectionFinder(this, origin, destination).execute();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        dlatitude = Double.parseDouble(getIntent().getStringExtra("latitude"));
        dlongitude = Double.parseDouble(getIntent().getStringExtra("longitude"));

        LatLng hcmus = new LatLng(dlatitude, dlongitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hcmus, 15));


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);


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


    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Molim sačekajte",
                "Pretraživanje putanje...", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            //((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }


    public class DownloadThread extends Thread {
        public String url;
        public Korisnik k;

        public DownloadThread(String url, Korisnik k) {
            this.url = url;
            this.k=k;
        }

        @Override
        public void run() {
            super.run();

                File f=null;
                StorageReference storageReference=storageRef.child(url + ".jpg");
                try {
                    f = File.createTempFile(url, "jpg");
                }
                catch (Exception e){

                }
                final File f2=f;

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

                        LatLng position=new LatLng(k.getLatitude(),k.getLongitude());

                        MarkerOptions markerOptions = new MarkerOptions().position(position)
                                .title(k.getFirstname() + ' ' + k.getLastname())
                                .snippet(k.getEmail())
                                .icon(icon);



                        Marker mMarker = mMap.addMarker(markerOptions);

                        // onMarkerClick

                        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
                        {

                            @Override
                            public boolean onMarkerClick(Marker marker) {
                                // if(arg0.getTitle().equals(k.getFirstname() + ' ' + k.getLastname())) // if marker source is clicked
                             //   Toast.makeText(MapsActivity.this, marker.getTitle(), Toast.LENGTH_SHORT).show();// display toast

                               Intent intent1 = new Intent(getApplicationContext(), ProfileActivity.class);
                                //String title = marker.getTitle();
                                //intent1.putExtra("markertitle", title);
                                startActivity(intent1);
                                return true;
                            }
                        });

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
