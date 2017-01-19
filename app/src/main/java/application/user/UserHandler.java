package application.user;

import android.app.AlertDialog;

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
    private static UserProfile profile;
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

    public static void editProfile(HashMap<String,String> info){
        //aggiunto il metodo open, in modo che venga creato il collegamento
        //e lavori su un db writable
        MainApplication.getDB().open().updateProfile(info.get("email"),info.get("pass"),info.get("name"),
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

    public static UserProfile getInformation(String email){
        return MainApplication.getDB().open().getUserProfile(email);
    }



    public static boolean isLogged() {
        boolean b = false;
        if (email!=null) b=true;
        return b;
    }

    public static boolean login(String name, String pass){
        boolean b = false;
            //assegnato valore solo se si trova utente con quel nome, altrimenti null
        UserProfile u= MainApplication.getDB().open().getUserProfile(name);
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
        }
        return b;
    }


    public void searchRoom(){
        // TODO: 05/12/2016  procedura di ricerca delle aule offline
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
}
