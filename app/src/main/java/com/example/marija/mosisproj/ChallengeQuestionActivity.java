package com.example.marija.mosisproj;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class ChallengeQuestionActivity extends AppCompatActivity {

    private String pitanje;
    private String tacanOdg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge_question);

        pitanje=getIntent().getStringExtra("pitanje");
        tacanOdg=getIntent().getStringExtra("tacanOdgovor");

        final TextView tw=(TextView) findViewById(R.id.pitanjeTextView);
        tw.setText(pitanje);

        final EditText et=(EditText)findViewById(R.id.odgovorEditText);

        Button buttonPredajOdgovor=(Button)findViewById(R.id.buttonPredajOdgovor);
        buttonPredajOdgovor.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onClick (View v)
            {
                if(et.getText().toString().equals(tacanOdg))
                {
                    Toast toast = Toast.makeText(getApplicationContext(), "Bravo, to je tacan odgovor!", Toast.LENGTH_SHORT);
                    toast.show();
                }
                else
                {
                    Toast toast = Toast.makeText(getApplicationContext(), "Nazalost, Vas odgovor je netacan!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

    }
}
