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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.File;
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
import application.validation.FormControl;

/**
 * Created by Niccolo on 09/05/2017.
 */

public class WelcomeActivity extends AppCompatActivity {

    private boolean mapSelected;
    private boolean alreadyRun;
    private int version;

    private static final String BEACONLISTFILE = "beaconlist";
    private static final String ROOMLISTFILE = "roomlist";

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

        alreadyRun = prefer.getBoolean(runFlag,false);

        spinner = (Spinner) findViewById(R.id.spinner);

        Button select = (Button) findViewById(R.id.button_map);

        TextView textView = (TextView) findViewById(R.id.textView_ip);

        editText = (EditText) findViewById(R.id.ip_text);
        editText.setText(prefer.getString(serverIp,""));

        if(!alreadyRun) {

            mapSelected = false;


        }
        else {
            String ip = prefer.getString(serverIp,"no ip");
            ServerComunication.setHostMaster(ip);
            if(!checkVersion()) {
                Toast.makeText(getApplicationContext(), " La versione del file CSV non è aggiornata, la sto richiedendo", Toast.LENGTH_SHORT).show();
                String building = prefer.getString(buildingID,"");

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
                } catch (NullPointerException e) {
                    Log.e("CSV Error","Update CSV Error");
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
                } catch (NullPointerException e) {
                    Log.e("CSV Error","Update CSV Error");
                }

                int currentVersion = ServerComunication.checkVersion();
                version = currentVersion;

                SharedPreferences.Editor edit = prefer.edit();
                edit.putInt(versionID,version);
                edit.commit();
                Toast.makeText(getApplicationContext(), " Ho aggiornato il CSV, riprova ora", Toast.LENGTH_SHORT).show();
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
                } catch (NullPointerException e) {
                    Log.e("CSV Error","Update CSV Error");
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
                } catch (NullPointerException e) {
                    Log.e("CSV Error","Update CSV Error");
                }

                int currentVersion = ServerComunication.checkVersion();
                version = currentVersion;

                if(isCSVEmpty(CSVHandler.getFiles().get(CSVHandler.getFileBeacon())) &&
                        isCSVEmpty(CSVHandler.getFiles().get(CSVHandler.getFileRoom()))) {
                    SharedPreferences.Editor edit = prefer.edit();
                    edit.putBoolean(runFlag,true);
                    edit.putInt(versionID,version);
                    edit.putString(buildingID,building);
                    edit.commit();
                    changeActivity();
                }

            }
            }
        });

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

    private void changeActivity() {
        startActivity(new Intent(getBaseContext(),Home.class));
        this.finish();
    }
}
