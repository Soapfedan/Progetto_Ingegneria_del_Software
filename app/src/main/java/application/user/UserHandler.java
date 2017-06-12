package application.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import java.util.ArrayList;
import java.net.NetworkInterface;
import java.util.*;
import java.util.concurrent.ExecutionException;
import application.MainApplication;
import application.comunication.ServerComunication;
import application.comunication.message.MessageBuilder;

/**
 * Classe che gestirà tutte le operazioni su un utente, quali login, iscrizione, modifica profilo, logout, ecc..
 * Inoltre c'è un campo pref (SharedPreferences) che conterrà le informazioni, quali username, nome e cognome.
 * Queste vengono mantenute all'interno della struttura anche dopo la chiusura dell'applicazione. Si tratta di uno
 * storage di tipo Session. In questo modo se l'utente rimane loggato, ad un nuovo accesso avrà ancora in memoria
 * le informazioni pregresse.
 */

public class UserHandler {
    private static String macAddress;
    private static String email;
    private static String nome;
    private static String cognome;
    private static UserProfile profile;
        //variabile di tipo sessione
    private static SharedPreferences pref;
    private static Editor editor;
        //private ArrayList<Beacon> beacons;
    private static String ipAddress;

    public static void init(){
        macAddress = obtainMacAddr();
        editor = pref.edit();
        ipAddress = obtainLocalIpAddress();

    }

    /**
     * Metodo che inizializza la posizione dell'utente e costruisce il messaggio considerando le seguenti condizioni:
     * - se è già loggato nel messaggio verranno inseriti Nome e Cognome dell'utente altrimenti verrà utilizzata la coppia Guest e Guest
     * - se si conosce la sua posizione, rilevata da un beacon, si inserisce il codice del beacon altrimenti posizione sconosciuta (unkonwn).
     *
     */

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
        if(MainApplication.getOnlineMode()) {
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

    /**
     * Metodo che permette l'iscrizione di un utente, inviando al server le informazioni già controllate in fase di inserimento.
     * @param info informazioni dell'utente
     * @return un boolean che indica se la richiesta è andata a buon fine o no
     */
    public static boolean logup(HashMap<String,String> info){

        if(MainApplication.getOnlineMode()) {
            try {
               return ServerComunication.logup(info);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    /**
     * Metodo che permette la modifica delle informazioni di un utente inviandole al server
     * @param info informazioni dell'utente
     * @return un boolean che indica se la richiesta è andata a buon fine o no
     */

    public static void editProfile(HashMap<String,String> info){

        if(MainApplication.getOnlineMode()) {
            try {
                ServerComunication.editprofile(info);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Metodo che permette di effettuare la logout
     */

    public static void logout() {
        email = null;
        cleanEditor();
        if(MainApplication.getOnlineMode()) {
            initializePosition();
        }

    }

    /**
     * Metodo che restituisce le informazioni di un utente
     * @param email : email dell'utente
     * @return l'oggetto UserProfile che contiene tutte le info dell'utente
     */

    public static UserProfile getInformation(String email){

        if(MainApplication.getOnlineMode()) {
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

    /**
     * Modifica il contenute dell'oggetto session Pref
     */

    private static void updateEditor() {
        editor.putString("email",email);
        editor.putString("nome",nome);
        editor.putString("cognome",cognome);
        editor.commit();
    }

    /**
     * Resetta le informazioni dentro l'oggetto session Pref
     */

    private static void cleanEditor() {
        editor.clear();
        editor.commit();
    }

    /**
     * controlla che utente sia loggato o che ci siano dati nella sharedpreferencies
     * @return boolen che indica lo stato dell'utente
     */
    public static boolean isLogged() {
        boolean b = false;
        if(email!=null) b = true;
        else if (pref.getString("email",null)!=null) {
            setInfo(pref.getString("email",null),pref.getString("nome",null),pref.getString("cognome",null));
            b = true;
        }
        return b;
    }

    /**
     * Metodo che permette di compiere la login
     * @param name mail dell'utente
     * @param pass password dell'utente
     * @param chk parametro boolean che indica se l'utente ha scelto o no se mantenere i suoi dati di accesso in memoria. Basta pensare alla classica Ricordami su un form di login
     * @return
     */
    public static boolean login(String name, String pass, boolean chk){
        boolean b = false;


        if(MainApplication.getOnlineMode()) {
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
        Log.i("Risp "," " + b);
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
