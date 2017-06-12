package application.comunication;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import application.comunication.http.DeleteRequest;
import application.comunication.http.GetRequest;
import application.comunication.http.PostRequest;
import application.comunication.http.PutRequest;
import application.comunication.message.MessageBuilder;
import application.comunication.message.MessageParser;
import application.user.UserProfile;


/**
 * Questa classe serve per gestire la comunicazione con il server
 */

public class ServerComunication{

    private static String hostname="172.23.159.153";
    private static String host2 = "192.168.1.102";
    private static String hostMaster; //= hostname;
    private static JSONObject jsonObject;
    private static final ArrayList<String> userProfileKeys = new ArrayList<String>(){{
        add("email");
        add("password");
        add("nome");
        add("cognome");
        add("data_nascita");
        add("luogo_nascita");
        add("provincia");
        add("stato");
        add("telefono");
        add("sesso");
        add("cod_fis");
    }};

    /**
     *  Metodo che permette di compiere la login
     * @param mail email dell'utente
     * @param pass password dell'utente
     * @return un boolean che indica se la richiesta è stata fatta con successo o no
     * @throws ExecutionException
     * @throws InterruptedException
     */

    public static boolean login(String mail, String pass) throws ExecutionException, InterruptedException {
        ArrayList<String> name = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();
        name.add("email");
        name.add("password");
        value.add(mail);
        value.add(pass);
        String mex = MessageBuilder.builder(name,value,value.size(),0);
        return Boolean.parseBoolean(new PostRequest().execute(hostMaster,"user/login",mex).get());
    }

    /**
     *  Metodo che permette all'utente di inviare i suoi dati per iscriversi
     * @param info informazioni dell'utente
     * @return un boolean che indica se la richiesta è stata fatta con successo o no
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static boolean logup(HashMap<String,String> info) throws ExecutionException, InterruptedException {
        ArrayList<String> keys = userProfileKeys;
        ArrayList<String> values = new ArrayList<>();


        values.add(info.get("email"));
        values.add(info.get("pass"));
        values.add(info.get("name"));
        values.add(info.get("surname"));
        values.add(info.get("birth_date"));
        values.add(info.get("birth_city"));
        values.add(info.get("province"));
        values.add(info.get("state"));
        values.add(info.get("phone"));
        values.add(info.get("sex"));
        values.add(info.get("personal_number"));

        String msg = MessageBuilder.builder(keys,values,values.size(),0);

        return Boolean.parseBoolean(new PostRequest().execute(hostMaster,"user/createuser",msg).get());
    }

    /**
     * Metodo che restituisce tutte le informazioni di un utente
     * @param email email dell'utente di cui voglio prendere le informazioni
     * @return l'oggetto userprofile che contiente tutte le info dell'utente
     * @throws ExecutionException
     * @throws InterruptedException
     */

    public static UserProfile getprofile(String email) throws ExecutionException, InterruptedException {
        HashMap<String,String> info = new HashMap<>();
        UserProfile profile = null;
        try {
           info =  MessageParser.analyzeMessage(new GetRequest().execute(hostMaster,"user/getuser/"+email).get(),userProfileKeys);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(info!=null){
             profile = new UserProfile(info.get("email"),info.get("password"),info.get("nome"),
                    info.get("cognome"),info.get("data_nascita"),info.get("luogo_nascita"),
                    info.get("provincia"),info.get("stato"),info.get("telefono"),
                    info.get("sesso"), info.get("cod_fis"));

        }

        return profile;

    }


    /**
     * Metodo permette di modificare i dati dell'utente
     * @param info
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static void editprofile(HashMap<String,String> info) throws ExecutionException, InterruptedException {
        ArrayList<String> keys = userProfileKeys;
        ArrayList<String> values = new ArrayList<>();


        values.add(info.get("email"));
        values.add(info.get("pass"));
        values.add(info.get("name"));
        values.add(info.get("surname"));
        values.add(info.get("birth_date"));
        values.add(info.get("birth_city"));
        values.add(info.get("province"));
        values.add(info.get("state"));
        values.add(info.get("phone"));
        values.add(info.get("sex"));
        values.add(info.get("personal_number"));

        String msg = MessageBuilder.builder(keys,values,values.size(),0);

        new PutRequest().execute(hostMaster,"user/updateuser",msg).get();
    }

    /**
     * Metodo che invia i dati dei sensori di un beacon al server
     * @param message stringa che contiene tutte i dati prelevati
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static void sendValue(String message) throws ExecutionException, InterruptedException {
        new PostRequest().execute(hostMaster,"beaconvalue/insertvalue",message).get();
    }

    /**
     * Metodo che invia la posizione dell'utente
     * @param message stringa che contiene i dati sulla posizione
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static void sendPosition(String message) throws ExecutionException, InterruptedException {
        new PutRequest().execute(hostMaster,"position/setposition",message).get();
    }

    /**
     * Metodo che cancella la posizione di un utente
     * @param ip ip dell'utente che dovrò cancellare
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static void deletePosition(String ip) throws ExecutionException, InterruptedException {
        new DeleteRequest().execute(hostMaster,"position/deleteuser/"+ip).get();
    }

    /**
     * Metodo che restitutisce tutti i beacon di una determinata struttura passata in input
     * @param building nome dell'edificio
     * @return la lista di utti i beacon dell'edificio
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws JSONException
     */
    public static HashMap<String,String>[] getBuildingBeacon(String building) throws ExecutionException, InterruptedException, JSONException {
        ArrayList<String> keys = new ArrayList<>();
        keys.add("beacon_ID");
        keys.add("floor");
        keys.add("x");
        keys.add("y");
        return MessageParser.analyzeMessageArray(new GetRequest().execute(hostMaster,"beaconnode/getallnodes/"+building).get(),keys,"beacons");
    }

    public static HashMap<String,String> getScanParameters() throws ExecutionException, InterruptedException, JSONException {
        ArrayList<String> keys = new ArrayList<>();
        keys.add("scanPeriodNormal");
        keys.add("scanPeriodSearching");
        keys.add("scanPeriodEmergency");
        keys.add("periodBetweenScanNormal");
        keys.add("periodBetweenScanSearching");
        keys.add("periodBetweenScanEmergency");

        return MessageParser.analyzeMessage(new GetRequest().execute(hostMaster,"parameters").get(),keys);
    }

    /**
     * Metodo che restituisce la lista di tutte le stanze/aule di un determinato edificio
     * @param building nome dell'edificio
     * @return lista di tutte le stanze
     * @throws ExecutionException
     * @throws InterruptedException
     * @throws JSONException
     */

    public static HashMap<String,String>[] getBuildingRoom(String building) throws ExecutionException, InterruptedException, JSONException {
        ArrayList<String> keys = new ArrayList<>();
        keys.add("x");
        keys.add("y");
        keys.add("floor");
        keys.add("width");
        keys.add("room");
        return MessageParser.analyzeMessageArray(new GetRequest().execute(hostMaster,"room/getallrooms/"+building).get(),keys,"rooms");
    }

    /**
     * Metodo che preleva la versione del csv che è situata sul server
     * @return un intero che rappresenta la versione corrente del csv sul server
     */
    public static int checkVersion() {
        int version = -1;
        String s;
        try {
            s = new GetRequest().execute(hostMaster,"getcsvversion").get();
            if (s!=null && !s.equals("0")) {
                Log.i("s",s);
                version = Integer.parseInt(s);
            }
        } catch (InterruptedException e) {

        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return version;
    }

    /**
     * Metodo che va a testare se è disponibile una connessione con un certo ip
     * @param ip ip dell'ipotetico server con cui voglio comunicare
     * @return un boolean che indica se la connessione è andata a buon fine oppure no
     */
    public static boolean handShake(String ip) {
        boolean b;
        try {
            String s = new GetRequest().execute(ip,"testconnection").get();
            Log.i("s",s);
            b = Boolean.parseBoolean(s);
        } catch (Exception e) {
            b = false;
        }
        return b;
    }



    public static String getIP() {
        return hostMaster;
    }

    public static void setHostMaster (String h) {
        hostMaster = h;
    }
}
