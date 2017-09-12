package com.example.marija.mosisproj;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SpotInfo extends AppCompatActivity {

    ProgressDialog progressDialog;
    String spotID;

    ViewPager viewPager;
    LinearLayout sliderDotspanel;
    private int datacount;
    private ImageView [] dots;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_info);
        progressDialog = ProgressDialog.show(this, "Molim sačekajte.",
                "Prikupljanje informacija...", true);



        ArrayList<String> images = getIntent().getStringArrayListExtra("images");

        viewPager = (ViewPager) findViewById(R.id.imageViewSlider);
        sliderDotspanel=(LinearLayout)findViewById(R.id.SliderDots);

        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(this,images);
        viewPager.setAdapter(viewPagerAdapter);
        datacount=viewPagerAdapter.getCount();
        dots= new ImageView[datacount];

        for(int i=0;i<datacount;i++)
        {
            dots[i]=new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_fiber_manual_record_black_48px));

            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8,0,8,0);

            sliderDotspanel.addView(dots[i],params);

        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_fiber_manual_record_white_48px));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                for(int i=0;i<datacount;i++)
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_fiber_manual_record_black_48px));
                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_fiber_manual_record_white_48px));


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



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

                FloatingActionButton map=(FloatingActionButton) findViewById(R.id.floatingActionButton);
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
