package com.example.marija.mosisproj;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    DatabaseReference dref;
    ListView listview;
    MyPlacesAdapter adapter;
    List<Spot> list;
    private HashMap<Integer, String> markersMap;
    private Integer i=0;
    private ArrayList<String> imagekey;


    Intent intentMyService;
    ComponentName service;
    BroadcastReceiver receiver;
    String GPS_FILTER = "com.example.marija.mylocationtracker.LOCATION";
    FloatingActionButton fab;

    private double latitude;
    private double longitude;

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab=(FloatingActionButton)findViewById(R.id.fab);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        mAuth= FirebaseAuth.getInstance();
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

        fab.setOnClickListener(new View.OnClickListener(){
            public void onClick (View v)
            {
                Intent intent1 = new Intent(MainActivity.this.getApplicationContext(), AddSpotActivity.class);
               // intent1.putExtra("latitude",Double.toString(latitude));
              //  intent1.putExtra("longitude",Double.toString(longitude));
                startActivity(intent1);
            }
        });


   /*     btnFindPath = (FloatingActionButton) findViewById(R.id.floatingActionButton2);
        btnShowFriend = (Button) findViewById(R.id.btnShowUsers);
        editTextDistance = (EditText) findViewById(R.id.editTextDistance);

        localFile = null;

        tasklocation = new Location(LocationManager.GPS_PROVIDER);
        location = new Location(LocationManager.GPS_PROVIDER);
        location.setLongitude(longitude);
        location.setLatitude(latitude);*/

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        try {
            GetUserData();
        }
        catch (Exception e){
            System.out.print(e.toString());
        }


        if(!runtimePermisions()){
            startService(new Intent(this,MyService.class));
        }

        //Location Service start
        intentMyService = new Intent(this, MyService.class);
        service = startService(intentMyService);

        IntentFilter mainFilter = new IntentFilter(GPS_FILTER);
        receiver = new MyMainLocalReceiver();
        registerReceiver(receiver, mainFilter);
        //Location Service end

        addPlaces();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Intent intent1 = new Intent(MainActivity.this.getApplicationContext(), ShowChallengesActivity.class);
        intent1.putExtra("latitude",Double.toString(latitude));
        intent1.putExtra("longitude",Double.toString(longitude));

        //noinspection SimplifiableIfStatement
        if (id == R.id.showAllUsers) {
            intent1.putExtra("tip","1");

        }
        else if(id == R.id.showFriends)
        {
            intent1.putExtra("tip","2");

        }
        else if(id == R.id.showChallenge)
        {

            intent1.putExtra("tip","3");


        }

        startActivity(intent1);

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            Intent openRang=new Intent(MainActivity.this.getApplicationContext(),RangList.class);
            startActivity(openRang);
        } else if (id == R.id.nav_gallery) {

            Intent profile=new Intent(MainActivity.this.getApplicationContext(),ProfileActivity.class);
            startActivity(profile);

        }  else if (id == R.id.nav_share) {

            startService(new Intent(MainActivity.this.getApplicationContext(), MyService.class));
            Toast toast = Toast.makeText(getApplicationContext(), "Uključili ste notifikacije!", Toast.LENGTH_SHORT);
            toast.show();


        } else if (id == R.id.nav_send) {

            stopService(new Intent(MainActivity.this.getApplicationContext(), MyService.class));

            Toast toast = Toast.makeText(getApplicationContext(), "Isključili ste notifikacije!", Toast.LENGTH_SHORT);
            toast.show();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean runtimePermisions(){
        if(Build.VERSION.SDK_INT>=23 && ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CAMERA},100);
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==100){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED &&
                    grantResults[2] == PackageManager.PERMISSION_GRANTED)
            {
                Context context = getApplicationContext();
                CharSequence text = "Super";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
            else
            {
                Context context = getApplicationContext();
                CharSequence text = "O ne! Da bi aplikacija funkcionisla potrebno je da omogućite GPS";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                runtimePermisions();
            }


        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void GetUserData() throws IOException
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String userName = user.getDisplayName();
            View headerView = navigationView.getHeaderView(0);
            TextView navUsername = (TextView) headerView.findViewById(R.id.userInfo);
            navUsername.setText(userName);

            String userEmail = user.getEmail();
            View headerView1 = navigationView.getHeaderView(0);
            TextView navTextView = (TextView) headerView.findViewById(R.id.textView);
            navTextView.setText(userEmail);

            String userID=user.getUid();
            StorageReference storageReference = storageRef.child(userID+".jpg");


            final File localFile = File.createTempFile(userID, "jpg");

            storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created
                    Bitmap myBitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    ImageView image=(ImageView) findViewById(R.id.profile_picture);
                    image.setImageBitmap(myBitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }
    }

    private void addPlaces(){

        listview=(ListView)findViewById(R.id.places);
        list= new ArrayList<>();
        imagekey=new ArrayList<>();


        adapter=new MyPlacesAdapter(getApplicationContext(),list,imagekey);

        dref = FirebaseDatabase.getInstance().getReference("spot");
        dref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    //Getting the data from snapshot
                    Spot s = postSnapshot.getValue(Spot.class);

                    final String key = postSnapshot.getKey();
                    if (markersMap == null)
                        markersMap = new HashMap<Integer, String>();
                    markersMap.put(i, key);
                    imagekey.add(key+"1.jpg");
                    list.add(s);

                    listview.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    i++;

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

    });

        listview.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent spotInfo =new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                ArrayList<String> images=new ArrayList<String>();
                images.add(markersMap.get(position)+"1.jpg");
                images.add(markersMap.get(position)+"2.jpg");
                spotInfo.putExtra("images",images);
                spotInfo.putExtra("spot",markersMap.get(position).toString());
                startActivity(spotInfo);
            }
        });

    }

    private class MyMainLocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            latitude = intent.getDoubleExtra("latitude", -1);
            longitude = intent.getDoubleExtra("longitude", -1);
            Toast.makeText(getApplicationContext(), String.valueOf(latitude) +  " " + String.valueOf(longitude), Toast.LENGTH_LONG).show();
        }
    }

}
