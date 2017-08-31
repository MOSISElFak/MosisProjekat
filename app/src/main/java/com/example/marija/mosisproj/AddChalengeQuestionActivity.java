package com.example.marija.mosisproj;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.marija.mosisproj.R.id.odgovor2;

public class AddChalengeQuestionActivity extends AppCompatActivity {

    FirebaseUser user;


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

                String answer=questionAnswer.getText().toString();
                String question=questionText.getText().toString();


                ChalengeQuestion q = new ChalengeQuestion(question, answer);

                q.setLat("0.0");
                q.setLng("0.0");


                user = FirebaseAuth.getInstance().getCurrentUser();


                DatabaseReference mDatabase;

                mDatabase = FirebaseDatabase.getInstance().getReference();

                mDatabase.child("challenge_questions").child(user.getUid().toString()).push().setValue(q);


                Toast.makeText(AddChalengeQuestionActivity.this, "Uspesno ste dodali pitanje", Toast.LENGTH_SHORT).show();

            }
        });
    }


}
