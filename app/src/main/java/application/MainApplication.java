package application;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.core.progettoingegneriadelsoftware.FullScreenMap;

import java.util.HashMap;

import application.beacon.BeaconScanner;
import application.database.UserAdapter;
import application.maps.components.Node;
import application.maps.components.Floor;
import application.user.UserHandler;

/**
 * Created by Federico-PC on 05/12/2016.
 */

public class MainApplication {

    /**
     * array dinamico che contiene tutti i piani di una mappa scaricata
     */
    // TODO: 06/12/2016  in teoria io potrei switchare tra più mappe es. medicinia o ingengeria ...
    // devo prevedere come gestire le diverse mappe scaricate, tenendone in memoria solo una???
    // lasciando le altre sul db??


    private static HashMap<String,Floor> floors;
    private static UserAdapter db;

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
    }

    public void loadMap(String tipe){
        // TODO: 06/12/2016  devo caricare la mappa desiderata dal db
    }

    public void activateBluetooth(){

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

    //attiva il bluetooth
    public static void activateBluetooth (Activity activity) {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }
}

