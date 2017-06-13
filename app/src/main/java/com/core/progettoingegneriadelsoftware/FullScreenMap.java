package com.core.progettoingegneriadelsoftware;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import application.MainApplication;
import application.maps.components.Notify;
import application.maps.grid.TouchImageView;
import application.sharedstorage.Data;
import application.sharedstorage.DataListener;
import application.sharedstorage.UserPositions;


/**
 *  Classe che visualizza la mappa di un piano corredandola con posizione dell'utente,
 *  destinazione da raggiungere ed eventuali notifiche
 */

public class FullScreenMap extends AppCompatActivity implements DataListener{
        //indica le volte che si clicca BackButton
    private int backpress;
    //menu laterale
    private NavigationView navigationView;
        //serve per eventuali errori durante il login
    private AlertDialog alert;
    private SharedPreferences prefer;
    private TouchImageView image;
    private int[] position; //0->x 1->y

    final private int[] imageDim = {1600,1000};

    // These matrices will be used to move and zoom image
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();

    // We can be in one of these 3 states
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;

    // Remember some things for zooming
    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;
    private String savedItemClicked;

    private static final long timerPeriod = 20000l;

    private int s;

    private String selectedFloor;
    private String selectedRoom;
    private String currentFloor;

    private Handler handler;
    private ArrayList<Notify> notifies;
        //flag per permettere di capire quando ci si trova in uno stato di emergenza e l'app viene messa in background
    private boolean backgroundEmergency;
    int resID;

    private int[] coords;

    private Bitmap curr_pos;
    private Bitmap destination;
    private Bitmap gas;
    private Bitmap fire;
    private Bitmap earthquake;
    private Bitmap danger;

    private static final String EXIT_MAPS = "EXIT_MAPS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_maps_scrool);
        getSupportActionBar().setTitle("Go Safe!");
        handler = new Handler();
        notifies = new ArrayList<>();

        //Carico le differenti immagini da visualizzare sulla mappa
        curr_pos = BitmapFactory.decodeResource(getResources(), R.drawable.current_position);
        destination = BitmapFactory.decodeResource(getResources(), R.drawable.destination);
        danger = BitmapFactory.decodeResource(getResources(), R.drawable.danger);
        gas = BitmapFactory.decodeResource(getResources(), R.drawable.gas);
        fire = BitmapFactory.decodeResource(getResources(), R.drawable.fire);
        earthquake = BitmapFactory.decodeResource(getResources(), R.drawable.earthquake);

        s = 0;
        position = new int[2];
        //Registro la classe all'interno delle due strutture dati
        //in modo tale viene richiamato il suo metodo retrive al cambio della posizione dell'utente o all'arrivo di nuove notifiche
        if(!Data.getUserPosition().getListeners().contains(this)){
            Data.getUserPosition().addDataListener(this);
        }
        if(!Data.getNotification().getListeners().contains(this)){
            Data.getNotification().addDataListener(this);
        }

        Bundle extras = getIntent().getExtras();

        //Recupero l'id della mappa che dovrà essere visualizzata
        if (extras != null) {
            extractDatafromMessage(extras.getString("MAP_ID"));
//            s = extras.getInt("MAP_ID");
            String map = "m".concat(selectedFloor).concat("_color");
            s = getResources().getIdentifier(map , "drawable", getPackageName());
            //The key argument here must match that used in the other activity
        }

//        Toast.makeText(getApplicationContext(), " A breve verrà visualizzata la mappa ", Toast.LENGTH_SHORT).show();

       /* image = (ImageView) findViewById(R.id.imageHelp);
        image.setImageResource(s);
        image.setScaleType(ImageView.ScaleType.FIT_XY);*/
        image = new TouchImageView(this);
        image.setMaxZoom(4f);

        if(MainApplication.getEmergency()) {
            MainApplication.initializeScanner(this,"EMERGENCY");
        }
        else {
            MainApplication.initializeScanner(this,"SEARCHING");
        }
        if(Data.getUserPosition().getPosition()[0]!=0) retrive();
    }


    protected void onStart() {
        super.onStart();
        MainApplication.setCurrentActivity(this);
        if(!MainApplication.controlBluetooth()) MainApplication.activateBluetooth();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(EXIT_MAPS);
        backgroundEmergency = false;
        notifies = Data.getNotification().getNotifies();
        String floor = Data.getUserPosition().getFloor();
        if (floor!=null) {
            String curPos = "m".concat(floor).concat("_color");
            int val = getResources().getIdentifier(curPos , "drawable", getPackageName());

            setImageGrid(val);
            setContentView(image);
            Toast.makeText(getApplicationContext(), " questa mappa rappresenta la tua posizione attuale ", Toast.LENGTH_SHORT).show();
        }
        else {
            setImageGrid(s);
            setContentView(image);
            Toast.makeText(getApplicationContext(), " non trovo sensori, questa mappa rappresenta la destinazione ", Toast.LENGTH_SHORT).show();
        }

        getBaseContext().registerReceiver(broadcastReceiver,intentFilter);
        startTimer();
    }

    protected void onResume() {
        super.onResume();
        MainApplication.setVisible(true);
    }

    protected void onPause() {
        super.onPause();
        stopTimer();
        MainApplication.setVisible(false);
    }

    protected void onStop() {
        super.onStop();
        if(!MainApplication.getEmergency() && broadcastReceiver!=null)
            getBaseContext().unregisterReceiver(broadcastReceiver);
        else
            backgroundEmergency = true;
    }

    private void startTimer() {
        handler.postDelayed(timerTask,timerPeriod);
    }

    private void stopTimer() {
        handler.removeCallbacks(timerTask);
    }

    public void onDestroy() {
        super.onDestroy();
    }

    private void extractDatafromMessage(String mex) {
        String[] m;
        m = mex.split(";");
        selectedFloor = m[0];
        selectedRoom = m[1];
        currentFloor = selectedFloor;
    }

    //Metodo che inserisce tutte le caratteristiche all'interno della mappa, come la posizione dell'utente o le varie notifiche

    private void setImageGrid(int imageId){
        BitmapFactory.Options myOptions = new BitmapFactory.Options();
        myOptions.inDither = true;
        myOptions.inScaled = false;
        myOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// important
        myOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageId, myOptions);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);


        Bitmap workingBitmap = Bitmap.createBitmap(bitmap);
        Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);


        Canvas canvas = new Canvas(mutableBitmap);

        // se la posizione dell'utente è diversa da [0,0]
        if(position[0]!=0&&position[1]!=0) {

            //int[] c = coordsMapping(position);
            //canvas.drawCircle(position[0], position[1], 30, paint); //x y radius paint
            canvas.drawBitmap(curr_pos,position[0],position[1],null);
            image.setImageBitmap(mutableBitmap);

            //se mi trovo sullo stesso piano della destinazione visualizzo l'immagine della destinazione
            if(selectedFloor.equals(currentFloor)) {
                image.setImageBitmap(mutableBitmap);
                if(MainApplication.getEmergency()){
                    String emergency = selectedFloor.concat("EMERGENCY");
                    coords = MainApplication.getFloors().get(selectedFloor).getRooms().get(emergency).getCoords();
                }else{
                    coords = MainApplication.getFloors().get(selectedFloor).getRooms().get(selectedRoom).getCoords();
                }

                canvas.drawBitmap(destination,coords[0],coords[1],null);
                //canvas.drawCircle(coords[0],coords[1],30,new Paint(Color.BLUE));
            }
            else {
                String str = currentFloor.concat("A3");
                image.setImageBitmap(mutableBitmap);
                if(MainApplication.getEmergency()){
                    String emergency = currentFloor.concat("EMERGENCY");
                    Log.e("emergency",""+emergency);
                    coords = MainApplication.getFloors().get(currentFloor).getRooms().get(emergency).getCoords();
                }else {
                    coords = MainApplication.getFloors().get(currentFloor).getRooms().get(str).getCoords();
                }
                canvas.drawBitmap(destination,coords[0],coords[1],null);
            }

            if(!notifies.isEmpty()){//se ci sono delle notifiche scorro l'array e le visualizzo sulla mappa
                Paint pt = new Paint();
                paint.setAntiAlias(true);
                for(int k=0;k<notifies.size();k++){
                    if(currentFloor.equals(notifies.get(k).getFloor())) {
                        int[] c = MainApplication.getFloors().get(currentFloor).getRooms().get(notifies.get(k).getRoom()).getCoords();
                        if(((c[0]==coords[0]&&c[1]==coords[1])||(c[0]==position[0]&&c[1]==position[1]))&&(coords!=null&&position!=null)) {
                            c[0] += 20;
                            c[1] += 20;
                        }
                        switch (notifies.get(k).getCod_cat()){
                            case 1:
                                canvas.drawBitmap(fire,c[0],c[1],null);
                                break;
                            case 2:
                                canvas.drawBitmap(gas,c[0],c[1],null);
                                break;
                            case 3:
                                canvas.drawBitmap(earthquake,c[0],c[1],null);
                                break;
                            default:
                                canvas.drawBitmap(danger,c[0],c[1],null);
                                break;
                        }
                    }
                }
            }
        }
        else {//se la posizione dell'utente non è stata settata ed è ancora quella di default [0,0]
            image.setImageBitmap(mutableBitmap);
            currentFloor="145";
            int[] coords;
            if(MainApplication.getEmergency()){
                String emergency = selectedFloor.concat("EMERGENCY");
                coords = MainApplication.getFloors().get(selectedFloor).getRooms().get(emergency).getCoords();
            }else{
                coords = MainApplication.getFloors().get(selectedFloor).getRooms().get(selectedRoom).getCoords();
            }
            canvas.drawBitmap(destination,coords[0],coords[1],null);
            //canvas.drawCircle(coords[0],coords[1],30,new Paint(Color.BLUE));
            if(!notifies.isEmpty()){//se ci sono delle notifiche scorro l'array e le visualizzo sulla mappa
                Paint pt = new Paint();
                paint.setAntiAlias(true);
                for(int k=0;k<notifies.size();k++){
                    if(currentFloor.equals(notifies.get(k).getFloor())) {
                        int[] c = MainApplication.getFloors().get(currentFloor).getRooms().get(notifies.get(k).getRoom()).getCoords();
                        if((c[0]==coords[0]&&c[1]==coords[1])||(c[0]==position[0]&&c[1]==position[1])) {
                            c[0] += 20;
                            c[1] += 20;
                        }
                        switch (notifies.get(k).getCod_cat()){
                            case 1:
                                canvas.drawBitmap(fire,c[0],c[1],null);
                                break;
                            case 2:
                                canvas.drawBitmap(gas,c[0],c[1],null);
                                break;
                            case 3:
                                canvas.drawBitmap(earthquake,c[0],c[1],null);
                                break;
                            default:
                                canvas.drawBitmap(danger,c[0],c[1],null);
                                break;
                        }
                    }
                }
            }
        }
        //devo disegnare le emergenze

        /*
				 * codice categoria
				 * 1 incendio
				 * 2 gas
				 * 3 terremoto/crollo
				 * 4 altro
				 */

    }

    @Override
    public void update() {

    }

    @Override
    public void retrive() {
        stopTimer();
        int[] pos = Data.getUserPosition().getPosition();
        currentFloor = Data.getUserPosition().getFloor();
        notifies = Data.getNotification().getNotifies();
        position[0] = pos[0];
        position[1] = pos[1];

        Log.i("retrieve","cur f " + currentFloor);
        String map;
        if (currentFloor!=null) {
            map = "m".concat(currentFloor).concat("_color");
        }
        else {
            String flo;
            if(notifies!=null && !notifies.isEmpty()){
                flo = notifies.get(0).getFloor();
            }else{
                flo = "145";
            }


            map = "m".concat(flo).concat("_color");
        }

        resID = getResources().getIdentifier(map , "drawable", getPackageName());

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                setImageGrid(resID);
                setContentView(image);

            }
        });

        startTimer();
    }

    @Override
    public void onBackPressed() {
        if (!MainApplication.getEmergency()) {
            backpress = (backpress + 1);
            Toast.makeText(getApplicationContext(), " Press Back again to Exit ", Toast.LENGTH_SHORT).show();

            if (backpress>1) {
                MainApplication.getScanner().suspendScan();
            }
        }
        else {
            Toast.makeText(getApplicationContext(), " C'è un'emergenza in corso ", Toast.LENGTH_SHORT).show();
        }
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("ACTIVTY MAPS","ricevuto broadcast: " + intent.getAction());
            if(intent.getAction().equals(EXIT_MAPS)) {
                    //nel caso in cui l'app sia stata messa in background durante l'emergenza
                    //il broadcastreceiver non è stato cancellata, quindi cancellato ora
                if(backgroundEmergency==true) {
                    getBaseContext().unregisterReceiver(broadcastReceiver);
                }
                finish();

            }
        }
    };

    private Runnable timerTask = new Runnable() {
        @Override
        public void run() {
            if (MainApplication.getScanner().getSelectedBeacon()==null &&
                    MainApplication.getScanner().getCurrentBeacon()==null) {
                Toast.makeText(getApplicationContext(), " Non è stato trovato nessun sensore a cui collegarsi," +
                        " prova a spostarti o riaccendere l'applicazione", Toast.LENGTH_SHORT).show();
            }
            else if (MainApplication.getScanner().getSelectedBeacon()==null) {
                Toast.makeText(getApplicationContext(), " Non è stato trovato nessun sensore a cui collegarsi", Toast.LENGTH_SHORT).show();
            }
        }
    };



}
