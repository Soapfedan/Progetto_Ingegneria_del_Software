package com.core.progettoingegneriadelsoftware;

import android.app.Activity;
import android.content.Context;
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

import org.json.JSONException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import application.MainApplication;
import application.comunication.ServerComunication;
import application.comunication.message.MessageBuilder;
import application.maps.MapLoader;
import application.maps.components.Room;
import application.user.UserHandler;
import application.utility.CSVHandler;

/**
 * Created by Niccolo on 09/05/2017.
 */

public class WelcomeActivity extends AppCompatActivity {

    private boolean mapSelected;
    private boolean alreadyRun;

    private static final String BEACONLISTFILE = "beaconlist";
    private static final String ROOMLISTFILE = "roomlist";

    private static final String runFlag = "runFlag";

    private Spinner spinner;

    SharedPreferences prefer;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        CSVHandler.createCSV(this);

        prefer = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        alreadyRun = prefer.getBoolean(runFlag,false);

        if(!alreadyRun) {

            mapSelected = false;
            Button select = (Button) findViewById(R.id.button_map);
            Button signUp = (Button) findViewById(R.id.button_signup);

            spinner = (Spinner) findViewById(R.id.spinner);

            Log.i("low",spinner.getSelectedItem().toString().toLowerCase());

            final Context context = this.getBaseContext();

            select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String building = spinner.getSelectedItem().toString().toLowerCase();

                    //TODO get piano (update piano)
                    try {
                        CSVHandler.updateCSV(ServerComunication.getBuildingBeacon(building),getBaseContext(),CSVHandler.getFileBeacon());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //TODO get beaconlist (update beaconlist)
                    try {
                        CSVHandler.updateCSV(ServerComunication.getBuildingRoom(building),getBaseContext(),CSVHandler.getFileRoom());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                    CSVHandler.readCSV(BEACONLISTFILE,context);
                    CSVHandler.readCSV(ROOMLISTFILE,context);
                    //TODO controllo su correttezza e consistenza dati

                    if (CSVHandler.getFiles().get(CSVHandler.getFileBeacon()).length()>0 &&
                            CSVHandler.getFiles().get(CSVHandler.getFileRoom()).length()>0) {

                        SharedPreferences.Editor edit = prefer.edit();
                        edit.putBoolean(runFlag,true);
                        edit.commit();
                        changeActivity("home");
                    }


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
