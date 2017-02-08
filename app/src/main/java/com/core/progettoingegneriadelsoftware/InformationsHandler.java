package com.core.progettoingegneriadelsoftware;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import application.MainApplication;
import application.user.UserHandler;
import application.user.UserProfile;
import application.validation.FormControl;

/**
 * Created by Federico-PC on 28/12/2016.
 */

public class InformationsHandler extends AppCompatActivity {

    private HashMap<String,TextView> infoTxt;
    private AlertDialog alert;
    private Button send_b;
    private Spinner sex_spinner;
    private boolean[] emptyValue;
    private UserProfile profile;

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
        MainApplication.getDB().open();
        if(UserHandler.isLogged()){
            profile = UserHandler.getInformation(UserHandler.getMail());
            populate();
        }

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
        //MainApplication.getDB().close();
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

//                if (!FormControl.mailControl(infoTxt.get("email").getText().toString()) && !hasFocus) {
//                    alert.setMessage("l'email deve contenere solo lettere, @ e punti");
//                    alert.show();
//                }

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

//                if (!FormControl.passwordControl(infoTxt.get("pass1").getText().toString()) && !hasFocus) {
//                    alert.setMessage("la password deve contenere almeno 8 caratteri");
//                    alert.show();
//                }

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

//                if (!FormControl.passwordControl(infoTxt.get("pass2").getText().toString()) && !hasFocus) {
//                    alert.setMessage("la password deve contenere almeno 8 caratteri");
//                    alert.show();
//                }

                if( infoTxt.get("pass2").getText().toString().isEmpty()){
                    infoTxt.get("pass2").setBackgroundColor(errorColor);
                    emptyValue[2] = true;
                }
                else{
                    infoTxt.get("pass2").setBackgroundColor(worthColor);
                    emptyValue[2] = false;
                }
            }
        });

        infoTxt.get("name").setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                    //controlla che sia composto solo da lettere e spazi
//                if (!FormControl.letterControl(infoTxt.get("name").getText().toString()) && !hasFocus) {
//                    alert.setMessage("il nome deve contenere solo lettere");
//                    alert.show();
//                }

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
                    //controlla che sia composto solo da lettere e spazi
//                if (!FormControl.letterControl(infoTxt.get("surname").getText().toString()) && !hasFocus) {
//                    alert.setMessage("il cognome deve contenere solo lettere");
//                    alert.show();
//                }

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

                    //controlla che sia composto solo da lettere e spazi
//                if (!FormControl.letterControl(infoTxt.get("birth_city").getText().toString()) && !hasFocus) {
//                    alert.setMessage("la città deve contenere solo lettere");
//                    alert.show();
//                }

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
                    //controlla che sia composto solo da lettere e spazi
//                if (!FormControl.letterControl(infoTxt.get("name").getText().toString()) && !hasFocus) {
//                    alert.setMessage("la provincia deve contenere solo lettere");
//                    alert.show();
//                }
//                if (!FormControl.provenceControl(infoTxt.get("name").getText().toString()) && !hasFocus) {
//                    alert.setMessage("la provincia deve essere composta da due lettere");
//                    alert.show();
//                }

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

//                if (!FormControl.letterControl(infoTxt.get("state").getText().toString()) && !hasFocus) {
//                    alert.setMessage("lo stato deve contenere solo lettere");
//                    alert.show();
//                }

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
                //controllo se abbastanza lungo, solo quando esco dal text (!hasFocus serve per quello)
//                if (!FormControl.phoneControl(infoTxt.get("phone").getText().toString()) && !hasFocus) {
//                    alert.setMessage("numero troppo corto");
//                    alert.show();
//                }
//                    //controlla che ci siano solo numeri
//                if (!FormControl.numberControl(infoTxt.get("phone").getText().toString()) && !hasFocus) {
//                    alert.setMessage("vanno inseriti solo numeri");
//                    alert.show();
//                }

                //controllo se vuoto campo
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
                if(UserHandler.isLogged()){
                    editProfile();
                }else{
                    gatheringInformation();
                }



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
            alert.setMessage(null_msg);
            alert.show();
            for(int i=0;i<11;i++){
                System.out.println(i+" "+emptyValue[i]);

            }
        }

        //controllo password (devono essere diverse le stringhe)

        if(!infoTxt.get("pass1").getText().toString().equals(infoTxt.get("pass2").getText().toString())){
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

            //alert.setMessage("provo il logup");
            //alert.show();

            UserHandler.logup(info);
            Intent intent = new Intent (getApplicationContext(),
                    Home.class);
            startActivity(intent);
        }else{
            alert.setMessage(email_msg);
            alert.show();
        }


    }

    private void editProfile(){

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

        UserHandler.editProfile(info);
        Intent intent = new Intent (getApplicationContext(),
                Home.class);
        startActivity(intent);

    }

    private void populate(){

        infoTxt.get("email").setText(profile.getEmail());
        infoTxt.get("pass1").setText(profile.getPassword());
        infoTxt.get("name").setText(profile.getNome());
        infoTxt.get("surname").setText(profile.getCognome());
        infoTxt.get("birth_date").setText(profile.getData_nascita());
        infoTxt.get("birth_city").setText(profile.getLuogo_nascita());
        infoTxt.get("province").setText(profile.getProvincia());
        infoTxt.get("state").setText(profile.getStato());
        infoTxt.get("phone").setText(profile.getTelefono());
        infoTxt.get("personal_number").setText(profile.getCod_fis());
        if(profile.getSesso().equals("Uomo")){
            sex_spinner.setSelection(0);
        }else{
            sex_spinner.setSelection(1);
        }

    }
}


