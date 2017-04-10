package application.beacon;

import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Niccolo on 30/03/2017.
 */

public class BeaconService {
    private String id;
    private UUID serviceUUID;
    private UUID serviceDataUUID;
    private UUID serviceConfigUUID;
    private ArrayList<Double> value;

    public BeaconService(String name, String s, String sd, String sc) {
        id = name;
        serviceUUID = UUID.fromString(s);
        serviceDataUUID = UUID.fromString(sd);
        serviceConfigUUID = UUID.fromString(sc);
        value = new ArrayList<>();
    }

    public String getName() {
        return id;
    }

    public UUID getService() {
        return serviceUUID;
    }

    public UUID getServiceData() {
        return serviceDataUUID;
    }

    public UUID getServiceConfig() {
        return serviceConfigUUID;
    }

    public ArrayList<Double> getValue() {
        return value;
    }

    public void setValue(double v) {
        value.add(v);
    }

    public void setValue(double[] v) {
        for (int i=0; i<v.length; i++) {
            value.add(v[i]);
        }
    }

    public void printValue() {
        Log.i("VALUE",id + " value: " + value);
    }
}
