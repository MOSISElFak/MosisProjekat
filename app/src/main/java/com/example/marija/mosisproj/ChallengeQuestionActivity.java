package com.example.marija.mosisproj;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ChallengeQuestionActivity extends AppCompatActivity {

    private String pitanje;
    private String tacanOdg;

    DatabaseReference dref;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_question);

        pitanje = getIntent().getStringExtra("pitanje");
        tacanOdg = getIntent().getStringExtra("tacanOdgovor");

        final TextView tw = (TextView) findViewById(R.id.pitanjeTextView);
        tw.setText(pitanje);

        final EditText et = (EditText) findViewById(R.id.odgovorEditText);

        Button buttonPredajOdgovor = (Button) findViewById(R.id.buttonPredajOdgovor);
        buttonPredajOdgovor.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onClick(View v) {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(ChallengeQuestionActivity.this);

                if (et.getText().toString().equals(tacanOdg)) {


                    builder1.setMessage("Oggovor je tačan! Dobili ste 5 dodatnih poena! ");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton("Continue",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                    startActivity(new Intent(ChallengeQuestionActivity.this,
                                            MainActivity.class));
                                }
                            });


                    AlertDialog alert11 = builder1.create();
                    alert11.show();


                    user = FirebaseAuth.getInstance().getCurrentUser();

                    dref = FirebaseDatabase.getInstance().getReference().child("user").child(user.getUid());

                    dref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Korisnik k = dataSnapshot.getValue(Korisnik.class);
                            k.setScore(k.getScore() + 5);


                            dref.setValue(k);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            System.out.println("The read failed: " + databaseError.getCode());
                        }
                    });


                } else {
                    builder1.setMessage("Odgovor je netačan! ");
                    builder1.setCancelable(true);

                    builder1.setNegativeButton("Continue",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                    startActivity(new Intent(ChallengeQuestionActivity.this,
                                            MainActivity.class));
                                }
                            });
                }
            }
        });

    }
}
