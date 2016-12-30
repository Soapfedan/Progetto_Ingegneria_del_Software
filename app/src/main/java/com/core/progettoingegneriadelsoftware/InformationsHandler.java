package com.core.progettoingegneriadelsoftware;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Federico-PC on 28/12/2016.
 */

public class InformationsHandler extends AppCompatActivity {

    private HashMap<String,TextView> infoTxt;
    private AlertDialog alert;
    private Button send_b;
    private Spinner sex_spinner;

    public static final String null_msg = "Hai inserito un campo vuoto!";
    public static final String error_msg = "Hai inserito un campo errato!";
    public static final String pass_msg = "Hai sbagliato a reinserire la password";
    public static final String email_msg = "Esiste gia' un utente con questa mail";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informations);

        infoTxt = new HashMap<String,TextView>();
        loadResources();
        loadEvents();


    }

    protected void onStart() {
        super.onStart();

    }

    protected void onResume() {
        super.onResume();
    }

    protected void onPause() {
        super.onPause();
    }


    protected void onStop() {
        super.onStop();
    }


    public void onDestroy() {
        super.onDestroy();
    }


    private void loadResources(){

        TextView tv;
        // creo una hashmap con tutti gli elementi di una form
        tv = (TextView)findViewById(R.id.email_txt);
        infoTxt.put("email",tv);

        tv = (TextView)findViewById(R.id.pass_txt1);
        infoTxt.put("pass1",tv);

        tv = (TextView)findViewById(R.id.pass_txt2);
        infoTxt.put("pass2",tv);

        tv = (TextView)findViewById(R.id.name_txt);
        infoTxt.put("name", tv);

        tv = (TextView)findViewById(R.id.surname_txt);
        infoTxt.put("surname",tv);

        tv = (TextView)findViewById(R.id.birth_date_txt);
        infoTxt.put("birth_date",tv);

        tv = (TextView)findViewById(R.id.birth_city_txt);
        infoTxt.put("birth_city",tv);

        tv = (TextView)findViewById(R.id.province_txt);
        infoTxt.put("province",tv);

        tv = (TextView)findViewById(R.id.state_txt);
        infoTxt.put("state",tv);

        tv = (TextView)findViewById(R.id.phone_txt);
        infoTxt.put("phone",tv);

        tv = (TextView)findViewById(R.id.personal_number_txt);
        infoTxt.put("personal_number",tv);


        send_b = (Button) findViewById(R.id.profile_button);
        sex_spinner = (Spinner) findViewById(R.id.sex_spinner);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do nothings
                    }
                });
        alert = builder.create();


    }

    private void loadEvents(){

       /* Iterator it = infoTxt.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry pair = (Map.Entry)it.next();

            infoTxt.get(pair.getKey()).setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {

                    if(infoTxt.get(pair.getKey()).getText().toString().isEmpty())
                        infoTxt.get(pair.getKey()).setBackgroundColor(Color.RED);
                        infoTxt.get(pair.getKey()).setHint("Campo vuoto");
                }
            });

            it.remove(); // avoids a ConcurrentModificationException

        }*/


        send_b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //devo aggiungere che prende i dati
                //11 text 1 spinner e un'immagine

                alert.show();

                /*
                Intent intent = new Intent (getApplicationContext(),
                        Home.class);
                startActivity(intent);*/
            }
        });



        infoTxt.get("pass2").setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!(infoTxt.get("pass1").getText().toString().equals(infoTxt.get("pass2").getText().toString())&&
                        infoTxt.get("pass1").getText().toString()!=null && !infoTxt.get("pass1").getText().toString().isEmpty())){

                    //se sono diversi E la password non è nulle e non è vuota

                    alert.setMessage(pass_msg);
                    alert.show();

                }else {
                    //se sono uguali
                }
            }
        });
    }
}


