package application.beacon;

/**
 * Created by Niccolo on 10/04/2017.
 */

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Niccolo on 29/03/2017.
 */

public class BeaconConnection {

    public static final String SERVICE_DISCOVERED = "serviceDiscovered";
    public static final String CHAR_WRITTEN = "characteristicWritten";
    public static final String DATA_CHANGED = "dataChanged";
    public static final String CONNECTION_CHANGED = "connectionChanged";
    public static final String SUSPEND = "SUSPEND";

    private static final String UUIDTemp = "f000aa00-0451-4000-b000-000000000000";
    private static final String UUIDTempConfig = "f000aa02-0451-4000-b000-000000000000";
    private static final String UUIDTempData = "f000aa01-0451-4000-b000-000000000000";

    private static final String UUIDBarometer = "f000aa40-0451-4000-b000-000000000000";
    private static final String UUIDBarometerConfig = "f000aa42-0451-4000-b000-000000000000";
    private static final String UUIDBarometerData = "f000aa41-0451-4000-b000-000000000000";

    private static final String UUIDMovement = "f000aa80-0451-4000-b000-000000000000";
    private static final String UUIDMovementConfig = "f000aa82-0451-4000-b000-000000000000";
    private static final String UUIDMovementData = "f000aa81-0451-4000-b000-000000000000";

    private static final String UUIDLuxometer = "f000aa70-0451-4000-b000-000000000000";
    private static final String UUIDLuxometerConfig = "f000aa72-0451-4000-b000-000000000000";
    private static final String UUIDLuxometerData = "f000aa71-0451-4000-b000-000000000000";

    private static final String TAG = "MyBroadcastReceiver";
    private static Activity activity;
    private static IntentFilter intentFilter;

    private static int cont;

    private static BeaconService currentService;

    private static boolean connectionStarted = false;

    //servizi trovati da callback
    private static ArrayList<BluetoothGattService> findServices;
    //servizi ricercati
    private static ArrayList<BeaconService> services;

    private static BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG,"ricevuto broadcast: " + intent.getAction());
            switch(intent.getAction()) {
                case (CONNECTION_CHANGED):
                    GattLeService.getmBluetoothGatt().discoverServices();
                    break;
                case (SERVICE_DISCOVERED):

                    findServices = GattLeService.getServices();
                    //controlla il servizio se è fra quelli trovati nel beacon
                    searchService();
                    break;
                case (CHAR_WRITTEN):
                    Log.i(TAG,"char written ");
                    GattLeService.enableNotifications(GattLeService.getmBluetoothGatt(), currentService);
                    break;
                case (DATA_CHANGED):
                    Log.i(TAG,"data changed");
                    Bundle b = intent.getExtras();
                    if (services.get(cont).getName().equals("movement")) {
                        double[] v = b.getDoubleArray("data");
                        services.get(cont).setValue(v);
                    }
                    else {
                        double v = b.getDouble("data");
                        services.get(cont).setValue(v);
                    }

                    services.get(cont).printValue();
                    cont++;

                    if (cont<services.size()) searchService();
                    else {
                        GattLeService.closeConnection();
                        BeaconScanner.setState(2);
                        Log.i("State","state " + BeaconScanner.getState());
                    }
                    break;
                case (SUSPEND):
                    Log.i(TAG,"suspend");
                    GattLeService.closeConnection();
                    break;
            }
        }
    };

    //filtri per i messaggi ricevuti
    private static void initializeFilter() {
        intentFilter = new IntentFilter();
        intentFilter.addAction(SERVICE_DISCOVERED );
        intentFilter.addAction(CHAR_WRITTEN);
        intentFilter.addAction(DATA_CHANGED);
        intentFilter.addAction(CONNECTION_CHANGED);
        intentFilter.addAction(SUSPEND);
    }

    //servizi desiderati
    private static void initializeServices() {
        services = new ArrayList<>();

        services.add(new BeaconService("temperature",UUIDTemp,UUIDTempData,UUIDTempConfig));
//        services.add(new BeaconService("accelerometer",UUIDAccelerometer,UUIDAccelerometerData,UUIDAccelerometerConfig));
        services.add(new BeaconService("luxometer",UUIDLuxometer,UUIDLuxometerData,UUIDLuxometerConfig));
        services.add(new BeaconService("barometer",UUIDBarometer,UUIDBarometerData,UUIDBarometerConfig));
        services.add(new BeaconService("movement",UUIDMovement,UUIDMovementData,UUIDMovementConfig));

    }

    public static void startConnection(Activity a, BluetoothDevice device) {
        activity = a;
        initializeFilter();
        initializeServices();
        connectionStarted = true;
        findServices = new ArrayList<>();
        activity.getBaseContext().registerReceiver(broadcastReceiver,intentFilter);
        cont = 0;
        GattLeService.execute(device, activity.getBaseContext());

    }

    public BeaconConnection(Activity a, BluetoothDevice device) {
        activity = a;
        initializeFilter();
        initializeServices();
        connectionStarted = true;
        findServices = new ArrayList<>();
        activity.getBaseContext().registerReceiver(broadcastReceiver,intentFilter);
        cont = 0;
        GattLeService.execute(device, activity.getBaseContext());
//        close();
    }

    private static void searchService() {
        boolean b = false;
        int i = 0;
        currentService = services.get(cont);
        if (cont<services.size()) {
            while(i<findServices.size() && b==false) {
                if (findServices.get(i).getUuid().equals(currentService.getService())) {
                    b = true;
                    Log.i(TAG,"service found");
                }
                i++;
            }
        }

        //se ha trovato il servizio allora accende il sensore ed inizia a raccogliere dati
        if (b) GattLeService.turnOnSensor(GattLeService.getmBluetoothGatt(), currentService);
        else Log.i(TAG,"service not found");
    }


    public static boolean isNull() {
        return connectionStarted;
    }

    public static void close() {
        if (connectionStarted) {
            connectionStarted = false;
            activity.getBaseContext().unregisterReceiver(broadcastReceiver);
        }
    }

}
