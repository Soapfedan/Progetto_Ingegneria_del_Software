package application.comunication;



import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import application.comunication.http.DeleteRequest;
import application.comunication.http.GetRequest;
import application.comunication.http.PostRequest;
import application.comunication.http.PutRequest;
import application.comunication.message.MessageBuilder;
import application.comunication.message.MessageParser;
import application.sharedstorage.UserPositions;
import application.user.UserProfile;


/**
 * Created by Federico-PC on 23/03/2017.
 */

public class ServerComunication{


    private static String hostname="172.23.159.153";
    private static String host2 = "192.168.1.102";
    private static String hostMaster = hostname;
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

    public static JSONObject getRequest() throws ExecutionException, InterruptedException {

        //MessageParser.analyzeMessage(new GetRequest().execute(hostMaster,"user/test").get());
        return null;
    }

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

    public static void sendValue(String message) throws ExecutionException, InterruptedException {
        new PostRequest().execute(hostMaster,"beaconvalue/insertvalue",message).get();
    }
    public static void sendPosition(String message) throws ExecutionException, InterruptedException {
        new PutRequest().execute(hostMaster,"position/setposition",message).get();
    }

    //TODO DA INSERIRE NELLA ONDESTROY DELLA HOME
    public static void deletePosition(String ip) throws ExecutionException, InterruptedException {
        new DeleteRequest().execute(hostMaster,"position/deleteuser/"+ip).get();
    }

    public static HashMap<String,String>[] getBuildingBeacon(String building) throws ExecutionException, InterruptedException, JSONException {
        ArrayList<String> keys = new ArrayList<>();
        keys.add("beacon_ID");
        keys.add("floor");
        keys.add("x");
        keys.add("y");
        return MessageParser.analyzeMessageArray(new GetRequest().execute(hostMaster,"beaconnode/getallnodes/"+building).get(),keys);
    }
}
