package com.example.marija.mosisproj;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import static com.example.marija.mosisproj.R.id.odgovor2;

public class AddChalengeQuestionActivity extends AppCompatActivity {

    FirebaseUser user;
    double latitude;
    double longitude;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chalenge_question);


        Button addQuestion = (Button) findViewById(R.id.add_challenge_question);

        final EditText questionAnswer = (EditText) findViewById(R.id.question_answer);
        final EditText questionText = (EditText) findViewById(R.id.question_text);



        addQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                user = FirebaseAuth.getInstance().getCurrentUser();

                final DatabaseReference mDatabase;

                mDatabase = FirebaseDatabase.getInstance().getReference();

                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                   @RequiresApi(api = Build.VERSION_CODES.N)
                   @Override
                   public void onDataChange(DataSnapshot dataSnapshot) {
                       Korisnik k = dataSnapshot.child("user").child(user.getUid()).getValue(Korisnik.class);

                       latitude = k.getLatitude();
                       longitude = k.getLongitude();

                       String answer = questionAnswer.getText().toString();
                       String question = questionText.getText().toString();


                       ChalengeQuestion q = new ChalengeQuestion(question, answer);

                       q.setLat(Double.toString(latitude));
                       q.setLng(Double.toString(longitude));

                       DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                       Date today = Calendar.getInstance().getTime();
                       String reportDate = df.format(today);

                       q.setPostDate(reportDate);

                       mDatabase.child("challenge_questions").child(user.getUid().toString()).push().setValue(q);


                       Toast.makeText(AddChalengeQuestionActivity.this, "Uspesno ste dodali pitanje", Toast.LENGTH_SHORT).show();

                       startActivity(new Intent(AddChalengeQuestionActivity.this, MainActivity.class));
                   }

                   @Override
                   public void onCancelled(DatabaseError databaseError) {

                   }
               }) ;



            }
        });

    }


}