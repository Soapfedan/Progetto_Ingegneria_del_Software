package application;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

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
import application.user.UserHandler;
import application.utility.CSVHandler;

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

        InputStream inputStream = activity.getResources().openRawResource(R.raw.beaconlist);
        ArrayList<String[]> beaconList = new ArrayList<>();
        beaconList = CSVHandler.readCSV("beaconlist",activity.getBaseContext());
        loadSensors(beaconList);

//        HashMap<String,String>[] s = new HashMap[2];
//        s[0] = new HashMap<>();
//        s[0].put("id","B0:B4:48:BC:59:87");
//        s[0].put("floor","145");
//        s[0].put("x","320");
//        s[0].put("y","280");
//        s[1] = new HashMap<>();
//        s[1].put("id","B0:B4:48:BC:CE:87");
//        s[1].put("floor","145");
//        s[1].put("x","320");
//        s[1].put("y","280");
//
//
//        try {
//            CSVHandler.updateCSV(s,activity,"beaconlist");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
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
        switch(intent.getAction()) {
            case("TerminatedScan"):
                if(scanner.getSetup().getState().equals("NORMAL")) {
                    scanner.closeScan();
                    scanner = null;

                    context.sendBroadcast(new Intent("STARTMAPS"));

//                        Intent intentTWO = new Intent (activity.getApplicationContext(),
//                                FullScreenMap.class);
//                        activity.startActivity(intentTWO);

                }
                else {
                    context.sendBroadcast(new Intent("EXIT_MAPS"));
                    scanner.closeScan();
                    scanner = null;

                    initializeScanner(activity);
                }
                break;

            default:

                break;
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

