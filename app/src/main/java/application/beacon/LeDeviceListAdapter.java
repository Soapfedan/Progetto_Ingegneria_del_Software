package application.beacon;

/**
 * Created by Niccolo on 30/03/2017.
 */


import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import android.bluetooth.BluetoothDevice;
import android.util.Log;
import android.widget.Toast;

import application.MainApplication;
import application.maps.components.Node;


// Adapter per gestire i dispositivi identificati durante lo scanner.
public class LeDeviceListAdapter {

    private BluetoothDevice currentBeacon;

    private String TAG2 = "LeDeviceAdapter";

    private TreeMap<Integer,BluetoothDevice> mLeDevices;


    public LeDeviceListAdapter() {
        super();
        mLeDevices = new TreeMap<>(Collections.<Integer>reverseOrder());
    }
    //quando trova un dispositivo con un UUID diverso lo aggiunge all'array
    public void addDevice(BluetoothDevice device, int rssi) {
        if (!mLeDevices.containsValue(device)) {
            mLeDevices.put(rssi,device);
            Log.i(TAG2,"device: " + device.getAddress() + " rssi: " + rssi);
        }
    }

    public BluetoothDevice selectedDevice() {
        BluetoothDevice b = null;

        //per selezionare il beacon, si valuta anche se questo è presente nella struttura dati
        Iterator it = mLeDevices.entrySet().iterator();
        while (b==null && it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            BluetoothDevice dev = (BluetoothDevice) entry.getValue();
            if(MainApplication.getSensors().containsKey(dev.getAddress())) {
                b = dev;
//                Log.i("find","find");
            }
            else {
                Toast.makeText(MainApplication.getActivity().getApplicationContext(),
                        " Si è individuato un sensore non presente nel documento, " +
                                " dovresti aggiornare il file", Toast.LENGTH_SHORT).show();
            }

        }
//        for (Map.Entry<Integer,BluetoothDevice> map : mLeDevices.entrySet()) {
//            if (map.getKey()>dist) {
//                b = map.getValue();
//                dist = map.getKey();
//            }
//            Log.i("print","value: " + map.getKey() + " device: " + map.getValue().getAddress());
//        }
        currentBeacon = b;
        return b;
    }

    //restituisce il beacon più vicino (diventa il beacon a cui ci si lega)
    public BluetoothDevice getCurrentBeacon() {
        return currentBeacon;
    }

    public void setCurrentBeacon(BluetoothDevice b) {
        currentBeacon = b;
    }

    public void clear() {
        mLeDevices.clear();
    }

    public int getCount() {
        return mLeDevices.size();
    }

    public Object getItem(int i) {
        return mLeDevices.get(i);
    }

//    public HashMap<BluetoothDevice,Integer> getmLeDevices() {
//        return mLeDevices;
//    }

    public long getItemId(int i) {
        return i;
    }

}
