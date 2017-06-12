package com.core.progettoingegneriadelsoftware;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.Calendar;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import application.MainApplication;
import application.user.UserHandler;
import application.user.UserProfile;
import application.validation.FormControl;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import android.app.DatePickerDialog.OnDateSetListener;
import android.widget.Toast;

/**
 * Classe che gestisce tutte le procedure che coinvolgono il profilo di un utente,
 * quali iscrizione di un nuovo utente, login, logut, modifica profilo.
 */

public class InformationsHandler extends AppCompatActivity {

    private HashMap<String,TextView[]> infoTxt;
    private HashMap<String,TextView> infoTxtView;
    private AlertDialog alert;
    private Button send_b;
    private Spinner sex_spinner;
    private boolean[] emptyValue;
    private boolean[] errorValue;
    private UserProfile profile;

    public static final String null_msg = "Hai inserito un campo obbligatorio vuoto!";
    public static final String error_msg = "Hai inserito un campo errato!";
    public static final String pass_msg = "Hai sbagliato a reinserire la password";
    public static final String email_msg = "Esiste gia' un utente con questa mail";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Go Safe!");
        // Se la variabile editable = 1 allora dovrà modificare il profilo altrimento se è 0 dovrò visualizzare le informazioni (sola lettura)
        int editable = getIntent().getIntExtra("editable",-1);
        if(editable == 0)
        {
            setContentView(R.layout.content_view_information);
            infoTxtView = new HashMap<String, TextView>();
            loadResources(editable);
        }else{
            setContentView(R.layout.activity_informations);
            infoTxt = new HashMap<String, TextView[]>();
            //carico gli elementi della form
            loadResources(editable);
            //registro tutti gli eventi
            loadEvents();
        }

            emptyValue = new boolean[11];
            errorValue = new boolean[11];

            for (int i = 0; i < 11; i++) {
                errorValue[i] = false;
                emptyValue[i] = true;
            }
            
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do nothings
                    }
                });
        alert = builder.create();


            if (UserHandler.isLogged()) {
                profile = UserHandler.getInformation(UserHandler.getMail());
                populate(editable);
            }

    }

    protected void onStart() {
        super.onStart();
        MainApplication.setCurrentActivity(this);
        if(!MainApplication.controlBluetooth()) MainApplication.activateBluetooth();
    }

    protected void onResume() {
        super.onResume();
        MainApplication.setVisible(true);
    }

    protected void onPause() {
        super.onPause();
        MainApplication.setVisible(false);
    }

    protected void onStop() {
        super.onStop();
        //MainApplication.getDB().close();
    }


    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * Carico tutte le risorse che si trovano sulla form e le inserisco dentro una hashmap che mi permetterà di lavorarci
     * @param edit parametro che indica se la form dovrà solo visualizzare o modificare i dati dell'utente
     */

    private void loadResources(int edit) {
        if (edit != 0) {
            TextView[] tv = new TextView[2];
            // creo una hashmap con tutti gli elementi di una form
            tv[0] = (TextView) findViewById(R.id.email_txt);
            tv[1] = (TextView) findViewById(R.id.errorEmail);
            infoTxt.put("email", tv.clone());
            if(edit == 1)
                tv[0].setFocusable(false);
            else tv[0].setFocusable(true);

            tv[0] = (TextView) findViewById(R.id.pass_txt1);
            tv[1] = (TextView) findViewById(R.id.errorPass1);
            infoTxt.put("pass1", tv.clone());

            tv[0] = (TextView) findViewById(R.id.pass_txt2);
            tv[1] = (TextView) findViewById(R.id.errorPass2);
            infoTxt.put("pass2", tv.clone());

            tv[0] = (TextView) findViewById(R.id.name_txt);
            tv[1] = (TextView) findViewById(R.id.errorName);
            infoTxt.put("name", tv.clone());

            tv[0] = (TextView) findViewById(R.id.surname_txt);
            tv[1] = (TextView) findViewById(R.id.errorSurname);
            infoTxt.put("surname", tv.clone());

            tv[0] = (TextView) findViewById(R.id.birth_date_txt);
            tv[1] = (TextView) findViewById(R.id.errorBirthDate);
            infoTxt.put("birth_date", tv.clone());
            infoTxt.get("birth_date")[0].setFocusable(false);

            tv[0] = (TextView) findViewById(R.id.birth_city_txt);
            tv[1] = (TextView) findViewById(R.id.errorBirthCity);
            infoTxt.put("birth_city", tv.clone());

            tv[0] = (TextView) findViewById(R.id.province_txt);
            tv[1] = (TextView) findViewById(R.id.errorProvince);
            infoTxt.put("province", tv.clone());

            tv[0] = (TextView) findViewById(R.id.state_txt);
            tv[1] = (TextView) findViewById(R.id.errorState);
            infoTxt.put("state", tv.clone());

            tv[0] = (TextView) findViewById(R.id.phone_txt);
            tv[1] = (TextView) findViewById(R.id.errorPhone);
            infoTxt.put("phone", tv.clone());

            tv[0] = (TextView) findViewById(R.id.personal_number_txt);
            tv[1] = (TextView) findViewById(R.id.errorPersonalNumber);
            infoTxt.put("personal_number", tv.clone());


            send_b = (Button) findViewById(R.id.profile_button);
            sex_spinner = (Spinner) findViewById(R.id.sex_spinner);


        } else {
            TextView tv;
            // creo una hashmap con tutti gli elementi di una form
            tv = (TextView) findViewById(R.id.email_txt);
            infoTxtView.put("email", tv);

            tv = (TextView) findViewById(R.id.name);
            infoTxtView.put("name", tv);

            tv = (TextView) findViewById(R.id.surname);
            infoTxtView.put("surname", tv);

            tv = (TextView) findViewById(R.id.birth_city);
            infoTxtView.put("birth_city", tv);

            tv = (TextView) findViewById(R.id.province);
            infoTxtView.put("province", tv);

            tv = (TextView) findViewById(R.id.state);
            infoTxtView.put("state", tv);

            tv = (TextView) findViewById(R.id.phone);
            infoTxtView.put("phone", tv);

            tv = (TextView) findViewById(R.id.personal_number);
            infoTxtView.put("personal_number", tv);

            tv = (TextView) findViewById(R.id.birth_date);
            infoTxtView.put("birth_date", tv);

            tv = (TextView) findViewById(R.id.sex);
            infoTxtView.put("sex", tv);
        }
    }

        //aggiorna il textView di date_birth
    private void updateDisplay(int mMonth, int mDay, int mYear) {
        infoTxt.get("birth_date")[0].setText(
                new StringBuilder()
                        .append(mMonth + 1).append("-")
                        .append(mDay).append("-")
                        .append(mYear).append(" "));
    }

    /**
     * Metodo che setta per ogni elemento all'interno della hashmap (cioè tutte le textview della forma che conterranno le informazioni)
     * un comportantmento in risposta al cambio di focus sull'elemento, in particolar modo, andremo a controllare la text,
     * vedendo se il contenuto è non conforme con la regola Regex imposta o se è vuota. Nel primo caso la registreremo nell'elenco degli elementi
     * con contenuto errato, altrimento nella lista delle text vuote.
     */

    private void loadEvents(){

        final int emptyColor = Color.rgb(255,255,153);
        final int errorColor = Color.RED;
        final int worthColor = Color.GREEN;

        infoTxt.get("email")[0].setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if( infoTxt.get("email")[0].getText().toString().isEmpty()) {
                    infoTxt.get("email")[0].setBackgroundColor(emptyColor);
                    emptyValue[0] = true;
                }else if (!FormControl.mailControl(infoTxt.get("email")[0].getText().toString())) {
                    //alert.setMessage("l'email deve contenere solo lettere, @ e punti");
                    //
                    infoTxt.get("email")[0].setBackgroundColor(errorColor);
                    infoTxt.get("email")[1].setVisibility(View.VISIBLE);
                    errorValue[0] = true;

                }else{
                    infoTxt.get("email")[0].setBackgroundColor(worthColor);
                    infoTxt.get("email")[1].setVisibility(View.INVISIBLE);
                    emptyValue[0] = false;

                }
            }
        });

        infoTxt.get("pass1")[0].setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if( infoTxt.get("pass1")[0].getText().toString().isEmpty()){
                    infoTxt.get("pass1")[0].setBackgroundColor(emptyColor);
                    emptyValue[1] = true;
                }else if (!FormControl.passwordControl(infoTxt.get("pass1")[0].getText().toString())){
                    //alert.setMessage("la password deve contenere almeno 8 caratteri");
                    //alert.show();
                    infoTxt.get("pass1")[0].setBackgroundColor(errorColor);
                    infoTxt.get("pass1")[1].setVisibility(View.VISIBLE);
                    errorValue[1] = true;
                }else{
                    infoTxt.get("pass1")[0].setBackgroundColor(worthColor);
                    infoTxt.get("pass1")[1].setVisibility(View.INVISIBLE);
                    emptyValue[1] = false;
                    errorValue[1] = false;
                }
            }
        });

        infoTxt.get("pass2")[0].setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if( infoTxt.get("pass2")[0].getText().toString().isEmpty()){
                    infoTxt.get("pass2")[0].setBackgroundColor(emptyColor);
                    emptyValue[2] = true;
                }else if (infoTxt.get("pass2")[0].getText().toString().compareTo(infoTxt.get("pass1")[0].getText().toString())!=0) {
                    //alert.setMessage("la password deve contenere almeno 8 caratteri");
                    //alert.show();
                    infoTxt.get("pass2")[0].setBackgroundColor(errorColor);
                    infoTxt.get("pass2")[1].setVisibility(View.VISIBLE);
                    errorValue[2] = true;
                }else{
                    infoTxt.get("pass2")[0].setBackgroundColor(worthColor);
                    infoTxt.get("pass2")[1].setVisibility(View.INVISIBLE);
                    emptyValue[2] = false;
                    errorValue[2] = false;
                }
            }
        });

        infoTxt.get("name")[0].setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                    //controlla che sia composto solo da lettere e spazi
                if( infoTxt.get("name")[0].getText().toString().isEmpty()){
                    infoTxt.get("name")[0].setBackgroundColor(emptyColor);
                    emptyValue[3] = true;
                }else if (!FormControl.letterControl(infoTxt.get("name")[0].getText().toString())) {
                    //alert.setMessage("il nome deve contenere solo lettere");
                    //alert.show();
                    infoTxt.get("name")[0].setBackgroundColor(errorColor);
                    infoTxt.get("name")[1].setVisibility(View.VISIBLE);
                    errorValue[3] = true;
                }else{
                    infoTxt.get("name")[0].setBackgroundColor(worthColor);
                    infoTxt.get("name")[1].setVisibility(View.INVISIBLE);
                    emptyValue[3] = false;
                    errorValue[3] = false;
                }
            }
        });

        infoTxt.get("surname")[0].setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                    //controlla che sia composto solo da lettere e spazi
                if( infoTxt.get("surname")[0].getText().toString().isEmpty()){
                    infoTxt.get("surname")[0].setBackgroundColor(emptyColor);
                    emptyValue[4] = true;
                }else if (!FormControl.letterControl(infoTxt.get("surname")[0].getText().toString())) {
                    //alert.setMessage("il cognome deve contenere solo lettere");
                    //alert.show();
                    infoTxt.get("surname")[0].setBackgroundColor(errorColor);
                    infoTxt.get("surname")[1].setVisibility(View.VISIBLE);
                    errorValue[4] = true;
                }else{
                    infoTxt.get("surname")[0].setBackgroundColor(worthColor);
                    infoTxt.get("surname")[1].setVisibility(View.INVISIBLE);
                    emptyValue[4] = false;
                    errorValue[4] = false;
                }

            }
        });

        infoTxt.get("birth_date")[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Calendar mcurrentDate=Calendar.getInstance();
                    int mYear=mcurrentDate.get(Calendar.YEAR);
                    int mMonth=mcurrentDate.get(Calendar.MONTH);
                    int mDay=mcurrentDate.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog mDatePicker=new DatePickerDialog(InformationsHandler.this,new OnDateSetListener() {
                        public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                            int year = selectedyear;
                            int month = selectedmonth;
                            int day = selectedday;
                            updateDisplay(month,day,year);
                        }
                    },mYear, mMonth, mDay);
                    mDatePicker.setTitle("Select date");
                    //quando clicco cancel nel dialog non deve rimanere la tastiera
                    mDatePicker.setButton(DialogInterface.BUTTON_NEGATIVE, "cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == DialogInterface.BUTTON_NEGATIVE) {
                                //TODO deve far scomparire la tastiera se clicco cancel nel dialog
                                infoTxt.get("birth_date")[0].clearFocus();
                            }
                        }
                    });
                    mDatePicker.show();
                    updateDisplay(mMonth,mDay,mYear);

                if( infoTxt.get("birth_date")[0].getText().toString().isEmpty()){
                    infoTxt.get("birth_date")[0].setBackgroundColor(emptyColor);
                    emptyValue[5] = true;
                }else{
                    infoTxt.get("birth_date")[0].setBackgroundColor(worthColor);
                    emptyValue[5] = false;
                    errorValue[5] = false;
                }
            }

        });

        infoTxt.get("birth_city")[0].setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                    //controlla che sia composto solo da lettere e spazi
                if( infoTxt.get("birth_city")[0].getText().toString().isEmpty()){
                    infoTxt.get("birth_city")[0].setBackgroundColor(emptyColor);
                    emptyValue[6] = true;
                }else if (!FormControl.letterControl(infoTxt.get("birth_city")[0].getText().toString())) {
                    //alert.setMessage("la città deve contenere solo lettere");
                    //alert.show();
                    infoTxt.get("birth_city")[0].setBackgroundColor(errorColor);
                    infoTxt.get("birth_city")[1].setVisibility(View.VISIBLE);
                    errorValue[6] = true;
                }else{
                    infoTxt.get("birth_city")[0].setBackgroundColor(worthColor);
                    infoTxt.get("birth_city")[1].setVisibility(View.INVISIBLE);
                    emptyValue[6] = false;
                    errorValue[6] = false;
                }
            }
        });

        infoTxt.get("province")[0].setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                    //controlla che sia composto solo da lettere e spazi
                if( infoTxt.get("province")[0].getText().toString().isEmpty()){
                    infoTxt.get("province")[0].setBackgroundColor(emptyColor);
                    emptyValue[7] = true;
                }else if (!FormControl.letterControl(infoTxt.get("province")[0].getText().toString()) ||
                          !FormControl.provenceControl(infoTxt.get("province")[0].getText().toString())) {
                        //alert.setMessage("la provincia deve contenere solo lettere");
                        //alert.show();
                    infoTxt.get("province")[0].setBackgroundColor(errorColor);
                    infoTxt.get("province")[1].setVisibility(View.VISIBLE);
                    errorValue[7] = true;
                }else{
                    infoTxt.get("province")[0].setBackgroundColor(worthColor);
                    infoTxt.get("province")[1].setVisibility(View.INVISIBLE);
                    emptyValue[7] = false;
                    errorValue[7] = false;
                }
            }
        });

        infoTxt.get("state")[0].setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if( infoTxt.get("state")[0].getText().toString().isEmpty()){
                    infoTxt.get("state")[0].setBackgroundColor(emptyColor);
                    emptyValue[8] = true;
                }else if (!FormControl.letterControl(infoTxt.get("state")[0].getText().toString())) {
                    //alert.setMessage("lo stato deve contenere solo lettere");
                    //alert.show();
                    infoTxt.get("state")[0].setBackgroundColor(errorColor);
                    infoTxt.get("state")[1].setVisibility(View.VISIBLE);
                    errorValue[8] = true;
                }else{
                    infoTxt.get("state")[0].setBackgroundColor(worthColor);
                    infoTxt.get("state")[1].setVisibility(View.INVISIBLE);
                    emptyValue[8] = false;
                    errorValue[8] = false;
                }
            }
        });


        infoTxt.get("phone")[0].setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //controllo se abbastanza lungo, solo quando esco dal text (!hasFocus serve per quello)
                if( infoTxt.get("phone")[0].getText().toString().isEmpty()){
                    infoTxt.get("phone")[0].setBackgroundColor(emptyColor);
                    emptyValue[9] = true;
                }else if (!FormControl.phoneControl(infoTxt.get("phone")[0].getText().toString()) ||
                        !FormControl.numberControl(infoTxt.get("phone")[0].getText().toString())) {
                    //alert.setMessage("numero troppo corto");
                    //alert.show();
                    infoTxt.get("phone")[0].setBackgroundColor(errorColor);
                    infoTxt.get("phone")[1].setVisibility(View.VISIBLE);
                    errorValue[9] = true;
                }else{
                    infoTxt.get("phone")[0].setBackgroundColor(worthColor);
                    infoTxt.get("phone")[1].setVisibility(View.INVISIBLE);
                    emptyValue[9] = false;
                    errorValue[9] = false;
                }
            }
        });

        infoTxt.get("personal_number")[0].setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if( infoTxt.get("personal_number")[0].getText().toString().isEmpty()){
                    infoTxt.get("personal_number")[0].setBackgroundColor(emptyColor);
                    emptyValue[10] = true;
                }else if (!FormControl.PersonalNumberControl(infoTxt.get("personal_number")[0].getText().toString())){
                    //alert.setMessage("numero troppo corto");
                    //alert.show();
                    infoTxt.get("personal_number")[0].setBackgroundColor(errorColor);
                    infoTxt.get("personal_number")[1].setVisibility(View.VISIBLE);
                    errorValue[10] = true;
                }else{
                    infoTxt.get("personal_number")[0].setBackgroundColor(worthColor);
                    infoTxt.get("personal_number")[1].setVisibility(View.INVISIBLE);
                    errorValue[10] = false;
                    errorValue[10] = false;
                }
            }
        });


        send_b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                send_b.setFocusable(true);
                send_b.setFocusableInTouchMode(true);///add this line
                send_b.requestFocus();
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


    }

    private void gatheringInformation(){
        boolean isFinished = false;
        //campo vuoto
        boolean error = false;
        //check if some value is empty
        for(int i=0;i<11;i++){
            if(errorValue[i]==true){
                error = true;
                alert.setMessage(error_msg);
            }
        }
        for(int i=0;i<5;i++) {
            if(emptyValue[i]==true && error==false) {
                error = true;
                alert.setMessage(null_msg);
            }
        }

        if(!infoTxt.get("pass1")[0].getText().toString().equals(infoTxt.get("pass2")[0].getText().toString())){
            error=true;
        }

        if(error==true){
            alert.show();
        }else {
            HashMap<String, String> info = new HashMap<>();
            info.put("email", infoTxt.get("email")[0].getText().toString());
            info.put("pass", infoTxt.get("pass1")[0].getText().toString());
            info.put("name", infoTxt.get("name")[0].getText().toString());
            info.put("surname", infoTxt.get("surname")[0].getText().toString());
            info.put("birth_date", infoTxt.get("birth_date")[0].getText().toString());
            info.put("birth_city", infoTxt.get("birth_city")[0].getText().toString());
            info.put("province", infoTxt.get("province")[0].getText().toString());
            info.put("state", infoTxt.get("state")[0].getText().toString());
            info.put("phone", infoTxt.get("phone")[0].getText().toString());
            info.put("personal_number", infoTxt.get("personal_number")[0].getText().toString());
            info.put("sex", sex_spinner.getSelectedItem().toString());

            if(UserHandler.logup(info)) {


                isFinished = true;

                Toast.makeText(getApplicationContext(),
                        "Registrazione avvenuta con successo!",Toast.LENGTH_SHORT).show();

            } else {
                alert.setMessage(email_msg);
                alert.show();
            }
        }

        if (isFinished) finish();

    }

    /**
     * Metodo che permette la modifica del profilo solo se tutti i controlli hanno esito positivo (controllo se non è vuota e se il contenuto
     * è conforme alla regola Regex
     */

    private void editProfile(){
        //campo vuoto
        boolean error = false;
        //check if some value is empty
        for(int i=0;i<11;i++){
            if(errorValue[i]==true){
                error = true;
                alert.setMessage(error_msg);
            }
        }
        for(int i=0;i<5;i++) {
            if(emptyValue[i]==true && error==false) {
                error = true;
                alert.setMessage(null_msg);
            }
        }

        if(!infoTxt.get("pass1")[0].getText().toString().equals(infoTxt.get("pass2")[0].getText().toString())){
            error=true;
            alert.setMessage(pass_msg);
        }

        if(error==true) {
            alert.show();
        }else {
            HashMap<String, String> info = new HashMap<>();
            info.put("email", infoTxt.get("email")[0].getText().toString());
            info.put("pass", infoTxt.get("pass1")[0].getText().toString());
            info.put("name", infoTxt.get("name")[0].getText().toString());
            info.put("surname", infoTxt.get("surname")[0].getText().toString());
            info.put("birth_date", infoTxt.get("birth_date")[0].getText().toString());
            info.put("birth_city", infoTxt.get("birth_city")[0].getText().toString());
            info.put("province", infoTxt.get("province")[0].getText().toString());
            info.put("state", infoTxt.get("state")[0].getText().toString());
            info.put("phone", infoTxt.get("phone")[0].getText().toString());
            info.put("personal_number", infoTxt.get("personal_number")[0].getText().toString());
            info.put("sex", sex_spinner.getSelectedItem().toString());

            UserHandler.editProfile(info);
//            Intent intent = new Intent(getApplicationContext(),
//                    Home.class);
//            startActivity(intent);

            Toast.makeText(getApplicationContext(),
                    "Modifica avvenuta con successo",Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    /**
     * Metodo che popola il contenuto della form, tramite i dati prelevati dal server
     * @param edit
     */

    private void populate(int edit){
        if (edit == 1)
        {
            infoTxt.get("email")[0].setText(profile.getEmail());
            infoTxt.get("pass1")[0].setText(profile.getPassword());
            infoTxt.get("name")[0].setText(profile.getNome());
            infoTxt.get("surname")[0].setText(profile.getCognome());
            infoTxt.get("birth_date")[0].setText(profile.getData_nascita());
            infoTxt.get("birth_city")[0].setText(profile.getLuogo_nascita());
            infoTxt.get("province")[0].setText(profile.getProvincia());
            infoTxt.get("state")[0].setText(profile.getStato());
            infoTxt.get("phone")[0].setText(profile.getTelefono());
            infoTxt.get("personal_number")[0].setText(profile.getCod_fis());
            if (profile.getSesso().equals("Uomo")) {
                sex_spinner.setSelection(0);
            } else {
                sex_spinner.setSelection(1);
            }
        }else{
            infoTxtView.get("email").setText(profile.getEmail());
            infoTxtView.get("name").setText(profile.getNome());
            infoTxtView.get("surname").setText(profile.getCognome());
            infoTxtView.get("birth_date").setText(profile.getData_nascita());
            infoTxtView.get("birth_city").setText(profile.getLuogo_nascita());
            infoTxtView.get("province").setText(profile.getProvincia());
            infoTxtView.get("state").setText(profile.getStato());
            infoTxtView.get("phone").setText(profile.getTelefono());
            infoTxtView.get("personal_number").setText(profile.getCod_fis());
            infoTxtView.get("sex").setText(profile.getSesso());

        }
    }
}


