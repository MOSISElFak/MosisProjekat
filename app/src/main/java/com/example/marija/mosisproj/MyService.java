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


public class MyService extends Service {

    private static final String TAG = "LOCATION_SERVICE";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 5f;
    private int mNotificationId = 001;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotifyMgr;

    Integer Medijana=2;
    Integer arheoloska_sala=3;
    Integer arsenal=4;
    Integer barutane=5;
    Integer cairska_cesma=6;
    Integer cele_kula=7;
    Integer crkva_svete_petke_iverica=8;
    Integer crkva_svete_trojice_u_gabrovcu=9;
    Integer crkva_sveti_nikola=10;
    Integer crkva_svetog_arhangela_mihaila=11;
    Integer crkva_svetog_pantelejmona=12;
    Integer dzamija_balije_jedrenca=13;
    Integer gradska_kuca=14;
    Integer hamam=15;
    Integer humska_cuka=16;
    Integer kazandzijsko_sokace=17;
    Integer konstantin_veliki=18;
    Integer kuca_stambolijskih=19;
    Integer lapidarijum=20;
    Integer latinska_crkva=21;
    Integer letnja_pozornica=22;
    Integer  manastir_sveta_bogorodica=23;
    Integer muzej_na_crvenom_krstu=24;
    Integer narodno_pozoriste=25;
    Integer niska_banja=26;
    Integer niska_tvrdjava=27;
    Integer objekat_sa_svodovima=28;
    Integer oficirski_dom=28;
    Integer osnovni_sud_u_nisu=29;
    Integer palata_sa_oktagonom=30;
    Integer pasterov_zavod=31;
    Integer ranovizantijska_grobnica=32;
    Integer saborna_crkva_svete_trojice=33;
    Integer sicevacka_klisura=34;
    Integer sinagoga=35;
    Integer spomen_kompleks_bubanj=36;
    Integer spomen_kosturnica=37;
    Integer spomenik_knezu_milanu_obrenovicu=38;
    Integer spomenik_kralju_aleksandru_i_karadjordjevicu=39;
    Integer spomenik_na_cegru=40;
    Integer spomenik_oslobodiocima_nisa=41;
    Integer vojno_groblje_britanskog_komonvelta=42;
    Integer srpsko_vojnicko_groblje=43;
    Integer ulica_iz_doba_justinijana_prvog=44;
    Integer zgrada_banovine=45;
    Integer zgrada_glavne_poste=46;
    Integer zgrada_trgovca_andona_andonovica=47;



    DatabaseReference dref;
    ArrayList<Spot> list;
    HashMap<String,String> keyValueMap;
    HashMap<String,Integer> keyNotificationMap;

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
                if(distance<=5000.00){
                    mNotifyMgr.notify(keyNotificationMap.get(s.getHeader()), mBuilder.build());
                    mNotificationId++;
                }
                i++;
            }

        }

        public void callBroadcastReceiver(){
            Intent myFilteredResponse = new
                    Intent("com.example.nemanja.mylocationtracker.LOCATION");
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
        keyNotificationMap=new HashMap<String, Integer>();
        dref= FirebaseDatabase.getInstance().getReference("spot");
        dref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.e("Count " ,""+snapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Spot spot1 = postSnapshot.getValue(Spot.class);
                    list.add(spot1);

                    switch (spot1.getHeader()){
                        case "Medijana": keyValueMap.put(spot1.getHeader(),"Medijana");
                            keyNotificationMap.put(spot1.getHeader(),Medijana); break;
                        case "Arheološka sala": keyValueMap.put(spot1.getHeader(),"arheoloska_sala");
                            keyNotificationMap.put(spot1.getHeader(),arheoloska_sala); break;
                        case "Arsenal": keyValueMap.put(spot1.getHeader(),"arsenal");
                            keyNotificationMap.put(spot1.getHeader(),arsenal); break;
                        case "Barutane": keyValueMap.put(spot1.getHeader(),"barutane");
                            keyNotificationMap.put(spot1.getHeader(),barutane); break;
                        case "Čairska česma": keyValueMap.put(spot1.getHeader(),"cairska_cesma");
                            keyNotificationMap.put(spot1.getHeader(),cairska_cesma); break;
                        case "Ćele Kula": keyValueMap.put(spot1.getHeader(),"cele_kula");
                            keyNotificationMap.put(spot1.getHeader(),cele_kula); break;
                        case "Crkva Svete Petke \"Iverica\"": keyValueMap.put(spot1.getHeader(),"crkva_svete_petke_iverica");
                            keyNotificationMap.put(spot1.getHeader(),crkva_svete_petke_iverica); break;
                        case "Crkva Svete Trojice u Gabrovcu": keyValueMap.put(spot1.getHeader(),"crkva_svete_trojice_u_gabrovcu");
                            keyNotificationMap.put(spot1.getHeader(),crkva_svete_trojice_u_gabrovcu); break;
                        case "Crkva Sveti Nikola": keyValueMap.put(spot1.getHeader(),"crkva_sveti_nikola");
                            keyNotificationMap.put(spot1.getHeader(),crkva_sveti_nikola);break;
                        case "Crkva Svetog Arhangela Mihaila": keyValueMap.put(spot1.getHeader(),"crkva_svetog_arhangela_mihaila");
                            keyNotificationMap.put(spot1.getHeader(),crkva_svetog_arhangela_mihaila);break;
                        case "Crkva Svetog Pantelejmona": keyValueMap.put(spot1.getHeader(),"crkva_svetog_pantelejmona");
                            keyNotificationMap.put(spot1.getHeader(),crkva_svetog_pantelejmona);break;
                        case "Džamija Balije Jedrenca": keyValueMap.put(spot1.getHeader(),"dzamija_balije_jedrenca");
                            keyNotificationMap.put(spot1.getHeader(),dzamija_balije_jedrenca);break;
                        case "Gradska kuća": keyValueMap.put(spot1.getHeader(),"gradska_kuca");
                            keyNotificationMap.put(spot1.getHeader(),gradska_kuca);break;
                        case "Hamam": keyValueMap.put(spot1.getHeader(),"hamam");
                            keyNotificationMap.put(spot1.getHeader(),hamam);break;
                        case "Humska čuka": keyValueMap.put(spot1.getHeader(),"humska_cuka");
                            keyNotificationMap.put(spot1.getHeader(),humska_cuka);break;
                        case "Kazandžijsko sokače": keyValueMap.put(spot1.getHeader(),"kazandzijsko_sokace");
                            keyNotificationMap.put(spot1.getHeader(),kazandzijsko_sokace);break;
                        case "Konstantin Veliki": keyValueMap.put(spot1.getHeader(),"konstantin_veliki");
                            keyNotificationMap.put(spot1.getHeader(),konstantin_veliki);break;
                        case "Kuća Stambolijskih": keyValueMap.put(spot1.getHeader(),"kuca_stambolijskih");
                            keyNotificationMap.put(spot1.getHeader(),kuca_stambolijskih);break;
                        case "Lapidarijum": keyValueMap.put(spot1.getHeader(),"lapidarijum");
                            keyNotificationMap.put(spot1.getHeader(),lapidarijum);break;
                        case "Latinska Crkva": keyValueMap.put(spot1.getHeader(),"latinska_crkva");
                            keyNotificationMap.put(spot1.getHeader(),latinska_crkva);break;
                        case "Letnja pozornica": keyValueMap.put(spot1.getHeader(),"letnja_pozornica");
                            keyNotificationMap.put(spot1.getHeader(),letnja_pozornica);break;
                        case "Manastir Sveta Bogorodica": keyValueMap.put(spot1.getHeader(),"manastir_sveta_bogorodica");
                            keyNotificationMap.put(spot1.getHeader(),manastir_sveta_bogorodica);break;
                        case "Muzej na Crvenom krstu": keyValueMap.put(spot1.getHeader(),"muzej_na_crvenom_krstu");
                            keyNotificationMap.put(spot1.getHeader(),muzej_na_crvenom_krstu);break;
                        case "Narodno pozorište": keyValueMap.put(spot1.getHeader(),"narodno_pozoriste");
                            keyNotificationMap.put(spot1.getHeader(),narodno_pozoriste); break;
                        case "Niška Banja": keyValueMap.put(spot1.getHeader(),"niska_banja");
                            keyNotificationMap.put(spot1.getHeader(),niska_banja); break;
                        case "Niška tvrđava": keyValueMap.put(spot1.getHeader(),"niska_tvrdjava");
                            keyNotificationMap.put(spot1.getHeader(),niska_tvrdjava); break;
                        case "Objekat sa svodovima": keyValueMap.put(spot1.getHeader(),"objekat_sa_svodovima");
                            keyNotificationMap.put(spot1.getHeader(),objekat_sa_svodovima); break;
                        case "Oficirski dom": keyValueMap.put(spot1.getHeader(),"oficirski_dom");
                            keyNotificationMap.put(spot1.getHeader(),oficirski_dom); break;
                        case "Osnovni sud u Nišu": keyValueMap.put(spot1.getHeader(),"osnovni_sud_u_nisu");
                            keyNotificationMap.put(spot1.getHeader(),osnovni_sud_u_nisu); break;
                        case "Palata sa oktagonom": keyValueMap.put(spot1.getHeader(),"palata_sa_oktagonom");
                            keyNotificationMap.put(spot1.getHeader(),palata_sa_oktagonom); break;
                        case "Pasterov zavod": keyValueMap.put(spot1.getHeader(),"pasterov_zavod");
                            keyNotificationMap.put(spot1.getHeader(),pasterov_zavod); break;
                        case "Ranovizantijska grobnica": keyValueMap.put(spot1.getHeader(),"ranovizantijska_grobnica");
                            keyNotificationMap.put(spot1.getHeader(),ranovizantijska_grobnica); break;
                        case "Saborna Crkva Svete Trojice": keyValueMap.put(spot1.getHeader(),"saborna_crkva_svete_trojice");
                            keyNotificationMap.put(spot1.getHeader(),saborna_crkva_svete_trojice); break;
                        case "Sićevačka klisura": keyValueMap.put(spot1.getHeader(),"sicevacka_klisura");
                            keyNotificationMap.put(spot1.getHeader(),sicevacka_klisura); break;
                        case "Sinagoga": keyValueMap.put(spot1.getHeader(),"sinagoga");
                            keyNotificationMap.put(spot1.getHeader(),sinagoga); break;
                        case "Spomen kompleks Bubanj": keyValueMap.put(spot1.getHeader(),"spomen_kompleks_bubanj");
                            keyNotificationMap.put(spot1.getHeader(),spomen_kompleks_bubanj); break;
                        case "Spomen-kosturnica": keyValueMap.put(spot1.getHeader(),"spomen_kosturnica");
                            keyNotificationMap.put(spot1.getHeader(),spomen_kosturnica); break;
                        case "Spomenik knezu Milanu Obrenoviću": keyValueMap.put(spot1.getHeader(),"spomenik_knezu_milanu_obrenovicu");
                            keyNotificationMap.put(spot1.getHeader(),spomenik_knezu_milanu_obrenovicu); break;
                        case "Spomenik Kralju Aleksandru I Karađorđeviću": keyValueMap.put(spot1.getHeader(),"spomenik_kralju_aleksandru_i_karadjordjevicu");
                            keyNotificationMap.put(spot1.getHeader(),spomenik_kralju_aleksandru_i_karadjordjevicu); break;
                        case "Spomenik na Čegru": keyValueMap.put(spot1.getHeader(),"spomenik_na_cegru");
                            keyNotificationMap.put(spot1.getHeader(),spomenik_na_cegru); break;
                        case "Spomenik oslobodiocima Niša": keyValueMap.put(spot1.getHeader(),"spomenik_oslobodiocima_nisa");
                            keyNotificationMap.put(spot1.getHeader(),spomenik_oslobodiocima_nisa); break;
                        case "Srpsko vojničko groblje": keyValueMap.put(spot1.getHeader(),"srpsko_vojnicko_groblje");
                            keyNotificationMap.put(spot1.getHeader(),srpsko_vojnicko_groblje);break;
                        case "Ulica iz doba Justinijana Prvog": keyValueMap.put(spot1.getHeader(),"ulica_iz_doba_justinijana_prvog");
                            keyNotificationMap.put(spot1.getHeader(),ulica_iz_doba_justinijana_prvog); break;
                        case "Vojno groblje britanskog komonvelta": keyValueMap.put(spot1.getHeader(),"vojno_groblje_britanskog_komonvelta");
                            keyNotificationMap.put(spot1.getHeader(),vojno_groblje_britanskog_komonvelta); break;
                        case "Zgrada Banovine": keyValueMap.put(spot1.getHeader(),"zgrada_banovine");
                            keyNotificationMap.put(spot1.getHeader(),zgrada_banovine); break;
                        case "Zgrada glavne pošte": keyValueMap.put(spot1.getHeader(),"zgrada_glavne_poste");
                            keyNotificationMap.put(spot1.getHeader(),zgrada_glavne_poste); break;
                        case "Zgrada trgovca Andona Andonovića": keyValueMap.put(spot1.getHeader(),"zgrada_trgovca_andona_andonovica");
                            keyNotificationMap.put(spot1.getHeader(),zgrada_trgovca_andona_andonovica); break;
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
