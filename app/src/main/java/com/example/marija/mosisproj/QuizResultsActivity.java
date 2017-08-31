package com.example.marija.mosisproj;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

public class QuizResultsActivity extends AppCompatActivity {

    TextView tacni,netacni;
    DatabaseReference dref;
    FirebaseUser user;
    String correct,wrong;
    Button btnAddQuestion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_results);

        correct= getIntent().getStringExtra("brojTacnih");
        wrong= String.valueOf(3-Integer.parseInt(correct));

        tacni=(TextView) findViewById(R.id.tacni);
        tacni.setText(correct);

        netacni=(TextView) findViewById(R.id.netacni);
        netacni.setText(wrong);


        btnAddQuestion=(Button)findViewById(R.id.btnAddQuestion);
        btnAddQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent AddQuestion =new Intent(QuizResultsActivity.this.getApplicationContext(),AddChalengeQuestionActivity.class);
                startActivity(AddQuestion);
            }
        });

        user = FirebaseAuth.getInstance().getCurrentUser();

        final Gson gson=new Gson();
        dref=FirebaseDatabase.getInstance().getReference("user/"+ user.getUid()+"/score");

        dref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                Object oldPoints= dataSnapshot.getValue();
                String json=gson.toJson(oldPoints);
                String p=gson.fromJson(json,String.class);

                String points=String.valueOf(Integer.parseInt(p)+Integer.parseInt(correct));

                dref.setValue(points);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });


    }
}
