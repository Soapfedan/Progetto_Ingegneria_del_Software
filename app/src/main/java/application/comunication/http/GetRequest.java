package application.comunication.http;

import android.os.AsyncTask;
import android.util.Log;

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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Federico-PC on 08/04/2017.
 */

public class GetRequest extends AsyncTask<String,Void,String> {

    private String json;

    private static final String PORT = "8080";

    private static final String SERVER_ID = "RestfulServerTID";

    private URL url;

    private HttpURLConnection connection;

    @Override
    protected String doInBackground(String... urls) {


//        String url = "http://" + urls[0] + ":" + PORT + "/" + SERVER_ID +"/" + urls[1];
        // set the connection timeout value to 30 seconds (30000 milliseconds)

        url = null;
        try {
            url = new URL("http://" + urls[0] + ":" + PORT + "/" + SERVER_ID + "/" + urls[1]);
            Log.i("URL","url: " + url.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        connection.setConnectTimeout(5000);
        try {
            connection.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }

        try {
            if (connection.getResponseCode() == 200) {
                InputStreamReader is = new InputStreamReader(connection.getInputStream());
                BufferedReader read = new BufferedReader(is);
                String s = null;
                StringBuffer sb = new StringBuffer();
                try {
                    while ((s = read.readLine()) != null) {
                        sb.append(s);
                    }
                    read.close();
                    json = sb.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    is.close();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (connection!=null) connection.disconnect();
        }

        return json;
    }

//        final HttpParams httpParams = new BasicHttpParams();
//        HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
//        HttpClient request =  new DefaultHttpClient(httpParams);
//        HttpGet get = new HttpGet(url);
//        HttpResponse response = null;
//        try {
//            response = request.execute(get);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        int responseCode = response.getStatusLine().getStatusCode();
//        if (responseCode == 200) {
//            InputStream istream = null;
//            try {
//                istream = response.getEntity().getContent();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            BufferedReader r = new BufferedReader(new InputStreamReader(istream));
//            String s = null;
//            StringBuffer sb = new StringBuffer();
//            try {
//                while ((s = r.readLine()) != null) {
//                    sb.append(s);
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            try {
//                json = new JSONObject(sb.toString());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }
//        return json;
//    }
}
