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

import application.user.UserHandler;

/**
 * Created by Federico-PC on 28/12/2016.
 */

public class InformationsHandler extends AppCompatActivity {

    private HashMap<String,TextView> infoTxt;
    private AlertDialog alert;
    private Button send_b;
    private Spinner sex_spinner;
    private boolean[] emptyValue;

    public static final String null_msg = "Hai inserito un campo vuoto!";
    public static final String error_msg = "Hai inserito un campo errato!";
    public static final String pass_msg = "Hai sbagliato a reinserire la password";
    public static final String email_msg = "Esiste gia' un utente con questa mail";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informations);

        infoTxt = new HashMap<String,TextView>();
        emptyValue = new boolean[11];

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

        final int errorColor = Color.rgb(255,255,153);
        final int worthColor = Color.WHITE;

        infoTxt.get("email").setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {

                    if( infoTxt.get("email").getText().toString().isEmpty()){
                        infoTxt.get("email").setBackgroundColor(errorColor);
                        emptyValue[0] = true;

                    }
                    else{
                        infoTxt.get("email").setBackgroundColor(worthColor);
                        emptyValue[0] = false;

                    }
                }
            });

        infoTxt.get("pass1").setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if( infoTxt.get("pass1").getText().toString().isEmpty()){
                    infoTxt.get("pass1").setBackgroundColor(errorColor);
                    emptyValue[1] = true;
                }
                else{
                    infoTxt.get("pass1").setBackgroundColor(worthColor);
                    emptyValue[1] = false;
                }
            }
        });

        infoTxt.get("pass2").setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if( infoTxt.get("pass1").getText().toString().isEmpty()){
                    infoTxt.get("pass1").setBackgroundColor(errorColor);
                    emptyValue[2] = true;
                }
                else{
                    infoTxt.get("pass1").setBackgroundColor(worthColor);
                    emptyValue[2] = false;
                }
            }
        });

        infoTxt.get("name").setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if( infoTxt.get("name").getText().toString().isEmpty()){
                    infoTxt.get("name").setBackgroundColor(errorColor);
                    emptyValue[3] = true;
                }
                else{
                    infoTxt.get("name").setBackgroundColor(worthColor);
                    emptyValue[3] = false;
                }
            }
        });

        infoTxt.get("surname").setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if( infoTxt.get("surname").getText().toString().isEmpty()){
                    infoTxt.get("surname").setBackgroundColor(errorColor);
                    emptyValue[4] = true;
                }
                else{
                    infoTxt.get("surname").setBackgroundColor(worthColor);
                    emptyValue[4] = false;
                }
            }
        });


        infoTxt.get("birth_date").setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if( infoTxt.get("birth_date").getText().toString().isEmpty()){
                    infoTxt.get("birth_date").setBackgroundColor(errorColor);
                    emptyValue[5] = true;
                }
                else{
                    infoTxt.get("birth_date").setBackgroundColor(worthColor);
                    emptyValue[5] = false;
                }
            }
        });

        infoTxt.get("birth_city").setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if( infoTxt.get("birth_city").getText().toString().isEmpty()){
                    infoTxt.get("birth_city").setBackgroundColor(errorColor);
                    emptyValue[6] = true;
                }
                else{
                    infoTxt.get("birth_city").setBackgroundColor(worthColor);
                    emptyValue[6] = false;
                }
            }
        });

        infoTxt.get("province").setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if( infoTxt.get("province").getText().toString().isEmpty()){
                    infoTxt.get("province").setBackgroundColor(errorColor);
                    emptyValue[7] = true;
                }
                else{
                    infoTxt.get("province").setBackgroundColor(worthColor);
                    emptyValue[7] = false;
                }
            }
        });

        infoTxt.get("state").setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if( infoTxt.get("state").getText().toString().isEmpty()){
                    infoTxt.get("state").setBackgroundColor(errorColor);
                    emptyValue[8] = true;
                }
                else{
                    infoTxt.get("state").setBackgroundColor(worthColor);
                    emptyValue[8] = false;
                }
            }
        });


        infoTxt.get("phone").setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if( infoTxt.get("phone").getText().toString().isEmpty()){
                    infoTxt.get("phone").setBackgroundColor(errorColor);
                    emptyValue[9] = true;
                }
                else{
                    infoTxt.get("phone").setBackgroundColor(worthColor);
                    emptyValue[9] = true;
                }
            }
        });

        infoTxt.get("personal_number").setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if( infoTxt.get("personal_number").getText().toString().isEmpty()){
                    infoTxt.get("personal_number").setBackgroundColor(errorColor);
                    emptyValue[10] = true;
                }
                else{
                    infoTxt.get("personal_number").setBackgroundColor(worthColor);
                    emptyValue[10] = false;
                }
            }
        });


        send_b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //devo aggiungere che prende i dati
                //11 text 1 spinner e un'immagine

                gatheringInformation();

                /*
                Intent intent = new Intent (getApplicationContext(),
                        Home.class);
                startActivity(intent);*/
            }
        });


        /*
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
        });*/
    }

    private void gatheringInformation(){

        //campo vuoto
        boolean error = false;
        //check if some value is empty
        for(int i=0;i<11;i++){
            if(emptyValue[i]==true){
                error = true;
            }
        }

        if(error==true){
            alert.setMessage(email_msg);
            alert.show();
        }

        //controllo password

        if(infoTxt.get("pass1").getText().toString().equals(infoTxt.get("pass2").getText().toString())){
            alert.setMessage(pass_msg);
            alert.show();
        }

        if(!UserHandler.checkUser(infoTxt.get("email").getText().toString())){
            HashMap<String,String> info = new HashMap<>();
            info.put("email",infoTxt.get("email").getText().toString());
            info.put("pass",infoTxt.get("pass1").getText().toString());
            info.put("name",infoTxt.get("name").getText().toString());
            info.put("surname",infoTxt.get("surname").getText().toString());
            info.put("birth_date",infoTxt.get("birth_date").getText().toString());
            info.put("birth_city",infoTxt.get("birth_city").getText().toString());
            info.put("province",infoTxt.get("province").getText().toString());
            info.put("state",infoTxt.get("state").getText().toString());
            info.put("phone",infoTxt.get("phone").getText().toString());
            info.put("personal_number",infoTxt.get("personal_number").getText().toString());
            info.put("sex",sex_spinner.getSelectedItem().toString());

            UserHandler.logup(info);
        }else{
            alert.setMessage(email_msg);
            alert.show();
        }


    }
}


