package application.user;

import java.util.ArrayList;

import java.net.NetworkInterface;
import java.util.*;

import application.MainApplication;

/**
 * Created by Federico-PC on 05/12/2016.
 */

public class UserHandler {
    private static String macAddress;
    private static String email;
    private static String nome;
    private static String cognome;
    //private ArrayList<Beacon> beacons;


    public static void init(){
        macAddress = obtainMacAddr();
    }

    public static String getMail() {
        return email;
    }

    public static boolean checkUser(String e){
        boolean b;
        if (MainApplication.getDB().checkNewUser(e)==0) b = false;
        else b = true;
        return b;
    }

    public static void logup(HashMap<String,String> info){
        //aggiunto il metodo open, in modo che venga creato il collegamento
        //e lavori su un db writable
        MainApplication.getDB().open().createUser(info.get("email"),info.get("pass"),info.get("name"),
                info.get("surname"),info.get("birth_date"),info.get("birth_city"),
                info.get("province"),info.get("state"),info.get("phone"),
                info.get("sex"), info.get("personal_number"));
        //finito ad usare il db, viene chiuso
        MainApplication.getDB().close();
    }

    public static void logout() {
        email = null;
        //rende nulli anche altri elementi
    }

    public static boolean isLogged() {
        boolean b = false;
        if (email!=null) b=true;
        return b;
    }

    public static void login(String name){
        //  TODO: va effettuato qua il controllo sui dati dell'utente??
        email = name;
    }

    public void searchRoom(){
        // TODO: 05/12/2016  procedura di ricerca delle aule offline
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
}
