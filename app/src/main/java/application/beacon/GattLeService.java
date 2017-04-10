package application.beacon;

/**
 * Created by Niccolo on 10/04/2017.
 */

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

import static java.lang.Math.pow;

/**
 * Created by Niccolo on 29/03/2017.
 */

public class GattLeService {

    public static final String TAG = "GattLeService";

    private static Context context;
    private static ArrayList<BluetoothGattService> services;

    private static BluetoothDevice device;
    private static BluetoothGatt mBluetoothGatt;
    private static int mConnectionState;

    private static final int numSample = 5;

    private static final String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    private static String serviceAnalyzed;
    private static BeaconService currentBeacon;

    private static boolean terminated = false;
    private static boolean sensorWorking = false;
    private static int cont = 0;

    //flag utilizzato per scartare la prima misurazione del sensore
    private static boolean sampleFlag = false;

    private static ArrayList<Double>[] data;

//    private static double dataOneDim[];
//    private static double dataThreeDim[][];

    public static void execute(BluetoothDevice d, Context c) {
        device = d;
        services = new ArrayList<>();
        mConnectionState = STATE_DISCONNECTED;
        context = c;

        mBluetoothGatt = device.connectGatt(context,false,mGattCallback);
    }

    public static ArrayList<BluetoothGattService> getServices() {
        Log.e("onServicesDiscovered", "Services count: " + mBluetoothGatt.getServices().size());
        for (BluetoothGattService service: mBluetoothGatt.getServices()) {
            String serviceUUID = service.getUuid().toString();
            Log.e("onServicesDiscovered", "Service uuid " + serviceUUID);
            for (BluetoothGattCharacteristic chaar : service.getCharacteristics()) {
                Log.i(TAG,"char uuid " + chaar.getUuid());
            }
            services.add(service);
        }
        return services;
    }

    // Various callback methods defined by the BLE API.
    public static BluetoothGattCallback mGattCallback =
            new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                                    int newState) {
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        mConnectionState = STATE_CONNECTED;
                        Log.i(TAG, "Connected to GATT server.");
//                    Log.i(TAG, "Attempting to start service discovery:" + mBluetoothGatt.discoverServices());
                        Log.i(TAG, "Attempting to start service discovery:" );

                        context.sendBroadcast(new Intent("connectionChanged"));

                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        mConnectionState = STATE_DISCONNECTED;
                        Log.i(TAG, "Disconnected from GATT server.");
                    }
                }

                @Override
                // New services discovered
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        Log.w(TAG,"GATT_SUCCESS");
                        context.sendBroadcast(new Intent(BeaconConnection.SERVICE_DISCOVERED));
                    } else {
                        Log.w(TAG, "onServicesDiscovered received: " + status);
                    }
                }

                @Override
                // Result of a characteristic read operation
                public void onCharacteristicRead(BluetoothGatt gatt,
                                                 BluetoothGattCharacteristic characteristic,
                                                 int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        Log.i(TAG,"action data available " + characteristic.getUuid().toString());
                    }
                }

                @Override
                public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        //status 0 significa che è stato modificato con successo


                        if (!sensorWorking) {
                            Log.i("WRITECHAR", "sensor activated");
                            sensorWorking = true;

                            //contenitore dei dati
                            data = new ArrayList[numSample];

                            //inizializzati elementi dell'array
                            for (int i=0; i<data.length;i++) {
                                data[i] = new ArrayList<>();
                            }

                            context.sendBroadcast(new Intent(BeaconConnection.CHAR_WRITTEN));
                        }
                        else {
                            Log.i("WRITECHAR", "sensor disactivated");
                            sensorWorking = false;
                            terminated = false;
                            sampleFlag = false;

                            Intent intent = new Intent(BeaconConnection.DATA_CHANGED);

                            ArrayList<Double> value = mediaValues(data);
                            if (value.size()==1) {
                                intent.putExtra("data",value.get(0));
                            }
                            else {
                                double[] d = new double[value.size()];
                                for (int i=0; i<value.size(); i++) {
                                    d[i]=value.get(i);
                                }
                                intent.putExtra("data",d);
                            }

                            data = null;
                            cont = 0;
                            context.sendBroadcast(intent);

                        }
                    }

                }

                @Override
                public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                    Log.i("DESCRIPTOR READ", "descriptor read");
                }

                @Override
                public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                    Log.i("DESCRIPTOR WRITE", "descriptor write");
                    if(terminated) {
                        turnOffSensor(gatt,currentBeacon);
                    }
                }


                @Override
                public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {

                    if (sampleFlag) {
                        if(cont<numSample) {
                            switch(serviceAnalyzed) {
                                case("temperature"):
                                    data[cont].add(extractAmbientTemperature(characteristic));
                                    break;
                                case("barometer") :
                                    data[cont].add(extractBarometerValue(characteristic));
                                    break;
                                case("movement") :
                                    double[] val = extractAccelerometerValue(characteristic);
                                    for (Double d : val) {
                                        data[cont].add(d);
                                    }
                                    break;
                                case("luxometer") :
                                    data[cont].add(extractAmbientLight(characteristic));
                                    break;
                            }
                            Log.i("CHARCHANGE", "char changed " + data[cont]);
                            cont++;
                        }
                        else {
                            terminated = true;
                            disableNotifications(gatt,currentBeacon);
                        }
                    }
                    sampleFlag = true;

                }
            };
    //calcola la media delle misurazioni ricevute
    private static ArrayList<Double> mediaValues(ArrayList<Double>[] v) {
        ArrayList<Double> value;
        value = new ArrayList<>();
        double tot = 0;
        //analizzo elementi unidimensionali
        if(v[0].size()==1) {
            for (int i = 0; i<v.length; i++) {
                tot += v[i].get(0);
            }
            value.add(tot/v.length);
        }
        else {
            for (int j=0; j<v[0].size(); j++) {
                for (int i=0; i<v.length; i++) {
                    tot+=v[i].get(j);
                }
                value.add(tot/v.length);
                tot = 0;
            }
        }
        return value;
    }



    public  static void closeConnection() {
        if (mBluetoothGatt != null) {
//            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
        }
    }
    //spegne il sensore, in base al servizio che viene passato
    public static void turnOffSensor(BluetoothGatt gatt, BeaconService beacon) {
        UUID serviceUUID = beacon.getService();
        UUID configUUID = beacon.getServiceConfig();

        BluetoothGattService service = gatt.getService(serviceUUID);
        BluetoothGattCharacteristic config = service.getCharacteristic(configUUID);

        if (beacon.getName().equals("movement")) {
            byte[] b = new byte[2];
            b[0] = 0x00;
            b[1] = 0x00;
            config.setValue(b);
        }
        else {
            config.setValue(new byte[]{0});
        }

        gatt.writeCharacteristic(config);
    }

    //attiva il sensore in base al Servizio passato, va chiamato necessariamente dopo DiscoverService
    public static void turnOnSensor(BluetoothGatt gatt, BeaconService beacon) {
        currentBeacon = beacon;
        UUID serviceUUID = beacon.getService();
        UUID configUUID = beacon.getServiceConfig();

        BluetoothGattService service = gatt.getService(serviceUUID);
        BluetoothGattCharacteristic config = service.getCharacteristic(configUUID);


        if (beacon.getName().equals("movement")) {
            byte[] b = new byte[2];
            b[0] = 0x38;
            b[1] = 0x02;
            config.setValue(b);
        }
        else {
            config.setValue(new byte[]{1});
        }

        gatt.writeCharacteristic(config);
    }

    public static void disableNotifications(BluetoothGatt gatt, BeaconService beacon) {

        UUID serviceUUID = beacon.getService();
        UUID dataUUID = beacon.getServiceData();
        UUID clientCharacteristicConfigUUID = UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG);
        currentBeacon = beacon;
        serviceAnalyzed = beacon.getName();

        BluetoothGattService service = gatt.getService(serviceUUID);
        BluetoothGattCharacteristic dataCharacteristic = service.getCharacteristic(dataUUID);
        //attiva le notifiche
        gatt.setCharacteristicNotification(dataCharacteristic, true);
        //Enabled remote notifications
        BluetoothGattDescriptor config = dataCharacteristic.getDescriptor(clientCharacteristicConfigUUID);
        config.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);

        boolean b = gatt.writeDescriptor(config);
    }

    public static void enableNotifications(BluetoothGatt gatt, BeaconService beacon) {

        UUID serviceUUID = beacon.getService();
        UUID dataUUID = beacon.getServiceData();
        UUID clientCharacteristicConfigUUID = UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG);
        currentBeacon = beacon;
        serviceAnalyzed = beacon.getName();
        Log.i("service ana","service analyzed: " + serviceAnalyzed);

        BluetoothGattService service = gatt.getService(serviceUUID);
        BluetoothGattCharacteristic dataCharacteristic = service.getCharacteristic(dataUUID);
        //attiva le notifiche
        gatt.setCharacteristicNotification(dataCharacteristic, true);
        //Enabled remote notifications
        BluetoothGattDescriptor config = dataCharacteristic.getDescriptor(clientCharacteristicConfigUUID);
        config.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);

        boolean b = gatt.writeDescriptor(config);
    }

    private static double[] extractAccelerometerValue(BluetoothGattCharacteristic c) {
        // Range 8G
        double acc[] = new double[3];
        //deriva da 32768/8 (8G)
        final double SCALE = 4096.0;

        acc[0] = ((c.getValue()[7]<<8) + c.getValue()[6])/SCALE;
        acc[1] = ((c.getValue()[9]<<8) + c.getValue()[8])/SCALE;
        acc[2] = ((c.getValue()[11]<<8) + c.getValue()[10])/SCALE;

        Log.i("ACC","x: " + acc[0] + " y: " + acc[1] + " z: " + acc[2]);

        return acc;
    }

    private static double extractBarometerValue(BluetoothGattCharacteristic c) {
        double value;
        if (c.getValue().length > 4) {
            Integer val = twentyFourBitUnsignedAtOffset(c.getValue(), 2);
            value = (double) val / 100.0;
        }
        else {
            int mantissa;
            int exponent;
            Integer sfloat = shortUnsignedAtOffset(c, 2);

            mantissa = sfloat & 0x0FFF;
            exponent = (sfloat >> 12) & 0xFF;

            double magnitude = pow(2.0f, exponent);
            value = (mantissa * magnitude) / 100.0;

        }
        return value;
    }

    private static double extractAmbientTemperature(BluetoothGattCharacteristic c) {
        int offset = 2;
        return shortUnsignedAtOffset(c, offset) / 128.0;
    }

    private static double extractAmbientLight(BluetoothGattCharacteristic c) {

        int mantissa;
        int exponent;

        byte[] b = new byte[2];
        b[0] = c.getValue()[0];
        b[1] = c.getValue()[1];
        Integer sfloat= shortUnsignedAtOffset(b, 0);

        mantissa = sfloat & 0x0FFF;
        exponent = (sfloat >> 12) & 0xFF;

        double output;
        double magnitude = pow(2.0f, exponent);
        output = (mantissa * magnitude)/100.0;

        return output;
    }

    /**
     * Gyroscope, Magnetometer, Barometer, IR temperature
     * all store 16 bit two's complement values in the awkward format
     * LSB MSB, which cannot be directly parsed as getIntValue(FORMAT_SINT16, offset)
     * because the bytes are stored in the "wrong" direction.
     *
     * This function extracts these 16 bit two's complement values.
     * */


    private static Integer twentyFourBitUnsignedAtOffset(byte[] c, int offset) {
        Integer lowerByte = (int) c[offset] & 0xFF;
        Integer mediumByte = (int) c[offset+1] & 0xFF;
        Integer upperByte = (int) c[offset + 2] & 0xFF;
        return (upperByte << 16) + (mediumByte << 8) + lowerByte;
    }

    private static Integer shortUnsignedAtOffset(BluetoothGattCharacteristic c, int offset) {

        Integer lowerByte = (int) c.getValue()[offset] & 0xFF;
        Integer upperByte = (int) c.getValue()[offset+1] & 0xFF;
        return (upperByte << 8) + lowerByte;
    }

    private static Integer shortUnsignedAtOffset(byte[] c, int offset) {
        Integer lowerByte = (int) c[offset] & 0xFF;
        Integer upperByte = (int) c[offset+1] & 0xFF;
        return (upperByte << 8) + lowerByte;
    }

    public static BluetoothGatt getmBluetoothGatt() {
        return mBluetoothGatt;
    }
}
