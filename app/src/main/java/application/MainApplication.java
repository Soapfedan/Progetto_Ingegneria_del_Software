package application;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.core.progettoingegneriadelsoftware.FullScreenMap;
import com.core.progettoingegneriadelsoftware.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import application.beacon.BeaconScanner;
import application.comunication.ServerComunication;
import application.comunication.http.GetReceiver;
import application.database.UserAdapter;
import application.maps.MapLoader;
import application.maps.components.Node;
import application.maps.components.Floor;
import application.maps.components.Room;
import application.sharedstorage.Data;
import application.user.UserHandler;
import application.utility.CSVHandler;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Federico-PC on 05/12/2016.
 */

public class MainApplication {

    /**
     * array dinamico che contiene tutti i piani di una mappa scaricata
     */

    private static HashMap<String,Floor> floors;
    private static UserAdapter db;

    private static HashMap<String, Node> sensors;

    private static boolean emergency;
    private static BluetoothAdapter mBluetoothAdapter;
    private static BeaconScanner scanner;

    private static Activity activity;

    //costante per attivare il bluetooth
    private static final int REQUEST_ENABLE_BT = 1;

    private static IntentFilter intentFilter;

    public static final String TERMINATED_SCAN = "TerminatedScan";

    /**
     * Method used t
     * @param n
     */
    public void researchNode(Node n){

    }

    public static UserAdapter getDB () {
        return db;
    }

    public static HashMap<String,Node> getSensors() {
        return sensors;
    }

    public static void start(Activity a) {
        activity = a;
        initializeFilter();
        emergency = false;

        activity.getBaseContext().registerReceiver(broadcastReceiver,intentFilter);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!controlBluetooth()) activateBluetooth(activity);
        if(mBluetoothAdapter!=null) initializeScanner(activity);
        UserHandler.init();
        //crea il db, ma ancora non è ne leggibile ne scrivibile
        db = new UserAdapter(activity.getBaseContext());

        ServerComunication.setHostMaster(PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext()).getString("serverIp",""));
        Log.i("ip","" + ServerComunication.getIP());
//        InputStream inputStream = activity.getResources().openRawResource(R.raw.beaconlist);
        ArrayList<String[]> beaconList = CSVHandler.readCSV("beaconlist",activity.getBaseContext());
        loadSensors(beaconList);

        ArrayList<String[]> roomsList = CSVHandler.readCSV("roomlist",activity.getBaseContext());
        loadRooms(roomsList);

    }

    private static void loadSensors(ArrayList<String[]> b) {
        int coords[] = new int[2];
        String fl;
        String cod;
        sensors = new HashMap<>();


        for(String[] beacon : b) {
            cod = beacon[0];
            coords[0] = Integer.parseInt(beacon[2]);
            coords[1] = Integer.parseInt(beacon[3]);
            fl = beacon[1];
            sensors.put(cod,new Node(coords.clone(),fl));
//            Log.i("csv","cod: " + beacon[0] + " floor " + fl + " coords " + coords[0] + "," + coords[1]);
        }

    }

    //aggiungo manualmente elementi alle barre, dopo andrà fatto diversamente
    private static void loadRooms(ArrayList<String[]> b) {

        int[] coords = new int[2];
        double width;
        String cod;
        HashMap<String,Floor> f = new HashMap<>();
        floors = f;
        for (String[] roomslist : b) {
            if(floors.containsKey(roomslist[2])) {    //il piano esiste
                coords[0] = Integer.parseInt(roomslist[0]); //x
                coords[1] = Integer.parseInt(roomslist[1]); //y
                width = Double.parseDouble(roomslist[3].replace(",","."));
                cod = roomslist[4];
                floors.get(roomslist[2]).addRoom(cod,new Room(cod,coords.clone(),roomslist[2],width));
            }else{
                floors.put(roomslist[2],new Floor(roomslist[2]));//aggiungo il nuovo piano
                //aggiunto il nodo
                coords[0] = Integer.parseInt(roomslist[0]); //x
                coords[1] = Integer.parseInt(roomslist[1]); //y
                width = Double.parseDouble(roomslist[3].replace(",","."));     //larghezza
                cod = roomslist[4];                         //codice
                floors.get(roomslist[2]).addRoom(cod,new Room(cod,coords.clone(),roomslist[2],width));
            }


        }


    }


    public static BluetoothAdapter getmBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    public static void setFloors(HashMap<String,Floor> f){
        floors = f;
    }

    public static HashMap<String,Floor> getFloors(){
        return floors;
    }

    public static boolean getEmergency() {
        return emergency;
    }

    public static void setEmergency(boolean e) {
        emergency = e;
//        if (emergency) Toast.makeText(activity.getApplicationContext()," C'è un'emergenza ", Toast.LENGTH_SHORT).show();
//        else Toast.makeText(activity.getApplicationContext()," Fine emergenza ", Toast.LENGTH_SHORT).show();
        scanner.suspendScan();
    }

    public static BeaconScanner getScanner() {
        return scanner;
    }

    public static void initializeScanner(Activity a) {
        scanner = new BeaconScanner(a);
    }

    public static void initializeScanner(Activity a, String cond) {
        scanner = new BeaconScanner(a,cond);
    }

    public static void closeApp(GetReceiver httpServerThread) {
        if(broadcastReceiver!=null) if(broadcastReceiver!=null) activity.getBaseContext().unregisterReceiver(broadcastReceiver);

        if (httpServerThread.status()) {
            try {
                httpServerThread.closeConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            ServerComunication.deletePosition(UserHandler.getIpAddress());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        Log.i("MESSAGE ARRIVED","ricevuto broadcast: " + intent.getAction());
        if(intent.getAction().equals("TerminatedScan")) {
            if(emergency) {
                if(scanner.getSetup().getState().equals("NORMAL")) {
                    scanner.closeScan();
                    scanner = null;

                    String floor = Data.getUserPosition().getFloor();

                    String mex = floor.concat(";").concat(floor).concat("EMERGENCY");
                    Log.i("mex",mex);
                    Intent intentTWO = new Intent (context,
                            FullScreenMap.class);
                    intentTWO.putExtra("MAP_ID",mex);
                    context.startActivity(intentTWO);

                }
                else {
                    context.sendBroadcast(new Intent("EXIT_MAPS"));
                    //TODO TOAST EMERGENZA FINITA
                    scanner.closeScan();
                    scanner = null;

                    initializeScanner(activity);
                }
            }
            else {
                if(scanner.getSetup().getState().equals("NORMAL")) {
                    scanner.closeScan();
                    scanner = null;

                    context.sendBroadcast(new Intent("STARTMAPS"));


                }
                else {
                    context.sendBroadcast(new Intent("EXIT_MAPS"));
                    scanner.closeScan();
                    scanner = null;

                    initializeScanner(activity);
                }
            }

        }
        }
    };

    private static void initializeFilter() {
        intentFilter = new IntentFilter();
        intentFilter.addAction(TERMINATED_SCAN);
    }

    public static Activity getActivity() {
        return activity;
    }

    //contralla che il bluetooth sia acceso
    public static boolean controlBluetooth() {
        boolean b;
        if (getmBluetoothAdapter()==null || !getmBluetoothAdapter().isEnabled()) b = false;
        else b = true;
        return b;
    }

    public static void launchNotification() {
        Intent intent = new Intent(activity, FullScreenMap.class);
// use System.currentTimeMillis() to have a unique ID for the pending intent
        PendingIntent pIntent = PendingIntent.getActivity(activity, (int) System.currentTimeMillis(), intent, 0);

        // build notification
// the addAction re-use the same intent to keep the example short
        Notification n  = new Notification.Builder(activity)
                .setContentTitle("Progetto Ingegneria")
                .setContentText("C'è un'emergenza")
                .setSmallIcon(R.drawable.ic_menu_gallery)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_menu_gallery, "Open", pIntent).build();

        NotificationManager notificationManager =
                (NotificationManager) activity.getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0, n);
    }

    public static boolean isAppIsInBackground(Context context) {
        boolean b = false;

        return b;
    }

    public static Node getSensor(String cod) {
        Node n = sensors.get(cod);
        return n;
    }

    //attiva il bluetooth
    public static void activateBluetooth (Activity activity) {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    public static boolean isEmergency() {
        return emergency;
    }
}

