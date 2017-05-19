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

    int resID;

    private static final String EXIT_MAPS = "EXIT_MAPS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_maps_scrool);
        getSupportActionBar().setTitle("Go Safe!");
        handler = new Handler();
        notifies = new ArrayList<>();

        Data.getUserPosition().addDataListener(this);
        s = 0;
        position = new int[2];
        //position = MainApplication.getFloors().get("145").getRooms().get("145DICEA").getCoords();
        Data.getUserPosition().addDataListener(this);
        Data.getNotification().addDataListener(this);
        Bundle extras = getIntent().getExtras();

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

    }


    protected void onStart() {
        super.onStart();
        if(!MainApplication.controlBluetooth()) MainApplication.activateBluetooth(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(EXIT_MAPS);
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
        if(broadcastReceiver!=null) getBaseContext().unregisterReceiver(broadcastReceiver);
    }

    protected void onStop() {
        super.onStop();

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

        if(position[0]!=0&&position[1]!=0) {

            //int[] c = coordsMapping(position);
            canvas.drawCircle(position[0], position[1], 30, paint); //x y radius paint

            image.setImageBitmap(mutableBitmap);

                //disegno obiettivo
            if(selectedFloor.equals(currentFloor)) {
                image.setImageBitmap(mutableBitmap);
                int[] coords = MainApplication.getFloors().get(selectedFloor).getRooms().get(selectedRoom).getCoords();
                canvas.drawCircle(coords[0],coords[1],30,new Paint(Color.BLUE));
            }
            if(!notifies.isEmpty()){
                Paint pt = new Paint();
                paint.setAntiAlias(true);
                for(int k=0;k<notifies.size();k++){
                    if(currentFloor.equals(notifies.get(k).getFloor())) {
                        int[] c = MainApplication.getFloors().get(currentFloor).getRooms().get(notifies.get(k).getRoom()).getCoords();
                        //TODO SE CI SONO PIU' NOTIFICHE NELLO STESSO PUNTO?
                        switch (notifies.get(k).getCod_cat()){
                            case 1:
                                pt.setColor(Color.GREEN);
                                canvas.drawCircle(c[0],c[1],30,pt);
                                break;
                            case 2:
                                pt.setColor(Color.YELLOW);
                                canvas.drawCircle(c[0],c[1],30,pt);
                                break;
                            case 3:
                                pt.setColor(Color.MAGENTA);
                                canvas.drawCircle(c[0],c[1],30,pt);
                                break;
                            default:
                                pt.setColor(Color.CYAN);
                                canvas.drawCircle(c[0],c[1],30,pt);
                                break;
                        }
                    }
                }
            }
        }
        else {
            image.setImageBitmap(mutableBitmap);
            int[] coords = MainApplication.getFloors().get(selectedFloor).getRooms().get(selectedRoom).getCoords();
            canvas.drawCircle(coords[0],coords[1],30,new Paint(Color.BLUE));
            if(!notifies.isEmpty()){
                Paint pt = new Paint();
                paint.setAntiAlias(true);
                for(int k=0;k<notifies.size();k++){
                    if(currentFloor.equals(notifies.get(k).getFloor())) {
                        int[] c = MainApplication.getFloors().get(currentFloor).getRooms().get(notifies.get(k).getRoom()).getCoords();
                        //TODO SE CI SONO PIU' NOTIFICHE NELLO STESSO PUNTO?
                        switch (notifies.get(k).getCod_cat()){
                            case 1:
                                pt.setColor(Color.GREEN);
                                canvas.drawCircle(c[0],c[1],30,pt);
                                break;
                            case 2:
                                pt.setColor(Color.YELLOW);
                                canvas.drawCircle(c[0],c[1],30,pt);
                                break;
                            case 3:
                                pt.setColor(Color.MAGENTA);
                                canvas.drawCircle(c[0],c[1],30,pt);
                                break;
                            default:
                                pt.setColor(Color.CYAN);
                                canvas.drawCircle(c[0],c[1],30,pt);
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
        String map = "m".concat(currentFloor).concat("_color");

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
//                MainApplication.setEmergency(false);
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
