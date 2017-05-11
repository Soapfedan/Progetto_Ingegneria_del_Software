package application.comunication.http;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import application.comunication.message.MessageBuilder;

/**
 * Created by Federico-PC on 08/04/2017.
 */

public class PostRequest extends AsyncTask<String,Void,String> {

    private String result;

    private static final String PORT = "8080";

    private static final String SERVER_ID = "RestfulServerTID";


    @Override
    protected String doInBackground(String... urls) {

        //        String url = "http://" + urls[0] + ":" + PORT + "/" + SERVER_ID +"/" + urls[1];
        // set the connection timeout value to 30 seconds (30000 milliseconds)

        URL url = null;
        try {
            url = new URL("http://" + urls[0] + ":" + PORT + "/" + SERVER_ID + "/" + urls[1]);
            Log.i("URL","url: " + url.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        connection.setConnectTimeout(5000);
        connection.setConnectTimeout(60000);

        try {

            connection.setDoOutput(true);   //abilita la scrittura
            connection.setRequestMethod("POST");
                //scritto header http del messaggio (per inviare json)
            connection.setRequestProperty("Content-Type", "application/json");
            //connection.setRequestProperty("Accept", "application/json");

//            connection.connect();

            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
            Log.i("json",urls[2]);
            wr.write(urls[2]);
            wr.flush();
            wr.close();


//            DataOutputStream localDataOutputStream = new DataOutputStream(connection.getOutputStream());
//            localDataOutputStream.writeBytes(urls[2]);
//            localDataOutputStream.flush();
//            localDataOutputStream.close();

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    is.close();
                }
                result = sb.toString();

            }
            else if (connection.getResponseCode()==201) {
                result = "true";
            }
            else if (connection.getResponseCode()==500) {
                result = "false";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                Log.i("Response"," " + connection.getResponseCode());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (connection!=null) connection.disconnect();


        }
        return result;
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
