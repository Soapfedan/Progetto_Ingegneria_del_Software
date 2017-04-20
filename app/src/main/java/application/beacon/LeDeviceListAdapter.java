package application.beacon;

/**
 * Created by Niccolo on 30/03/2017.
 */


import java.util.HashMap;
import java.util.Map;
import android.bluetooth.BluetoothDevice;
import android.util.Log;


// Adapter per gestire i dispositivi identificati durante lo scanner.
public class LeDeviceListAdapter {



    private String TAG2 = "LeDeviceAdapter";

    private HashMap<BluetoothDevice,Integer> mLeDevices;


    public LeDeviceListAdapter() {
        super();
        mLeDevices = new HashMap<>();
    }
    //quando trova un dispositivo con un UUID diverso lo aggiunge all'array
    public void addDevice(BluetoothDevice device, int rssi) {
        if (!mLeDevices.containsKey(device)) {
            mLeDevices.put(device,rssi);
            Log.i(TAG2,"device: " + device.getAddress() + " rssi: " + rssi);
        }
    }

    //restituisce il beacon più vicino (diventa il beacon a cui ci si lega)
    public BluetoothDevice getCurrentBeacon() {
        BluetoothDevice b = null;
        int dist = Integer.MIN_VALUE;
        for (Map.Entry<BluetoothDevice,Integer> map : mLeDevices.entrySet()) {
            if (map.getValue()>dist) {
                b = map.getKey();
                dist = map.getValue();
            }
        }

        return b;
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

    public HashMap<BluetoothDevice,Integer> getmLeDevices() {
        return mLeDevices;
    }

    public long getItemId(int i) {
        return i;
    }

}
