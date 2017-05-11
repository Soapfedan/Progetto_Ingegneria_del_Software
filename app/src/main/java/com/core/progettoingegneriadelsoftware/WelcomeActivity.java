package com.core.progettoingegneriadelsoftware;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import application.MainApplication;
import application.comunication.message.MessageBuilder;
import application.maps.MapLoader;
import application.maps.components.Room;
import application.user.UserHandler;

/**
 * Created by Niccolo on 09/05/2017.
 */

public class WelcomeActivity extends AppCompatActivity {

    private boolean mapSelected;
    private boolean alreadyRun;

    private static final String runFlag = "runFlag";

    private Spinner spinner;

    SharedPreferences prefer;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        prefer = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        alreadyRun = prefer.getBoolean(runFlag,false);

        if(!alreadyRun) {

            mapSelected = false;
            Button select = (Button) findViewById(R.id.button_map);
            Button signUp = (Button) findViewById(R.id.button_signup);

            spinner = (Spinner) findViewById(R.id.spinner);

            select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String builing = spinner.getSelectedItem().toString();

                    ArrayList<String> key = new ArrayList<String>();
                    ArrayList<String> value = new ArrayList<String>();

                    key.add("building");
                    value.add(builing);

                    String mex = MessageBuilder.builder(key,value,1,0);

                    //TODO get piano (update piano)
                    //TODO get beaconlist (update beaconlist)

                    //TODO controllo su correttezza e consistenza dati

                    SharedPreferences.Editor edit = prefer.edit();
                    edit.putBoolean(runFlag,true);
                    edit.commit();
                    changeActivity("home");
                }
            });

            signUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeActivity("information");
                }
            });

        }
        else {
            changeActivity("home");
        }


    }


    private void changeActivity(String act) {
        Class a = null;
        if (act.equals("home")) {
            a = Home.class;
        }
        else if (act.equals("information")) {
            a = InformationsHandler.class;
        }
        startActivity(new Intent(getBaseContext(),a));
        this.finish();
    }
}
