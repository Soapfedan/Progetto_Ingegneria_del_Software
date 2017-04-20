package application.comunication.http;

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

/**
 * Created by Federico-PC on 08/04/2017.
 */

public class GetRequest extends AsyncTask<String,Void,JSONObject> {

    private JSONObject json;

    @Override
    protected JSONObject doInBackground(String... urls) {


        String url = "http://" + urls[0] + ":8080/RestfulServerTID/todo";
        // set the connection timeout value to 30 seconds (30000 milliseconds)
        final HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
        HttpClient request =  new DefaultHttpClient(httpParams);
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