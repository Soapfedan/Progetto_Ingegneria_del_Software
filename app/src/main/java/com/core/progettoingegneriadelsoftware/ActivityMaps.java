package com.core.progettoingegneriadelsoftware;

/**
 * Created by Niccolò on 28/12/2016.
 */
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutionException;

import application.MainApplication;
import application.comunication.ServerComunication;
import application.comunication.Todo;
import application.maps.*;
import application.maps.components.Floor;
import application.maps.components.Room;

import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Niccolò on 27/12/2016.
 */

public class ActivityMaps extends AppCompatActivity {

    private Floor selectedFloor;
    private Room selectedRoom;
        //elementi grafici
    private Button b_search;
    private Spinner spinner_floor;
    private Spinner spinner_room;
    private ImageView image;
    private ArrayList<String> floorsname;
    private JSONObject s;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        floorsname = new ArrayList<>();
        ArrayList<String[]> roomsList;
        //selectedFloor = new Floor();
        selectedRoom = new Room(null,null,null,0);


        try {
            s = new ServerComunication().execute().get();
            System.out.println("Risultato "+s.getString("description")+" "+s.getString("summary"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }  catch (JSONException e) {
        e.printStackTrace();
    }





        InputStream inputStreamRooms = getResources().openRawResource(R.raw.roomlist);
        roomsList = MapLoader.read(inputStreamRooms);
        loadRooms(roomsList);
        floorsname = createNamesArray();
        createIcon();

    }

    protected void onStart() {
        super.onStart();

    }
        //aggiungo manualmente elementi alle barre, dopo andrà fatto diversamente
    private void loadRooms(ArrayList<String[]> b) {

        int[] coords = new int[2];
        double width;
        String cod;
        HashMap<String,Floor> f = new HashMap<>();
        MainApplication.setFloors(f);
        for (String[] roomslist : b) {
            if(MainApplication.getFloors().containsKey(roomslist[2])) {    //il piano esiste
                coords[0] = Integer.parseInt(roomslist[0]); //x
                coords[1] = Integer.parseInt(roomslist[1]); //y
                width = Double.parseDouble(roomslist[3].replace(",","."));
                cod = roomslist[4];
                MainApplication.getFloors().get(roomslist[2]).addRoom(cod,new Room(cod,coords.clone(),roomslist[2],width));
            }else{
                MainApplication.getFloors().put(roomslist[2],new Floor(roomslist[2]));//aggiungo il nuovo piano
                //aggiunto il nodo
                coords[0] = Integer.parseInt(roomslist[0]); //x
                coords[1] = Integer.parseInt(roomslist[1]); //y
                width = Double.parseDouble(roomslist[3].replace(",","."));     //larghezza
                cod = roomslist[4];                         //codice
                MainApplication.getFloors().get(roomslist[2]).addRoom(cod,new Room(cod,coords.clone(),roomslist[2],width));
            }



        }


    }

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
               // Toast.makeText(getApplicationContext(),
                        //"cercata stanza " + selectedRoom.getName() + " nel piano " + selectedFloor.getName() ,Toast.LENGTH_SHORT).show();
               // b_search.setVisibility(View.INVISIBLE);
                //spinner_floor.setVisibility(View.INVISIBLE);
                //spinner_room.setVisibility(View.INVISIBLE);
                selectImage();

            }
        });
    }

    private void selectImage() {

        String floor = selectedFloor.getFloorName();
        String room = selectedRoom.getCod();
        String map = "m".concat(floor).concat("_color");
        int resID = getResources().getIdentifier(map , "drawable", getPackageName());

        Intent intent = new Intent (getApplicationContext(),
                FullScreenMap.class);
        intent.putExtra("MAP_ID", resID);
        startActivity(intent);
    }

    //a partire da arrayList di stanze ne crea uno parallelo con i nomi delle stanze
    private ArrayList<String> createNamesArray(){
        ArrayList<String> s = new ArrayList();
            Iterator it = MainApplication.getFloors().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                s.add(pair.getKey().toString());
                //it.remove(); // avoids a ConcurrentModificationException
            }
            return s;

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
        //ServerComunication.disconnect();
    }
}

