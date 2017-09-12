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
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotifyMgr;


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
                if(distance<=50000.00)
                    mNotifyMgr.notify(keyNotificationMap.get(s.getHeader()), mBuilder.build());

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


    LocationListener GPSListener =  new LocationListener(LocationManager.GPS_PROVIDER);
    LocationListener NetworkListener =  new LocationListener(LocationManager.NETWORK_PROVIDER);
    public MyService() {
        list=new ArrayList<Spot>();
        keyValueMap=new HashMap<String,String>();
        keyNotificationMap=new HashMap<String, Integer>();
        dref= FirebaseDatabase.getInstance().getReference("spot");
        dref.addListenerForSingleValueEvent(new ValueEventListener() {
            int i=0;
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.e("Count " ,""+snapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    Spot spot1 = postSnapshot.getValue(Spot.class);
                    keyValueMap.put(spot1.getHeader(),postSnapshot.getKey());
                    keyNotificationMap.put(spot1.getHeader(),i);
                    i++;

                    list.add(spot1);

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
