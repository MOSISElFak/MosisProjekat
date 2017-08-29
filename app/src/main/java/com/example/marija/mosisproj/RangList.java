package com.example.marija.mosisproj;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static android.R.attr.id;
import static com.example.marija.mosisproj.R.*;

public class RangList extends AppCompatActivity {

    DatabaseReference dref;
    ListView listview;
    ArrayList<String> list=new ArrayList<>();
    ArrayAdapter<String> adapter;
    Gson gson=new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rang_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listview=(ListView)findViewById(R.id.listview);
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_activated_1,list);
        listview.setAdapter(adapter);
        dref=FirebaseDatabase.getInstance().getReference("user");
        dref.orderByChild("score").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Object korisnik= dataSnapshot.getValue();
                String json=gson.toJson(korisnik);
                Korisnik p=gson.fromJson(json,Korisnik.class);

                String value=String.valueOf(p.getFirstname()+" "+p.getLastname()+" "+p.getScore());

                list.add(value);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Korisnik korisnik = dataSnapshot.getValue(Korisnik.class);
                String value= korisnik.getFirstname()+" "+korisnik.getLastname() +" "+korisnik.getScore();
                list.remove(value);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
