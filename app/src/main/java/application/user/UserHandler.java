package application.user;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

import java.net.NetworkInterface;
import java.util.*;
import java.util.concurrent.ExecutionException;

import application.MainApplication;
import application.beacon.BeaconScanner;
import application.comunication.ServerComunication;
import application.comunication.message.MessageBuilder;

/**
 * Created by Federico-PC on 05/12/2016.
 */

public class UserHandler {
    private static String macAddress;
    private static String email;
    private static String nome;
    private static String cognome;
    private static UserProfile profile;
    private static SharedPreferences pref;
    private static Editor editor;
    //private ArrayList<Beacon> beacons;
    private static String ipAddress;

    public static void init(){
        macAddress = obtainMacAddr();
        editor = pref.edit();
        ipAddress = obtainLocalIpAddress();
        if(!MainApplication.getOnlineMode()){
            initializePosition();
        }


    }

    public static void initializePosition() {
        String mex;
        ArrayList<String> keys = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();
        keys.add("beacon_ID");
        keys.add("user_ID");
        keys.add("nome");
        keys.add("cognome");
        if(MainApplication.getScanner().getCurrentBeacon()==null) {
            values.add("unknown");
        }else{
            values.add(MainApplication.getScanner().getCurrentBeacon().getAddress());
        }
        values.add(getIpAddress());
        if(UserHandler.isLogged()){
            values.add(getNome());
            values.add(getCognome());
        }else {
            values.add("Guest");
            values.add("Guest");
        }

        mex = MessageBuilder.builder(keys,values,keys.size(),0);
        Log.i("mex",mex);
        if(!MainApplication.getOnlineMode()) {
            try {
                ServerComunication.sendPosition(mex);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            }
    }

    public static String getIpAddress() {
        return ipAddress;
    }

    public static String getMail() {
        return email;
    }
/*
    public static boolean checkUser(String e){
        boolean b;
        if (MainApplication.getDB().checkNewUser(e)==0) b = false;
        else b = true;
        return b;
    }
*/
    public static boolean logup(HashMap<String,String> info){
        //aggiunto il metodo open, in modo che venga creato il collegamento
        //e lavori su un db writable
        if(!MainApplication.getOnlineMode()) {
            try {
               return ServerComunication.logup(info);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }/*
        MainApplication.getDB().open().createUser(info.get("email"),info.get("pass"),info.get("name"),
                info.get("surname"),info.get("birth_date"),info.get("birth_city"),
                info.get("province"),info.get("state"),info.get("phone"),
                info.get("sex"), info.get("personal_number"));
        //finito ad usare il db, viene chiuso
        MainApplication.getDB().close();*/
        return false;
    }

    public static void editProfile(HashMap<String,String> info){
        //aggiunto il metodo open, in modo che venga creato il collegamento
        //e lavori su un db writable
        /*
        MainApplication.getDB().open().updateProfile(info.get("email"),info.get("pass"),info.get("name"),
                info.get("surname"),info.get("birth_date"),info.get("birth_city"),
                info.get("province"),info.get("state"),info.get("phone"),
                info.get("sex"), info.get("personal_number"));
        //finito ad usare il db, viene chiuso
        MainApplication.getDB().close();*/
        if(!MainApplication.getOnlineMode()) {
            try {
                ServerComunication.editprofile(info);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void logout() {
        email = null;
        cleanEditor();
        initializePosition();

        //rende nulli anche altri elementi
    }

    public static UserProfile getInformation(String email){

        //return MainApplication.getDB().open().getUserProfile(email);
        if(!MainApplication.getOnlineMode()) {
            try {
                return ServerComunication.getprofile(email);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static void updateEditor() {
        editor.putString("email",email);
        editor.putString("nome",nome);
        editor.putString("cognome",cognome);
        editor.commit();
    }

    private static void cleanEditor() {
        editor.clear();
        editor.commit();
    }
        //controlla che utente sia loggato o che ci siano dati nella sharedpreferencies
    public static boolean isLogged() {
        boolean b = false;
        if(email!=null) b = true;
        else if (pref.getString("email",null)!=null) {
            setInfo(pref.getString("email",null),pref.getString("nome",null),pref.getString("cognome",null));
            b = true;
        }
        return b;
    }

    public static boolean login(String name, String pass, boolean chk){
        boolean b = false;
            //assegnato valore solo se si trova utente con quel nome, altrimenti null
        /*UserProfile u= MainApplication.getDB().open().getUserProfile(name);
        if (u==null) {
            b = false;
        }
        else {
                //si confronta la password dell'utente con quell salvata nello userprofile
            if (u.getPassword().equals(pass)) {
                email=name;
                nome = u.nome;
                cognome = u.cognome;

                b = true;
            }
            else b = false;
        }*/

        if(!MainApplication.getOnlineMode()) {
            try {
              b =  ServerComunication.login(name,pass);
                if (b) {
                    UserProfile u = null;
                    try {
                        u = ServerComunication.getprofile(name);
                        Log.i("nome e cognome"," "+u.getNome()+" "+u.getNome());
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    email=name;

                    nome = u.getNome();
                    cognome = u.getCognome();

                    if(chk)updateEditor();
                    else cleanEditor();
                    b = true;
                    initializePosition();
                }
                else b = false;

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.i("Risp "," "+b);
        return b;
    }


    public static String getNome() {
        return nome;
    }

    public static String getCognome() {
        return cognome;
    }

    public static String obtainMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            //handle exception
        }
        return "";
    }

        //calcola l'indirizzo ip dell'utente
    public static String obtainLocalIpAddress(){
        WifiManager wifiMan = (WifiManager) MainApplication.getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        int ipAddress = wifiInf.getIpAddress();
        String ip = String.format("%d.%d.%d.%d", (ipAddress & 0xff),(ipAddress >> 8 & 0xff),(ipAddress >> 16 & 0xff),(ipAddress >> 24 & 0xff));
//        Log.i("IP","IP " + ip);
        return ip;
    }

    public static void setPref(SharedPreferences p) {
        pref = p;
    }

    public static void setInfo(String e,String n,String c){
        email = e;
        nome = n;
        cognome = c;
    }
}
