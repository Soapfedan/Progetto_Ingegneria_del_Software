package com.core.progettoingegneriadelsoftware;

/**
 * Created by Niccolò on 28/12/2016.
 */
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.content.Intent;
import android.view.View;
import java.util.*;
import application.maps.*;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.ImageView;

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


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Button b_exit = (Button) findViewById(R.id.but_map_exit);
        b_exit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent (getApplicationContext(),
                        Home.class);
                startActivity(intent);
            }
        });

        ArrayList b = new ArrayList<Floor>();

        selectedFloor = new Floor();
        selectedRoom = new Room();

        createMaps(b);
        createIcon(b);

    }

    protected void onStart() {
        super.onStart();

    }
        //aggiungo manualmente elementi alle barre, dopo andrà fatto diversamente
    private void createMaps(ArrayList b) {
        Floor f_160 = new Floor("160");
        Floor f_155 = new Floor("155");
        Floor f_150 = new Floor("150");
        f_160.addRoom("160/1");
        f_160.addRoom("160/2");
        f_160.addRoom("160/3");
        f_155.addRoom("155/1");
        f_155.addRoom("155/2");
        f_155.addRoom("155/3");
        f_150.addRoom("150/1");
        f_150.addRoom("150/2");
        f_150.addRoom("150/3");
        b.add(f_160);
        b.add(f_155);
        b.add(f_150);
    }

    private void createIcon(final ArrayList<Floor> b) {
        selectedFloor = b.get(0);
        selectedRoom = b.get(0).getRooms().get(0);
        spinner_floor = (Spinner) findViewById(R.id.spin_floor);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item,
                createArray(b)
        );
        spinner_floor.setAdapter(adapter);
            //controlla che cosa è stato selezionato sullo spinner del piano
        spinner_floor.setOnItemSelectedListener(new OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {

                if (arg2 == 0) {
                    selectedFloor = b.get(0);
                } else if (arg2 == 1) {
                    selectedFloor = b.get(1);
                } else {
                    selectedFloor = b.get(2);
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

                        if (arg2 == 0) {
                            selectedRoom = selectedFloor.getRooms().get(0);
                        } else if (arg2 == 1) {
                            selectedRoom = selectedFloor.getRooms().get(1);
                        } else {
                            selectedRoom = selectedFloor.getRooms().get(2);
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
                Toast.makeText(getApplicationContext(),
                        "cercata stanza " + selectedRoom.getName() + " nel piano " + selectedFloor.getName() ,Toast.LENGTH_SHORT).show();
                b_search.setVisibility(View.INVISIBLE);
                spinner_floor.setVisibility(View.INVISIBLE);
                spinner_room.setVisibility(View.INVISIBLE);
                setContentView(R.layout.activity_maps_scrool);
                //selectImage(selectedFloor,selectedRoom);
                //image.setVisibility(View.VISIBLE);
            }
        });
    }

    private void selectImage(Floor f, Room r) {
        image = (ImageView) findViewById(R.id.map_image);
        if (selectedFloor.getName().equals("160") && selectedRoom.getName().equals("160/1"))
            image.setImageResource(R.drawable.gargantua1);
    }

    //a partire da arrayList di stanze ne crea uno parallelo con i nomi delle stanze
    private ArrayList<String> createArray(ArrayList<Floor> floors) {
        ArrayList<String> s = new ArrayList();
        for (Floor f : floors) {
            s.add(f.getName());
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
    }
}

