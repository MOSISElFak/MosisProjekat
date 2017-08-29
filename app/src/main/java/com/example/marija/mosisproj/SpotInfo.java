package com.example.marija.mosisproj;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SpotInfo extends AppCompatActivity {

    ProgressDialog progressDialog;
    String spotID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_info);
        progressDialog = ProgressDialog.show(this, "Molim sačekajte.",
                "Prikupljanje informacija...", true);

        SetInfo();


        Button StartQuiz=(Button) findViewById(R.id.button3);
        StartQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent quiz=new Intent(SpotInfo.this.getApplicationContext(),BarcodeActivity.class);
                quiz.putExtra("spot",spotID);
                startActivity(quiz);
            }
        });
    }

    private void SetInfo(){
        TextView Header = (TextView) findViewById(R.id.textView2);
        TextView Description = (TextView) findViewById(R.id.textView3);

        spotID=getIntent().getStringExtra("spot");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("spot"+"/"+spotID);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                final Spot s= dataSnapshot.getValue(Spot.class);
                TextView Header = (TextView) findViewById(R.id.textView2);
                TextView Description = (TextView) findViewById(R.id.textView3);

                Header.setText(s.getHeader());
                Description.setText(s.getDesc());

                Button map=(Button) findViewById(R.id.button5);
                map.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent openMap=new Intent(SpotInfo.this.getApplicationContext(),MapsActivity.class);
                        openMap.putExtra("latitude",Double.toString(s.getLatitude()));
                        openMap.putExtra("longitude",Double.toString(s.getLongitude()));
                        startActivity(openMap);
                    }
                });

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });


    }
}
