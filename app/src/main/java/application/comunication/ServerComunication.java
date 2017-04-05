package application.comunication;



import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * Created by Federico-PC on 23/03/2017.
 */

public class ServerComunication extends AsyncTask<String,Void,JSONObject>{


    private String hostname="173.23.159.153";
    private JSONObject json;


/*
    public JSONObject getJSON() throws IOException, JSONException {

    }*/


    @Override
    protected JSONObject doInBackground(String... params) {

        String url = "http://" + hostname + "RestfulServerTID/todo";
        String[] persone = null; // conterrà i risultati
        HttpClient request = new DefaultHttpClient();
        HttpGet get = new HttpGet(url);
        HttpResponse response = null;
        try {
            response = request.execute(get);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int responseCode = response.getStatusLine().getStatusCode();
        if (responseCode == 200) {
            InputStream istream = null;
            try {
                istream = response.getEntity().getContent();
            } catch (IOException e) {
                e.printStackTrace();
            }
            BufferedReader r = new BufferedReader(new InputStreamReader(istream));
            String s = null;
            StringBuffer sb = new StringBuffer();
            try {
                while ((s = r.readLine()) != null) {
                    sb.append(s);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                json = new JSONObject(sb.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return json;
    }
}
