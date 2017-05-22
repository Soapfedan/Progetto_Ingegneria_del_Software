package com.core.progettoingegneriadelsoftware;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import application.MainApplication;
import application.comunication.ServerComunication;
import application.utility.CSVHandler;
import application.validation.FormControl;

/**
 * Created by Niccolo on 09/05/2017.
 */

public class WelcomeActivity extends AppCompatActivity {


    private int version;

    private static final String BEACONLISTFILE = CSVHandler.getFileBeacon();
    private static final String ROOMLISTFILE = CSVHandler.getFileRoom();

    private static final String runFlag = "runFlag";
    private static final String serverIp = "serverIp";
    private static final String versionID = "versionID";
    private static final String buildingID = "buildingID";

    private Spinner spinner;

    private EditText editText;

    SharedPreferences prefer;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        CSVHandler.createCSV(this);


        prefer = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        spinner = (Spinner) findViewById(R.id.spinner);

        Button select = (Button) findViewById(R.id.button_map);

        TextView textView = (TextView) findViewById(R.id.textView_ip);

        editText = (EditText) findViewById(R.id.ip_text);
        editText.setText(prefer.getString(serverIp,""));

        String ip = prefer.getString(serverIp,"no ip");

        if(!ip.equals("no ip")) {
            ServerComunication.setHostMaster(ip);
            if(!checkVersion()) {
                Toast.makeText(getApplicationContext(), " La versione del file CSV non è aggiornata, la sto richiedendo", Toast.LENGTH_SHORT).show();
                String building = prefer.getString(buildingID,"");

                boolean beaconFile = downloadCSV(building,BEACONLISTFILE);
                boolean roomFile = downloadCSV(building,ROOMLISTFILE);

                int currentVersion = ServerComunication.checkVersion();
                version = currentVersion;

                if(beaconFile && roomFile) {
                    SharedPreferences.Editor edit = prefer.edit();
                    edit.putInt(versionID,version);
                    edit.putString(buildingID,building);
                    edit.commit();
                    Toast.makeText(getApplicationContext(), " Ho aggiornato il CSV, riprova ora", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                changeActivity();
            }
        }


        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkIp(editText.getText().toString())) {
                    String building = spinner.getSelectedItem().toString().toLowerCase();

                    boolean beaconFile = downloadCSV(building,BEACONLISTFILE);
                    boolean roomFile = downloadCSV(building,ROOMLISTFILE);

                    int currentVersion = ServerComunication.checkVersion();
                    version = currentVersion;

                    if(beaconFile && roomFile) {
                        SharedPreferences.Editor edit = prefer.edit();
                        edit.putInt(versionID,version);
                        edit.putString(buildingID,building);
                        edit.commit();
                        changeActivity();
                    }

                }
            }
        });

    }

    protected void onResume() {
        super.onResume();
        MainApplication.setVisible(true);
    }

    protected void onPause() {
        super.onPause();
        MainApplication.setVisible(false);
    }

    private boolean isCSVEmpty(File f) {
        boolean b = false;
        if (f.length()>0) b=true;
        Log.e("csv","bool" + b);
        return b;
    }

    private boolean checkIp(String ip) {
        boolean b;
        boolean check;
        boolean handshake = false;
        check = FormControl.ipControl(ip);
        if (check) {
            handshake = ServerComunication.handShake(ip);
            if (!handshake) Toast.makeText(getApplicationContext(), " La comunicazione col server è fallita ", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(), " L'ip è scritto male ", Toast.LENGTH_SHORT).show();
        }

        b = (check && handshake);
        if (b) {
            SharedPreferences.Editor edit = prefer.edit();
            edit.putString(serverIp,ip);
            edit.commit();
            ServerComunication.setHostMaster(ip);
        }

        return b;
    }

    private boolean checkVersion() {
        version = prefer.getInt(versionID,0);
        int currentVersion = ServerComunication.checkVersion();

        Log.i("version","version " + version + " current " + currentVersion);

        boolean b = (version==currentVersion);
        return b;
    }

    private boolean downloadCSV(String building, String fileName) {
        boolean b = true;
        HashMap<String,String>[] ma = null;
        try {
            if (fileName.equals(BEACONLISTFILE)) {
                ma = ServerComunication.getBuildingBeacon(building);
            }
            else if (fileName.equals(ROOMLISTFILE)) {
                ma = ServerComunication.getBuildingRoom(building);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
            b = false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            b = false;
        } catch (JSONException e) {
            e.printStackTrace();
            b = false;
        }

        try {
            CSVHandler.updateCSV(ma,getBaseContext(),fileName);
        } catch (IOException e) {
            e.printStackTrace();
            b = false;
        } catch (NullPointerException e) {
            Log.e("CSV Error","Update CSV Error");
            b = false;
        } catch (Exception e) {
            b = false;
        }

        if (b) b = isCSVEmpty(CSVHandler.getFiles().get(fileName));

        return b;
    }


    private void changeActivity() {
        startActivity(new Intent(getBaseContext(),Home.class));
        this.finish();
    }
}
