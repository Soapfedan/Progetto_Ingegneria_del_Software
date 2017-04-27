package application.beacon;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

import application.MainApplication;
import application.comunication.message.MessageBuilder;
import application.sharedstorage.Data;
import application.sharedstorage.DataListener;
import application.user.UserHandler;
import application.utility.StateMachine;


/**
 * Created by Niccolo on 29/03/2017.
 */

public class BeaconScanner extends StateMachine implements DataListener {

    //contiene le caratteristiche legate allo scan, in riferimento alla configurazione settata
    private Setup setup;

    //alcuni possibili messaggi che può ricevere lo scan (vengono utilizzati come parametri per l'intenFilter)
    public static final String SCAN_PHASE_FINISHED = "ScanPhaseFinished";
    public static final String SUSPEND_SCAN = "SuspendScan";
    public static final String EMERGENCY = "EMERGENCY";

    private static final String TAG = "BeaconRESPONSE";

    //oggetto unico che rappresenta il bluetooth del dispositivo
//    private BluetoothAdapter mBluetoothAdapter;
//    //costante per attivare il bluetooth
//    private static final int REQUEST_ENABLE_BT = 1;

    //adapter per ciò che viene trovato nello scan
    private LeDeviceListAdapter mLeDeviceListAdapter;
    //activity in cui viene fatto partire lo scan (in modo da poterne ricavare il context)
    private Activity activity;

    //rappresenta il beacon più vicino, con cui si deve effettuare il collegamento
    private BluetoothDevice currentBeacon;
    //uuid dei sensortag utilizati
    private static final String beaconUUID = "0000aa80-0000-1000-8000-00805f9b34fb";

    //maschera di UUID, serve per filtrare i dispositivi bluetooth da analizzare
    private UUID[] uuids;
    //handler utilizzato per lanciare le varie Runnable (start,stop,wait)
    private Handler scanHandler;
    //filtro dei messaggi per il broadcast receiver
    private IntentFilter intentFilter;
    //connessione con un determinato sensortag, per leggere i dati dai sensori
    private BeaconConnection connection;

    public BeaconScanner(Activity a) {
        super();

        activity = a;
        //inizializza il contenitore
        setup = new Setup();
        //mette in funzione la state machine
        running = true;
        //inizializzati i componenti del bluetooth
        mLeDeviceListAdapter = new LeDeviceListAdapter();
//        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //viene controllato che il bluetooth sia accesso, nel caso in cui ciò non sia vero
        //viene mostrata l'opzione per attivarlo
//        if(!controlBluetooth()) activateBluetooth(a);

        //insieme di UUID riconosciuti dallo scan e relativa inizializzazione
        uuids = new UUID[1];
        uuids[0] = UUID.fromString(beaconUUID);
        //inizializzazione del filtro per i messaggi e registrazione del broadcast receiver
        initializeFilter();
        //viene registrato il receiver, in modo che possa ricevere messaggi e possa leggere
        //quelli la cui intestazione si trova nell'intentFilter
        activity.getBaseContext().registerReceiver(broadcastReceiver,intentFilter);
        //viene inizializzato l'handler
        scanHandler = new Handler();
        //viene eseguito lo scan corrente
        executeState();
    }

    public BeaconScanner(Activity a, String set) {
        super();
        Data.getUserPosition().addDataListener(this);
        activity = a;
        //inizializza il contenitore
        setup = new Setup(set);
        //mette in funzione la state machine
        running = true;
        //inizializzati i componenti del bluetooth
        mLeDeviceListAdapter = new LeDeviceListAdapter();
//        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //viene controllato che il bluetooth sia accesso, nel caso in cui ciò non sia vero
        //viene mostrata l'opzione per attivarlo
//        if(!controlBluetooth()) activateBluetooth(a);

        //insieme di UUID riconosciuti dallo scan e relativa inizializzazione
        uuids = new UUID[1];
        uuids[0] = UUID.fromString(beaconUUID);
        //inizializzazione del filtro per i messaggi e registrazione del broadcast receiver
        initializeFilter();
        //viene registrato il receiver, in modo che possa ricevere messaggi e possa leggere
        //quelli la cui intestazione si trova nell'intentFilter
        activity.getBaseContext().registerReceiver(broadcastReceiver,intentFilter);
        //viene inizializzato l'handler
        scanHandler = new Handler();
        //viene eseguito lo scan corrente
        executeState();
    }

    //inseriti i filtri per i messaggi ricevuti
    private void initializeFilter() {
        intentFilter = new IntentFilter();
        intentFilter.addAction(SCAN_PHASE_FINISHED);
        intentFilter.addAction(SUSPEND_SCAN);
        intentFilter.addAction(EMERGENCY);
    }
    //gestisce i messaggi ricevuti, in base all'intestazione
    private void messageHandler(Intent intent) {
        switch (intent.getAction()) {
            //ricevuto quando è terminata la connessione al beacon
            case (SCAN_PHASE_FINISHED):
                connection = null;
                //finita l'esecuzione dello stato richiama
                int next = nextState();
                changeState(next);
                executeState();
                break;
            //ricevuto quando deve essere sospeso lo scan
            case (SUSPEND_SCAN):

                suspendScan();
                break;
            //ricevuto quando si presenta un'emergenza
            case (EMERGENCY):

                MainApplication.setEmergency(true);

                suspendScan();
                break;

            default:
                break;
        }
    }
    //contiene le istruzioni relative alla sospesione dello scan, diverse in base allo stato in cui ci si trova
    public void suspendScan() {
        running = false;
//        if (currentState==0) {
//            int next = nextState();
//            changeState(next);
//            executeState();
//        }
        //nel caso in cui si stiano leggendo i dati dai sensori, viene interrotta la procedura
        //fermando la macchina a stati che se ne occupa
        if(currentState==1) {
            activity.getBaseContext().sendBroadcast(new Intent(BeaconConnection.STOP_CONNECTION));
        }
        //se si sta aspettando per un nuovo scan, viene abortito il processo di attesa
        //e si passa allo stato successivo (gestione della chiusura dello scan)
        else if(currentState==2) {
            scanHandler.removeCallbacks(wait);
            int next = nextState();
            changeState(next);
            executeState();
        }
    }

    //il broadcast receiver deputato alla ricezione dei messaggi
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG,"ricevuto broadcast: " + intent.getAction());
            messageHandler(intent);
        }
    };


    public Setup getSetup() {
        return setup;
    }


//    //contralla che il bluetooth sia acceso
//    private boolean controlBluetooth() {
//        boolean b = false;
//        if (!MainApplication.getmBluetoothAdapter().isEnabled() || MainApplication.getmBluetoothAdapter()==null) b = false;
//        else b = true;
//        return b;
//    }
//
//    //attiva il bluetooth
//    private void activateBluetooth (Activity activity) {
//        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//        activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//    }

    //fa partire lo scan che ricerca i sensortag nel raggio d'azione dell'utente
    private void discoverBLEDevices() {
        //scan dei bluetooth LE
        startScan.run();

        Log.e("BLE_Scanner", "DiscoverBLE, in " + setup.getState() + " condition");
    }

    //in base allo stato attuale ed alle condizioni in cui si trova la macchina, viene valutato lo stato successivo
    protected int nextState() {
        int next;
        switch(currentState) {
            case(0):
                if (!running) next = 3;
                else if (running && mLeDeviceListAdapter.getCount()>0 && setup.mustAnalyze()) next = 1;
                else next = 2;
                break;
            case(1):
                if (!running) next = 3;
                else next = 2;
                break;
            case(2):
                if (!running) next = 3;
                else next = 0;
                break;
            default:
                next = 3;
                break;
        }
        return next;
    }

    public String packingMessage() {
        String mex;
        ArrayList<String> keys = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();
        keys.add("beacon_ID");
        keys.add("user_ID");

        values.add(currentBeacon.getAddress());
        values.add(UserHandler.getIpAddress());

        mex = MessageBuilder.builder(keys,values,keys.size(),0);
        Log.i("mex",mex);

        return mex;
    }

    //attende il tempo necessario fra due scan consecutivi
    private void waiting() {
        Log.i("WAITING","waiting for " + setup.getPeriodBetweenScan()/1000 + " seconds for new Scan ");
        scanHandler.postDelayed(wait,setup.getPeriodBetweenScan());
    }

    //vengono eseguite le istruzioni in base allo stato in cui ci si trova
    protected void executeState() {
        Log.i("State","execute scan state " + getState());
        switch(currentState) {
            case(0):
                discoverBLEDevices();
                break;
            case(1):
                connection = new BeaconConnection(activity, currentBeacon);
                connection.start();
                break;
            case (2):
                if (currentBeacon==null) closeConnection();
                waiting();
                break;
            case (3):
                activity.getBaseContext().sendBroadcast(new Intent("TerminatedScan"));
                break;
        }
    }

    //chiude la connessione, una volta terminata
    public void closeConnection() {
        connection = null;
    }

    //thread che si occupa di far partire lo scan in cerca dei beacon
    private Runnable startScan = new Runnable() {
        @Override
        public void run() {
            //cancella la lista di sensortag precedentemente trovati
            mLeDeviceListAdapter.clear();
            Log.e(TAG, "Start Scan");
            //parte effettivamente la ricerca dei sensortag
            MainApplication.getmBluetoothAdapter().startLeScan(uuids, mLeScanCallback);

            //attende per la durata dello scan e poi lancia la runnable per stopparlo
            scanHandler.postDelayed(stopScan, setup.getScanPeriod());

        }
    };

    //thread per mettere in pausa lo scan ed eventualmente elaborare i dati
    private Runnable stopScan = new Runnable() {
        @Override
        public void run() {

            Log.e(TAG, "Stop Scan");
            MainApplication.getmBluetoothAdapter().stopLeScan(mLeScanCallback);
            Log.i(TAG,"numero: " + mLeDeviceListAdapter.getCount());

            //trova il beacon più vicino
            //TODO DEVI CONTROLLARE SE IL BEACON E' UGUALE A QUELLO VECCHIO
//            currentBeacon = mLeDeviceListAdapter.getCurrentBeacon();

            if(mLeDeviceListAdapter.getCurrentBeacon()!=null){
                if (currentBeacon==null || !currentBeacon.getAddress().equals(mLeDeviceListAdapter.getCurrentBeacon().getAddress())) {
                    currentBeacon = mLeDeviceListAdapter.getCurrentBeacon();
                    update();
                }


            }

            //finita l'esecuzione dello stato richiama
            int next = nextState();
            changeState(next);
            executeState();


        }
    };
    //thread per gestire l'attesa fra due scan consecutivi
    private Runnable wait = new Runnable() {
        @Override
        public void run() {
            //finita l'attesa richiama i metodi per passare allo stato successivo
            int next = nextState();
            changeState(next);
            executeState();
        }
    };

    public BeaconConnection getConnection() {
        return connection;
    }

    //quando viene stoppato dall'esterno lo scan, come ultimo passaggio viene cancellata la registrazione
    //del receiver
    public void closeScan() {
        if(broadcastReceiver!=null) activity.getBaseContext().unregisterReceiver(broadcastReceiver);
    }

    //callback utilizzata per trovare dispositivi nel raggio d'azione
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi,
                                     byte[] scanRecord) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLeDeviceListAdapter.addDevice(device,rssi);
                        }
                    });
                }

            };


    @Override
    public void update() {
        String mex = packingMessage();
//        if(MainApplication.getFloors()!=null) {
            String cod = currentBeacon.getAddress();
//            MainApplication.getSensors().get(cod).getCoords();
//            int[] position = MainApplication.getFloors().get("145").getRooms().get("145RG2").getCoords();
            Data.getUserPosition().setPosition(MainApplication.getSensors().get(cod).getCoords());
            Data.getUserPosition().setFloor(MainApplication.getSensors().get(cod).getFloor());
            Data.getUserPosition().updateInformation();
//        }
    }

    @Override
    public void retrive() {

    }
}