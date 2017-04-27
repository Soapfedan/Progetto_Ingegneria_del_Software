package application.beacon;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;

import application.MainApplication;
import application.comunication.message.MessageBuilder;
import application.user.UserHandler;
import application.utility.StateMachine;

/**
 * Created by Niccolo on 29/03/2017.
 */

public class BeaconConnection extends StateMachine {

    //alcuni possibili messaggi che si possono ricevere durante
    //la connessione(vengono utilizzati come parametri per l'intenFilter)
    public static final String DATA_CHANGED = "dataChanged";
    public static final String ACKNOWLEDGE = "ACKNOWLEDGE";
    public static final String STOP_CONNECTION = "STOP CONNECTION";

    //indirizzi UUID riferiti alla temperatura (il primo indica il servizio, il secondo la configurazione del sensore
    //mentre il terzo i dati veri e proprio
    private static final String UUIDTemp = "f000aa00-0451-4000-b000-000000000000";
    private static final String UUIDTempConfig = "f000aa02-0451-4000-b000-000000000000";
    private static final String UUIDTempData = "f000aa01-0451-4000-b000-000000000000";

    //indirizzi UUID riferiti al barometro (il primo indica il servizio, il secondo la configurazione del sensore
    //mentre il terzo i dati veri e proprio
    private static final String UUIDBarometer = "f000aa40-0451-4000-b000-000000000000";
    private static final String UUIDBarometerConfig = "f000aa42-0451-4000-b000-000000000000";
    private static final String UUIDBarometerData = "f000aa41-0451-4000-b000-000000000000";

    //indirizzi UUID riferiti al sensore di movimento (il primo indica il servizio, il secondo la configurazione del sensore
    //mentre il terzo i dati veri e proprio
    private static final String UUIDMovement = "f000aa80-0451-4000-b000-000000000000";
    private static final String UUIDMovementConfig = "f000aa82-0451-4000-b000-000000000000";
    private static final String UUIDMovementData = "f000aa81-0451-4000-b000-000000000000";

    //indirizzi UUID riferiti al sensore di luce (il primo indica il servizio, il secondo la configurazione del sensore
    //mentre il terzo i dati veri e proprio
    private static final String UUIDLuxometer = "f000aa70-0451-4000-b000-000000000000";
    private static final String UUIDLuxometerConfig = "f000aa72-0451-4000-b000-000000000000";
    private static final String UUIDLuxometerData = "f000aa71-0451-4000-b000-000000000000";

    private static final String TAG = "MyBroadcastReceiver";
    private Activity activity;
    //filtro dei messaggi per il broadcast receiver
    private IntentFilter intentFilter;

    //contatore utilizzato per scandire i servizi del beacon
    private int cont;

    //identifica il servizio di cui si stanno leggendo i valori
    private BeaconService currentService;
    //identifica il dispositivo (beacon) con cui ci si è collegati
    private BluetoothDevice device;

    private boolean connectionStarted = false;

    //servizi trovati da callback
    private ArrayList<BluetoothGattService> findServices;
    //servizi ricercati
    private ArrayList<BeaconService> services;

    //costruttore
    public BeaconConnection(Activity a, BluetoothDevice d) {
        super();
        Log.i("CONNECTION","new connection");
        device = d;
        cont = 0;
        activity = a;
        initializeFilter();
        initializeServices();
        connectionStarted = true;
        findServices = new ArrayList<>();
        activity.getBaseContext().registerReceiver(broadcastReceiver,intentFilter);
    }

    //richiamato quando viene attivata la connessione (esegue lo stato 0 della macchina a stati)
    public void start() {
        executeState();
    }


    protected int nextState() {
        int next = 0;
        Log.i("RUNNING","running " + running);
        switch(currentState) {
            case(0):
                if (!running) next = 8;
                else next = 1;
                break;
            case(1):
                if (!running || cont>=services.size()) next = 8;
                else next = 2;
                break;
            case(2):
                if (!running) next = 6;
                else next = 3;
                break;
            case(3):
                if (!running) next = 5;
                else next = 4;
                break;
            case(4):
                next = 5;
                break;
            case(5):
                next = 6;
                break;
            case(6):
//                if (!running) next = 8;
//                else next = 7;
                next = 7;
                break;
            case(7):
                if (!running || cont>=services.size()) next = 8;
                else next = 1;
                break;
//            default:
//                next = 8;
//                break;

        }
        return next;
    }


    protected void executeState() {
        Log.i("State","execute connection state " + getState());
        switch(currentState) {
            case(0):
                GattLeService.execute(device, activity.getBaseContext());
                break;
            case(1):
                findServices = GattLeService.getServices();
                searchService();
                break;
            case(2):
                GattLeService.turnOnSensor(GattLeService.getmBluetoothGatt(), currentService);
                break;
            case(3):
                GattLeService.enableNotifications(GattLeService.getmBluetoothGatt(), currentService);
                break;
            case(4):
                GattLeService.initializeData();
                break;
            case(5):
                GattLeService.setSampleFlag(false);
                GattLeService.disableNotifications(GattLeService.getmBluetoothGatt(), currentService);
                break;
            case(6):
                GattLeService.turnOffSensor(GattLeService.getmBluetoothGatt(), currentService);
                break;
            case(7):
                GattLeService.analyzeData();
                break;
            case(8):
                String mex = packingMessage();
                close();
                GattLeService.closeConnection();
                activity.getBaseContext().sendBroadcast(new Intent("ScanPhaseFinished"));
                break;

        }
    }

    private void messageHandler(Intent intent) {
        switch (intent.getAction()) {
            //messaggio ricevuto in condizioni normali, quando uno stato termina le istruzioni da eseguire
            case(ACKNOWLEDGE):
                if (GattLeService.getmConnectionState()==0) running = false;
                int next = nextState();
                changeState(next);
                executeState();
                break;
            //messaggio ricevuto quando va stoppata la connessione per il sopraggiungere di un evento esterno
            case(STOP_CONNECTION):
                running = false;

                break;
            //messaggio ricevuto quando sono stati letti un numero sufficente di dati da un sensore
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
                    //aggiorna il timestamp
                services.get(cont).setLastSampleTime(new Timestamp(System.currentTimeMillis()));

                services.get(cont).printValue();
                cont++;

                next = nextState();
                changeState(next);
                executeState();

                break;
        }
    }

    private String packingMessage() {
        String mex;
        ArrayList<String> keys = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();

        keys.add("beacon_ID");
        keys.add("user_ID");
        keys.add("timestamp");
        keys.add("temperature");
        keys.add("luxometer");
        keys.add("barometer");
        keys.add("accx");
        keys.add("accy");
        keys.add("accz");

        values.add(device.getAddress());
        values.add(UserHandler.getIpAddress());
        values.add(new Timestamp(System.currentTimeMillis()).toString());

        int c = 0;


        for (int i=0;i<services.size(); i++) {
            for (int j=0; j<services.get(i).getValue().size(); j++) {
                values.add(""+services.get(i).getValue().get(j));
                c++;
            }
        }
            //qualora alcuni valori non siano stati letti, riempe value con "null"
        while(c<6) {
            values.add("null");
            c++;
        }

//        for (int i=0; i<services.size(); i++) {
//            for (int j=0; j<services.get(i).getValue().size(); j++) {
//                if (services.get(i).getValue().size()==1) {
//                    keys.add(services.get(i).getName());
//                    if(i<cont) values.add(""+services.get(i).getValue().get(j));
//                    else values.add("no value");
//                }
//                else {
//                    int letter = (int)'x' + j;
//                    char c = (char) letter;
//                    keys.add(services.get(i).getName()+c);
//                    if(i<cont) values.add(""+services.get(i).getValue().get(j));
//                    else values.add("no value");
//                }
//                Log.i("num", "i " + i + " j " + j);
//            }
//        }

        mex = MessageBuilder.builder(keys,values,keys.size(),0);
        Log.i("mex",mex);
        return mex;
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG,"ricevuto broadcast: " + intent.getAction());
            messageHandler(intent);
        }
    };

    //filtri per i messaggi ricevuti
    private void initializeFilter() {
        intentFilter = new IntentFilter();
        intentFilter.addAction(ACKNOWLEDGE);
        intentFilter.addAction(DATA_CHANGED);
        intentFilter.addAction(STOP_CONNECTION);
    }


    //servizi desiderati
    private void initializeServices() {
        services = new ArrayList<>();
        services.add(new BeaconService("temperature",UUIDTemp,UUIDTempData,UUIDTempConfig));
        services.add(new BeaconService("luxometer",UUIDLuxometer,UUIDLuxometerData,UUIDLuxometerConfig));
        services.add(new BeaconService("barometer",UUIDBarometer,UUIDBarometerData,UUIDBarometerConfig));
        services.add(new BeaconService("movement",UUIDMovement,UUIDMovementData,UUIDMovementConfig));

    }

    //ricerca un servizio da analizzare, fra quelli contenuti nell'arraylist di servizi di interesse per l'applicazione
    private void searchService() {
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

        int next = nextState();
        changeState(next);
        executeState();

    }

    //quando termina la connessione con il sensore, viene cancellata la registrazione del receiver
    public void close() {
        if (connectionStarted) {
            connectionStarted = false;
            activity.getBaseContext().unregisterReceiver(broadcastReceiver);
        }
    }

}
