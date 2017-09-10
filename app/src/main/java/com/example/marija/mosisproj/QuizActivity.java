package com.example.marija.mosisproj;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class QuizActivity extends AppCompatActivity {

    DatabaseReference dref;
    String ID;
    final List<Question> list=new ArrayList<Question>();
    TextView question1,question2,question3,question4;
    RadioButton odg11,odg12,odg13,odg14;
    CheckBox odg31,odg32,odg33,odg34;
    String odgovor1,odgovor2,odgovor3,odgovor4;
    String [] niz;
    String [] niz1;
    String tacanOdg1,tacanOdg2,tacanOdg3,tacanOdg4;
    Integer brTacnih;
    List<Question> pitanja=new ArrayList<>();
    List<Question> qRadio=new ArrayList<>();
    List<Question> qText=new ArrayList<>();
    List<Question> qCheck=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        setQuestion();


        Button submitAnswers = (Button) findViewById(R.id.button4);
        submitAnswers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                takeAnswers();

                brTacnih=0;

                if(odgovor1.equals(tacanOdg1))
                    brTacnih++;
                if(odgovor2.equals(tacanOdg2))
                    brTacnih++;
                //   if(odgovor4.equals(tacanOdg4)_
                //     brTacnih++;

                String [] datiOdgovori3=odgovor3.split(",");
                String [] tacniOdgovori3=tacanOdg3.split("\\,");

                int pom=0;
                if(datiOdgovori3.length==tacniOdgovori3.length) {
                    for (int i = 0; i < datiOdgovori3.length; i++)
                        for(int j=0;j<tacniOdgovori3.length; j++)
                            if (datiOdgovori3[i].equals(tacniOdgovori3[j]))
                                pom++;
                }
                if (pom==tacniOdgovori3.length)
                    brTacnih++;


                Intent QuizResults =new Intent(QuizActivity.this.getApplicationContext(),QuizResultsActivity.class);
                QuizResults.putExtra("brojTacnih",brTacnih.toString());
                QuizResults.putExtra("id",ID);
                startActivity(QuizResults);


            }
        });

    }

    void takeAnswers()
    {
        odgovor3="";
        odgovor1="";
        odgovor2="";

        TextView pom2=(TextView)findViewById(R.id.odgovor2);

        if(odg11.isChecked())
            odgovor1=odg11.getText().toString();
        else if(odg12.isChecked())
            odgovor1=odg12.getText().toString();
        else if(odg13.isChecked())
            odgovor1=odg13.getText().toString();
        else if(odg14.isChecked())
            odgovor1=odg14.getText().toString();
        else
            odgovor1="";

        odgovor2 = pom2.getText().toString();



        if(odg31.isChecked())
            odgovor3=odgovor3+","+odg31.getText().toString();
        else if(odg32.isChecked())
            odgovor3=odgovor3+","+odg32.getText().toString();
        else if(odg33.isChecked())
            odgovor3=odgovor3+","+odg33.getText().toString();
        else if(odg34.isChecked())
            odgovor3=odgovor3+","+odg34.getText().toString();
        else
            odgovor3="";

        if(!odgovor3.equals(""))
            odgovor3 = odgovor3.substring(1, odgovor3.length()-1);


    }
    void setQuestion()
    {
        ID=getIntent().getStringExtra("spot");
        dref= FirebaseDatabase.getInstance().getReference("question/" + ID);
        dref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Question q = postSnapshot.getValue(Question.class);
                    list.add(q);

                }

                rasporediPitanja(list);

                tacanOdg1=pitanja.get(0).getTacniOdg();
                tacanOdg2=pitanja.get(1).getTacniOdg();
                tacanOdg3=pitanja.get(2).getTacniOdg();
                //   tacanOdg4=pitanja.get(3).getTacniOdg();

                question1=(TextView) findViewById(R.id.pitanje1);
                question2=(TextView) findViewById(R.id.pitanje2);
                question3=(TextView) findViewById(R.id.pitanje3);
                //    question4=(TextView) findViewById(id.pitanje4);

                odg11=(RadioButton) findViewById(R.id.odg11);
                odg12=(RadioButton) findViewById(R.id.odg12);
                odg13=(RadioButton) findViewById(R.id.odg13);
                odg14=(RadioButton) findViewById(R.id.odg14);

                niz=pitanja.get(0).getPonudjeniOdg().split(",");
                odg11.setText(niz[0]);
                odg12.setText(niz[1]);
                odg13.setText(niz[2]);
                odg14.setText(niz[3]);


                odg31=(CheckBox) findViewById(R.id.odg31);
                odg32=(CheckBox) findViewById(R.id.odg32);
                odg33=(CheckBox) findViewById(R.id.odg33);
                odg34=(CheckBox) findViewById(R.id.odg34);

                niz1=pitanja.get(2).getPonudjeniOdg().split(",");
                odg31.setText(niz1[0]);
                odg32.setText(niz1[1]);
                odg33.setText(niz1[2]);
                odg34.setText(niz1[3]);


                question1.setText(pitanja.get(0).getTekst());
                question2.setText(pitanja.get(1).getTekst());
                question3.setText(pitanja.get(2).getTekst());
                //  question4.setText(pitanja.get(3).getTekst());


            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.e("The read failed: " ,firebaseError.getMessage());
            }

        });


    };
    void rasporediPitanja(List<Question> q)
    {
        for(int i=0;i<q.size();i++)
        {
            String s=q.get(i).getTip().toString();
            if(s.equals("text"))
                qText.add(q.get(i));
            else if(s.equals("radio"))
                qRadio.add(q.get(i));
            else if(s.equals("check"))
                qCheck.add(q.get(i));
        }

        int prvo, drugo, trece;
        prvo=qRadio.size();
        drugo=qText.size();
        trece=qCheck.size();

        Random rand = new Random();
        int a,b,c;
        a=rand.nextInt(prvo);
        b=rand.nextInt(drugo);
        c=rand.nextInt(trece);

        pitanja.add(qRadio.get(a));
        pitanja.add(qText.get(b));
        pitanja.add(qCheck.get(c));
    }


}