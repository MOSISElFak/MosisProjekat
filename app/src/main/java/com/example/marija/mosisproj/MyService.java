package com.example.marija.mosisproj;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.Gson;

public class MyService extends Service {

    private static final String TAG = "LOCATION_SERVICE";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 5f;
    private int mNotificationId = 001;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotifyMgr;

    DatabaseReference dref;
    ArrayList<Spot> list;
    HashMap<String,String> keyValueMap;

    private class LocationListener implements android.location.LocationListener
    {
        Location mLastLocation;
        Location currLocation;

        public LocationListener(String provider)
        {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }


        @Override
        public void onLocationChanged(Location location)
        {
            currLocation = location;
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            ExecutorService transThread = Executors.newSingleThreadExecutor();
            transThread.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        callBroadcastReceiver();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            Location tasklocation=new Location(LocationManager.GPS_PROVIDER);

            int i=0;
            for(Spot s:list){

                tasklocation.setLatitude(s.getLatitude());
                tasklocation.setLongitude(s.getLongitude());
                double distance = location.distanceTo(tasklocation);

                mBuilder.setContentText("Nalazite se blizu lokacije - "+s.getHeader()+"!");
                Intent resultIntent = new Intent(MyService.this.getApplicationContext(), SpotInfo.class);
                resultIntent.putExtra("spot",String.valueOf(keyValueMap.get(s.getHeader())));
                PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(
                                MyService.this.getApplicationContext(),
                                i,
                                resultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setContentIntent(resultPendingIntent);
                if(distance<=50.00){
                    mNotifyMgr.notify(mNotificationId, mBuilder.build());
                    mNotificationId++;
                }
                i++;
            }

        }

        public void callBroadcastReceiver(){
            Intent myFilteredResponse = new
                    Intent("com.example.marija.mylocationtracker.LOCATION");
            myFilteredResponse.putExtra("latitude", currLocation.getLatitude());
            myFilteredResponse.putExtra("longitude", currLocation.getLongitude());

            sendBroadcast(myFilteredResponse);
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    // LocationListener GPSListener;
    //LocationListener NetworkListener;
    LocationListener GPSListener =  new LocationListener(LocationManager.GPS_PROVIDER);
    LocationListener NetworkListener =  new LocationListener(LocationManager.NETWORK_PROVIDER);
    public MyService() {
        list=new ArrayList<Spot>();
        keyValueMap=new HashMap<String,String>();
        final Gson gson=new Gson();
        dref= FirebaseDatabase.getInstance().getReference("spot");
        dref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.e("Count " ,""+snapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Object spot = postSnapshot.getValue();
                    String json=gson.toJson(spot);
                    Spot spot1=gson.fromJson(json,Spot.class);
                    list.add(spot1);

                    switch (spot1.getHeader()){
                        case "Medijana": keyValueMap.put(spot1.getHeader(),"Medijana"); break;
                        case "Arheološka sala": keyValueMap.put(spot1.getHeader(),"arheoloska_sala"); break;
                        case "Arsenal": keyValueMap.put(spot1.getHeader(),"arsenal"); break;
                        case "Barutane": keyValueMap.put(spot1.getHeader(),"barutane"); break;
                        case "Čairska česma": keyValueMap.put(spot1.getHeader(),"cairska_cesma"); break;
                        case "Ćele Kula": keyValueMap.put(spot1.getHeader(),"cele_kula"); break;
                        case "Crkva Svete Petke \"Iverica\"": keyValueMap.put(spot1.getHeader(),"crkva_svete_petke_iverica"); break;
                        case "Crkva Svete Trojice u Gabrovcu": keyValueMap.put(spot1.getHeader(),"crkva_svete_trojice_u_gabrovcu"); break;
                        case "Crkva Sveti Nikola": keyValueMap.put(spot1.getHeader(),"crkva_sveti_nikola"); break;
                        case "Crkva Svetog Arhangela Mihaila": keyValueMap.put(spot1.getHeader(),"crkva_svetog_arhangela_mihaila"); break;
                        case "Crkva Svetog Pantelejmona": keyValueMap.put(spot1.getHeader(),"crkva_svetog_pantelejmona"); break;
                        case "Džamija Balije Jedrenca": keyValueMap.put(spot1.getHeader(),"dzamija_balije_jedrenca"); break;
                        case "Gradska kuća": keyValueMap.put(spot1.getHeader(),"gradska_kuca"); break;
                        case "Hamam": keyValueMap.put(spot1.getHeader(),"hamam"); break;
                        case "Humska čuka": keyValueMap.put(spot1.getHeader(),"humska_cuka"); break;
                        case "Kazandžijsko sokače": keyValueMap.put(spot1.getHeader(),"kazandzijsko_sokace"); break;
                        case "Konstantin Veliki": keyValueMap.put(spot1.getHeader(),"konstantin_veliki"); break;
                        case "Kuća Stambolijskih": keyValueMap.put(spot1.getHeader(),"kuca_stambolijskih"); break;
                        case "Lapidarijum": keyValueMap.put(spot1.getHeader(),"lapidarijum"); break;
                        case "Latinska Crkva": keyValueMap.put(spot1.getHeader(),"latinska_crkva"); break;
                        case "Letnja pozornica": keyValueMap.put(spot1.getHeader(),"letnja_pozornica"); break;
                        case "Manastir Sveta Bogorodica": keyValueMap.put(spot1.getHeader(),"manastir_sveta_bogorodica"); break;
                        case "Muzej na Crvenom krstu": keyValueMap.put(spot1.getHeader(),"muzej_na_crvenom_krstu"); break;
                        case "Narodno pozorište": keyValueMap.put(spot1.getHeader(),"narodno_pozoriste"); break;
                        case "Niška Banja": keyValueMap.put(spot1.getHeader(),"niska_banja"); break;
                        case "Niška tvrđava": keyValueMap.put(spot1.getHeader(),"niska_tvrdjava"); break;
                        case "Objekat sa svodovima": keyValueMap.put(spot1.getHeader(),"objekat_sa_svodovima"); break;
                        case "Oficirski dom": keyValueMap.put(spot1.getHeader(),"oficirski_dom"); break;
                        case "Osnovni sud u Nišu": keyValueMap.put(spot1.getHeader(),"osnovni_sud_u_nisu"); break;
                        case "Palata sa oktagonom": keyValueMap.put(spot1.getHeader(),"palata_sa_oktagonom"); break;
                        case "Pasterov zavod": keyValueMap.put(spot1.getHeader(),"pasterov_zavod"); break;
                        case "Ranovizantijska grobnica": keyValueMap.put(spot1.getHeader(),"ranovizantijska_grobnica"); break;
                        case "Saborna Crkva Svete Trojice": keyValueMap.put(spot1.getHeader(),"saborna_crkva_svete_trojice"); break;
                        case "Sićevačka klisura": keyValueMap.put(spot1.getHeader(),"sicevacka_klisura"); break;
                        case "Sinagoga": keyValueMap.put(spot1.getHeader(),"sinagoga"); break;
                        case "Spomen kompleks Bubanj": keyValueMap.put(spot1.getHeader(),"spomen_kompleks_bubanj"); break;
                        case "Spomen-kosturnica": keyValueMap.put(spot1.getHeader(),"spomen_kosturnica"); break;
                        case "Spomenik knezu Milanu Obrenoviću": keyValueMap.put(spot1.getHeader(),"spomenik_knezu_milanu_obrenovicu"); break;
                        case "Spomenik Kralju Aleksandru I Karađorđeviću": keyValueMap.put(spot1.getHeader(),"spomenik_kralju_aleksandru_i_karadjordjevicu"); break;
                        case "Spomenik na Čegru": keyValueMap.put(spot1.getHeader(),"spomenik_na_cegru"); break;
                        case "Spomenik oslobodiocima Niša": keyValueMap.put(spot1.getHeader(),"spomenik_oslobodiocima_nisa"); break;
                        case "Srpsko vojničko groblje": keyValueMap.put(spot1.getHeader(),"srpsko_vojnicko_groblje"); break;
                        case "Ulica iz doba Justinijana Prvog": keyValueMap.put(spot1.getHeader(),"ulica_iz_doba_justinijana_prvog"); break;
                        case "Vojno groblje britanskog komonvelta": keyValueMap.put(spot1.getHeader(),"vojno_groblje_britanskog_komonvelta"); break;
                        case "Zgrada Banovine": keyValueMap.put(spot1.getHeader(),"zgrada_banovine"); break;
                        case "Zgrada glavne pošte": keyValueMap.put(spot1.getHeader(),"zgrada_glavne_poste"); break;
                        case "Zgrada trgovca Andona Andonovića": keyValueMap.put(spot1.getHeader(),"zgrada_trgovca_andona_andonovica"); break;
                    }
                    Log.e("Get Data", spot1.getHeader());
                }
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.e("The read failed: " ,firebaseError.getMessage());
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");
        Log.e(TAG, "initializeLocationManager");

        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }

        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    NetworkListener);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    GPSListener);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }

        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_near_me_white_48px)
                        .setContentTitle("Upoznaj Grad - Obavestenje")
                        .setContentText("Nalazite se blizu lokacije od značaja!")
                        .setSound(uri);
        mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mBuilder.setVibrate(new long[] {1000,200,1000,200});

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationManager != null) {

            try {
                mLocationManager.removeUpdates(GPSListener);
                mLocationManager.removeUpdates(NetworkListener);
            } catch (Exception ex) {
                Log.i(TAG, "fail to remove location listners, ignore", ex);
            }

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
