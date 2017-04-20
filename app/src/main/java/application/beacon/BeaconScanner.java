package application.beacon;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import java.util.UUID;

import static java.util.UUID.fromString;

/**
 * Created by Niccolo on 29/03/2017.
 */

public class BeaconScanner {

    private static Setup setup;

    private static int state;

    private static final String TAG = "BeaconRESPONSE";
    //oggetto unico che rappresenta il bluetooth del dispositivo
    private static BluetoothAdapter mBluetoothAdapter;
    //costante per attivare il bluetooth
    private static final int REQUEST_ENABLE_BT = 1;


    //adapter per ciò che viene trovato nello scan
    private static LeDeviceListAdapter mLeDeviceListAdapter;

    private static Activity a;

    private static boolean running;

    //rappresenta il beacon più vicino, con cui si deve effettuare il collegamento
    private static BluetoothDevice currentBeacon;
    //uuid dei beacon
    private static final String beaconUUID = "0000aa80-0000-1000-8000-00805f9b34fb";

    private static UUID[] uuids;

    private static Handler scanHandler;

    private static BeaconConnection connection;

    public static void start(Activity activity) {
        //inizializzati i componenti del bluetooth
        a = activity;
        //inizializza il contenitore
        setup = new Setup();

        running = true;

        mLeDeviceListAdapter = new LeDeviceListAdapter();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!controlBluetooth()) activateBluetooth(a);

        //insieme di UUID riconosciuti dallo scan
        uuids = new UUID[1];
        uuids[0] = fromString(beaconUUID);

        scanHandler = new Handler();
        //scan dei dispositivi LE
        discoverBLEDevices();

    }

    public static void start(Activity activity, String status) {
        //inizializzati i componenti del bluetooth
        a = activity;
        //inizializza il contenitore
        setup = new Setup();
        setup.setCondition(status);

        running = true;

        mLeDeviceListAdapter = new LeDeviceListAdapter();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!controlBluetooth()) activateBluetooth(a);

        //insieme di UUID riconosciuti dallo scan
        uuids = new UUID[1];
        uuids[0] = fromString(beaconUUID);
        scanHandler = new Handler();
        //scan dei dispositivi LE
        discoverBLEDevices();

    }

    public static void changeRunning() {
        running = !running;
    }

    public static Setup getSetup() {
        return setup;
    }

    public static void stop() {
        //fa in modo che non partano eventuali nuovi scan
        running = false;
        Log.i("STOP","STOP");
        switch (state) {
            //caso in cui stia effettuando lo scan
            case(0):
                Log.i("CASE","case " + state);
                break;
            //caso in cui stia estraendo dati
            case(1):
                Log.i("CASE","case " + state);
                a.getBaseContext().sendBroadcast(new Intent("SUSPEND"));
                break;
            //caso in cui stia aspettando
            case(2):
                Log.i("CASE","case " + state);
                scanHandler.removeCallbacksAndMessages(null);

                break;
        }
//        scanHandler.removeCallbacks(startScan);
//        scanHandler.removeCallbacks(stopScan);
    }

    private static void initializeUUIDArray() {
        //TODO inserire qua dentro tutti gli UUID dei servizi che può servire leggere
    }
    //contralla che il bluetooth sia acceso
    private static boolean controlBluetooth() {
        boolean b = false;
        if (!mBluetoothAdapter.isEnabled() || mBluetoothAdapter==null) b = false;
        else b = true;
        return b;
    }
    //attiva il bluetooth
    private static void activateBluetooth (Activity activity) {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    private static void discoverBLEDevices() {
        //scan dei bluetooth LE
        startScan.run();
        Log.e("BLE_Scanner", "DiscoverBLE");
    }


    public static void closeConnection() {
        BeaconConnection.close();
    }

    //thread che si occupa di cercare i beacon
    private static Runnable startScan = new Runnable() {
        @Override
        public void run() {
            state = 0;
            Log.i("State","state " + BeaconScanner.getState());
            mLeDeviceListAdapter.clear();
            if (!BeaconConnection.isNull()) closeConnection();
            if (running) {
                scanHandler.postDelayed(stopScan, setup.getScanPeriod());
                mBluetoothAdapter.startLeScan(uuids, mLeScanCallback);
            }
            Log.e(TAG, "Start Scan");
        }
    };

    //thread per mettere in pausa lo scan ed eventualmente elaborare i dati
    private static Runnable stopScan = new Runnable() {
        @Override
        public void run() {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            Log.i(TAG,"numero: " + mLeDeviceListAdapter.getCount());
            //trova il beacon più vicino
            currentBeacon = mLeDeviceListAdapter.getCurrentBeacon();

            if(running) {
                //se le trova, ci si collega per leggere i dati
                if(currentBeacon!=null && setup.mustAnalyze()) {
                    state = 1;
                    Log.i("State","state " + BeaconScanner.getState());
                    BeaconConnection.startConnection(a,currentBeacon);
                }

                scanHandler.postDelayed(startScan, setup.getPeriodBetweenScan());
            }

            Log.e(TAG, "Stop Scan");
        }
    };

    public static int getState() {
        return state;
    }

    public static void setState(int s) {
        state = s;
    }

    //Device scan callback.
    private static BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi,
                                     byte[] scanRecord) {
                    a.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLeDeviceListAdapter.addDevice(device,rssi);
                        }
                    });
                }

            };


}
