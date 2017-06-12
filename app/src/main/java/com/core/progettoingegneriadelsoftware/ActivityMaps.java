package com.core.progettoingegneriadelsoftware;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.view.View;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutionException;

import application.MainApplication;
import application.beacon.BeaconScanner;
import application.comunication.ServerComunication;
import application.maps.*;
import application.maps.components.Floor;
import application.maps.components.Room;
import application.sharedstorage.Data;
import application.utility.CSVHandler;

import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *  Classe attraverso cui è possibile selezionare un punto da ricercare.
 */

public class ActivityMaps extends AppCompatActivity {
        //indica il piano da ricercare, selezionato dall'utente
    private Floor selectedFloor;
        //indica la stanza da ricercare, selezionata dall'utente
    private Room selectedRoom;
        //elementi grafici
    private Button b_search;
    private Spinner spinner_floor;
    private Spinner spinner_room;
    private ImageView image;
        //insieme dei nomi di piano che compaiono nello spinner
    private ArrayList<String> floorsname;
    private JSONObject s;
    private int resID;
        //identificativo del messaggio che si può ricevere
    private static final String STARTMAPS = "STARTMAPS";
        //flag usato per evitare che si prema più volte il bottone
    private boolean buttonPressed;
        //messaggio impacchettato nell'intent per passare informazioni alla creazione di FullScreenMap
    private String message;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        floorsname = new ArrayList<>();

        getSupportActionBar().setTitle("Go Safe!");

        selectedRoom = new Room(null,null,null,0);

        floorsname = createNamesArray();
        createIcon();

    }

    protected void onStart() {
        super.onStart();
        MainApplication.setCurrentActivity(this);
            //inizializzato filtro per i messaggi
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(STARTMAPS);

        buttonPressed = false;

        if(!MainApplication.controlBluetooth()) MainApplication.activateBluetooth();
            //registrato il receiver nell'activity
        getBaseContext().registerReceiver(broadcastReceiver,intentFilter);

    }

    protected void onResume() {
        super.onResume();
        Log.i("back","onresume");
        MainApplication.setVisible(true);
    }


    protected void onPause() {
        super.onPause();
        MainApplication.setVisible(false);
            //cancellata la registrazione del receiver
        if(broadcastReceiver!=null) getBaseContext().unregisterReceiver(broadcastReceiver);

    }

    /**
     * Metodo per caricare tutti gli elementi dell'interfaccia grafica (gli spinner ed il bottone)
     */
    private void createIcon() {
        selectedFloor = MainApplication.getFloors().get(floorsname.get(0));
        selectedRoom = MainApplication.getFloors().get(floorsname.get(0)).getRooms().get(
                MainApplication.getFloors().get(floorsname.get(0)).nameStringRoom().get(0));
        spinner_floor = (Spinner) findViewById(R.id.spin_floor);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                createNamesArray()
        );
        spinner_floor.setAdapter(adapter);
            //controlla che cosa è stato selezionato sullo spinner del piano
        spinner_floor.setOnItemSelectedListener(new OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {

                for(int i=0;i<floorsname.size();i++) {
                    if (arg2 == i) {
                        selectedFloor = MainApplication.getFloors().get(floorsname.get(i));
                    }
                }

                spinner_room = (Spinner) findViewById(R.id.spin_room);
                ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(
                        ActivityMaps.this,
                        android.R.layout.simple_spinner_item,
                        selectedFloor.nameStringRoom()
                );
                spinner_room.setAdapter(adapter1);
                spinner_room.setOnItemSelectedListener(new OnItemSelectedListener()
                {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int arg2, long arg3) {

                        for(int j=0;j<selectedFloor.nameStringRoom().size();j++) {
                            if (arg2 == j) {
                                selectedRoom = MainApplication.getFloors().get(selectedFloor.getFloorName()).getRooms().get(
                                        MainApplication.getFloors().get(selectedFloor.getFloorName()).nameStringRoom().get(j));
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0)
                    { }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            { }
        });

        b_search = (Button) findViewById(R.id.but_map_search);
        b_search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            if (!buttonPressed) selectImage();
            buttonPressed = true;

            }
        });
    }

    /**
     * Metodo per caricare le immagini della mappa in base al piano ed alla stanza ricercata
     */
    private void selectImage() {
            //
        String floor = selectedFloor.getFloorName();
        String room = selectedRoom.getCod();

        message = floor.concat(";").concat(room);

//        String map = "m".concat(floor).concat("_color");
//        resID = getResources().getIdentifier(map , "drawable", getPackageName());

            //inviato un messaggio per sospendere lo scan, in vista del passaggio a FullScreenMaps (di conseguenza
            //viene cambiata anche la modalità di scan)
        getApplicationContext().sendBroadcast(new Intent("SuspendScan"));

        if(!MainApplication.controlBluetooth())
            Toast.makeText(getApplicationContext(), "Si è verificato un problema, verificare che il Bluetooth sia attivo", Toast.LENGTH_SHORT).show();
    }

    /**
     * Metodo che restituisce una lista di stringhe, contenente i nomi dei piani
     * @return lista di stringhe contenente i nomi dei piani
     */
    private ArrayList<String> createNamesArray(){
        ArrayList<String> s = new ArrayList();
            Iterator it = MainApplication.getFloors().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                s.add(pair.getKey().toString());
            }
            return s;

    }
    /**
     * Metodo all'interno del quale viene creata e fatta partire la FullScreenMap activity
     */
    private void startFullMaps(){
        Intent intentTWO = new Intent (this.getApplicationContext(),
                FullScreenMap.class);
        intentTWO.putExtra("MAP_ID",message);
        this.startActivity(intentTWO);
        if (Data.getUserPosition().getFloor()!=null) comunicateDestination();
    }

    private void comunicateDestination() {
        if(Data.getUserPosition().getFloor().equals(selectedFloor.getFloorName())) {
            Toast.makeText(getApplicationContext(), " Ti trovi sullo stesso piano della tua destinazione, dirigiti" +
                    "verso la bandierina", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(), " La tua destinazione non si trova su questo piano, dirigiti" +
                    "verso le scale", Toast.LENGTH_SHORT).show();
        }
    }

        //il broadcast receiver deputato alla ricezione dei messaggi
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("ACTIVTY MAPS","ricevuto broadcast: " + intent.getAction());
                //questo messaggio viene ricevuto quando si deve passare alla FullScreenMaps
            if(intent.getAction().equals(STARTMAPS)) {

                startFullMaps();
            }
        }
    };
}

