package com.example.marija.mosisproj;

import android.*;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private DatabaseReference mDatabase;

    Intent intentMyService;
    ComponentName service;
    BroadcastReceiver receiver;
    String GPS_FILTER = "com.example.marija.mylocationtracker.LOCATION";

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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

        linkLayouts();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

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

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

            startService(new Intent(MainActivity.this.getApplicationContext(), MyService.class));

        } else if (id == R.id.nav_send) {

            stopService(new Intent(MainActivity.this.getApplicationContext(), MyService.class));

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

    private void linkLayouts(){
        LinearLayout NiskaTvrdjava= (LinearLayout) findViewById(R.id.niska_tvrdjava);
        NiskaTvrdjava.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","niska_tvrdjava");
                startActivity(openInfo);
            }
        });

        LinearLayout KonstantinVeliki= (LinearLayout) findViewById(R.id.konstantin_veliki);
        KonstantinVeliki.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","konstantin_veliki");
                startActivity(openInfo);
            }
        });

        LinearLayout Medijana= (LinearLayout) findViewById(R.id.Medijana);
        Medijana.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","Medijana");
                startActivity(openInfo);
            }
        });

        LinearLayout CeleKula= (LinearLayout) findViewById(R.id.cele_kula);
        CeleKula.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","cele_kula");
                startActivity(openInfo);
            }
        });

        LinearLayout LatinskaCrkva= (LinearLayout) findViewById(R.id.latinska_crkva);
        LatinskaCrkva.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","latinska_crkva");
                startActivity(openInfo);
            }
        });

        LinearLayout CrkvaSN= (LinearLayout) findViewById(R.id.crkva_sveti_nikola);
        CrkvaSN.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","crkva_sveti_nikola");
                startActivity(openInfo);
            }
        });

        LinearLayout Sinagoga= (LinearLayout) findViewById(R.id.sinagoga);
        Sinagoga.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","sinagoga");
                startActivity(openInfo);
            }
        });

        LinearLayout ZgradaBanovine= (LinearLayout) findViewById(R.id.zgrada_banovine);
        ZgradaBanovine.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","zgrada_banovine");
                startActivity(openInfo);
            }
        });

        LinearLayout OficirskiDom= (LinearLayout) findViewById(R.id.oficirski_dom);
        OficirskiDom.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","oficirski_dom");
                startActivity(openInfo);
            }
        });

        LinearLayout CrkvaSvPant= (LinearLayout) findViewById(R.id.crkva_svetog_pantelejmona);
        CrkvaSvPant.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","crkva_svetog_pantelejmona");
                startActivity(openInfo);
            }
        });

        LinearLayout CrkvaSvAM= (LinearLayout) findViewById(R.id.crkva_svetog_arhangela_mihaila);
        CrkvaSvAM.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","crkva_svetog_arhangela_mihaila");
                startActivity(openInfo);
            }
        });

        LinearLayout SabornaCrkva= (LinearLayout) findViewById(R.id.saborna_crkva_svete_trojice);
        SabornaCrkva.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","saborna_crkva_svete_trojice");
                startActivity(openInfo);
            }
        });

        LinearLayout Bubanj= (LinearLayout) findViewById(R.id.spomen_kompleks_bubanj);
        Bubanj.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","spomen_kompleks_bubanj");
                startActivity(openInfo);
            }
        });

        LinearLayout Logor= (LinearLayout) findViewById(R.id.muzej_na_crvenom_krstu);
        Logor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","muzej_na_crvenom_krstu");
                startActivity(openInfo);
            }
        });

        LinearLayout Konj= (LinearLayout) findViewById(R.id.spomenik_oslobodiocima_nisa);
        Konj.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","spomenik_oslobodiocima_nisa");
                startActivity(openInfo);
            }
        });

        LinearLayout Hamam= (LinearLayout) findViewById(R.id.hamam);
        Hamam.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","cele_kula");
                startActivity(openInfo);
            }
        });

        LinearLayout DBJ= (LinearLayout) findViewById(R.id.dzamija_balije_jedrenca);
        DBJ.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","dzamija_balije_jedrenca");
                startActivity(openInfo);
            }
        });

        LinearLayout UlicaJI= (LinearLayout) findViewById(R.id.ulica_iz_doba_justinijana_prvog);
        UlicaJI.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","ulica_iz_doba_justinijana_prvog");
                startActivity(openInfo);
            }
        });

        LinearLayout Barutane= (LinearLayout) findViewById(R.id.barutane);
        Barutane.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","barutane");
                startActivity(openInfo);
            }
        });

        LinearLayout LP= (LinearLayout) findViewById(R.id.letnja_pozornica);
        LP.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","letnja_pozornica");
                startActivity(openInfo);
            }
        });

        LinearLayout RG= (LinearLayout) findViewById(R.id.ranovizantijska_grobnica);
        RG.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","ranovizantijska_grobnica");
                startActivity(openInfo);
            }
        });

        LinearLayout Pozoriste= (LinearLayout) findViewById(R.id.narodno_pozoriste);
        Pozoriste.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","narodno_pozoriste");
                startActivity(openInfo);
            }
        });

        LinearLayout CrkvaSV3G= (LinearLayout) findViewById(R.id.crkva_svete_trojice_u_gabrovcu);
        CrkvaSV3G.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","crkva_svete_trojice_u_gabrovcu");
                startActivity(openInfo);
            }
        });

        LinearLayout Kosturnica= (LinearLayout) findViewById(R.id.spomen_kosturnica);
        Kosturnica.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","spomen_kosturnica");
                startActivity(openInfo);
            }
        });

        LinearLayout MO= (LinearLayout) findViewById(R.id.spomenik_knezu_milanu_obrenovicu);
        MO.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","spomenik_knezu_milanu_obrenovicu");
                startActivity(openInfo);
            }
        });

        LinearLayout Lapidarijum= (LinearLayout) findViewById(R.id.lapidarijum);
        Lapidarijum.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","lapidarijum");
                startActivity(openInfo);
            }
        });

        LinearLayout Arsenal= (LinearLayout) findViewById(R.id.arsenal);
        Arsenal.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","arsenal");
                startActivity(openInfo);
            }
        });

        LinearLayout ObjekatS= (LinearLayout) findViewById(R.id.objekat_sa_svodovima);
        ObjekatS.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","objekat_sa_svodovima");
                startActivity(openInfo);
            }
        });

        LinearLayout Oktagon= (LinearLayout) findViewById(R.id.palata_sa_oktagonom);
        Oktagon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","palata_sa_oktagonom");
                startActivity(openInfo);
            }
        });

        LinearLayout ArheoloskaSala= (LinearLayout) findViewById(R.id.arheoloska_sala);
        ArheoloskaSala.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","arheoloska_sala");
                startActivity(openInfo);
            }
        });

        LinearLayout Sud= (LinearLayout) findViewById(R.id.osnovni_sud_u_nisu);
        Sud.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","osnovni_sud_u_nisu");
                startActivity(openInfo);
            }
        });

        LinearLayout Kazandzijsko= (LinearLayout) findViewById(R.id.kazandzijsko_sokace);
        Kazandzijsko.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","kazandzijsko_sokace");
                startActivity(openInfo);
            }
        });

        LinearLayout GradskaKuca= (LinearLayout) findViewById(R.id.gradska_kuca);
        GradskaKuca.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","gradska_kuca");
                startActivity(openInfo);
            }
        });

        LinearLayout Andon= (LinearLayout) findViewById(R.id.zgrada_trgovca_andona_andonovica);
        Andon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","zgrada_trgovca_andona_andonovica");
                startActivity(openInfo);
            }
        });

        LinearLayout CairskaCesma= (LinearLayout) findViewById(R.id.cairska_cesma);
        CairskaCesma.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","cairska_cesma");
                startActivity(openInfo);
            }
        });

        LinearLayout Stambolijski= (LinearLayout) findViewById(R.id.kuca_stambolijskih);
        Stambolijski.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","kuca_stambolijskih");
                startActivity(openInfo);
            }
        });

        LinearLayout Posta= (LinearLayout) findViewById(R.id.zgrada_glavne_poste);
        Posta.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","zgrada_glavne_poste");
                startActivity(openInfo);
            }
        });

        LinearLayout SKA= (LinearLayout) findViewById(R.id.spomenik_kralju_aleksandru_i_karadjordjevicu);
        SKA.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","spomenik_kralju_aleksandru_i_karadjordjevicu");
                startActivity(openInfo);
            }
        });

        LinearLayout Paster= (LinearLayout) findViewById(R.id.pasterov_zavod);
        Paster.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","pasterov_zavod");
                startActivity(openInfo);
            }
        });

        LinearLayout SVG= (LinearLayout) findViewById(R.id.srpsko_vojnicko_groblje);
        SVG.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","srpsko_vojnicko_groblje");
                startActivity(openInfo);
            }
        });

        LinearLayout VG= (LinearLayout) findViewById(R.id.vojno_groblje_britanskog_komonvelta);
        VG.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","vojno_groblje_britanskog_komonvelta");
                startActivity(openInfo);
            }
        });

        LinearLayout Cegar= (LinearLayout) findViewById(R.id.spomenik_na_cegru);
        Cegar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","spomenik_na_cegru");
                startActivity(openInfo);
            }
        });

        LinearLayout HumskaCuka= (LinearLayout) findViewById(R.id.humska_cuka);
        HumskaCuka.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","humska_cuka");
                startActivity(openInfo);
            }
        });

        LinearLayout NiskaBanja= (LinearLayout) findViewById(R.id.niska_banja);
        NiskaBanja.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","niska_banja");
                startActivity(openInfo);
            }
        });

        LinearLayout Sicevacka= (LinearLayout) findViewById(R.id.sicevacka_klisura);
        Sicevacka.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","sicevacka_klisura");
                startActivity(openInfo);
            }
        });

        LinearLayout Iverica= (LinearLayout) findViewById(R.id.crkva_svete_petke_iverica);
        Iverica.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","crkva_svete_petke_iverica");
                startActivity(openInfo);
            }
        });

        LinearLayout SvBogorodica= (LinearLayout) findViewById(R.id.manastir_sveta_bogorodica);
        SvBogorodica.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent openInfo=new Intent(MainActivity.this.getApplicationContext(),SpotInfo.class);
                openInfo.putExtra("spot","manastir_sveta_bogorodica");
                startActivity(openInfo);
            }
        });

    }

    private class MyMainLocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            double latitude = intent.getDoubleExtra("latitude", -1);
            double longitude = intent.getDoubleExtra("longitude", -1);
            /*EditText lon = (EditText) findViewById(R.id.lon);
            EditText lat = (EditText) findViewById(R.id.lat);
            lon.setText(String.valueOf(longitude));
            lat.setText(String.valueOf(latitude));*/
            Toast.makeText(getApplicationContext(), String.valueOf(latitude) +  " " + String.valueOf(longitude), Toast.LENGTH_LONG).show();
        }
    }
}
