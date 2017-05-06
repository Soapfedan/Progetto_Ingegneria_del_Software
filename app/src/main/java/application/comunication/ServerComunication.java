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
import java.util.concurrent.ExecutionException;

import application.comunication.http.GetRequest;
import application.comunication.http.PostRequest;
import application.comunication.message.MessageBuilder;


/**
 * Created by Federico-PC on 23/03/2017.
 */

public class ServerComunication{


    private static String hostname="172.23.159.153";
    private static String host2 = "192.168.1.102";
    private static String hostMaster = host2;
    private static JSONObject jsonObject;

    public static JSONObject getRequest() throws ExecutionException, InterruptedException {

        return new GetRequest().execute(hostMaster,"user/test").get();
    }

    public static String login(String mail, String pass) throws ExecutionException, InterruptedException {
        ArrayList<String> name = new ArrayList<>();
        ArrayList<String> value = new ArrayList<>();
        name.add("email");
        name.add("password");
        value.add(mail);
        value.add(pass);
        String mex = MessageBuilder.builder(name,value,value.size(),0);
        return new PostRequest().execute(hostMaster,"user/login",mex).get();
    }
}
