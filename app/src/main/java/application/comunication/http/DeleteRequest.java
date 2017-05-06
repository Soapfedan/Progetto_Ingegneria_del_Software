package application.comunication.http;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Federico-PC on 08/04/2017.
 */

public class DeleteRequest extends AsyncTask<String,Void,String> {

    private String result;

    private static final String PORT = "8080";

    private static final String SERVER_ID = "RestfulServerTID";

    private static final String DELETE_USER = "deleteuser";
    @Override
    protected String doInBackground(String... urls) {
        URL url = null;
        try {
            url = new URL("http://" + urls[0] + ":" + PORT + "/" + SERVER_ID + "/" + DELETE_USER + "/" +urls[1]);
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

//            connection.setDoOutput(true);   //abilita la scrittura
            connection.setRequestMethod("DELETE");
            //scritto header http del messaggio (per inviare json)
//            connection.setRequestProperty("Content-Type", "application/json");
//            connection.setRequestProperty("Accept", "application/json");
//
////            connection.connect();
//
//            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
//            wr.write(urls[2]);
//            wr.flush();
//            wr.close();


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
                result = "delete effettuata con successo";
            }
            else if (connection.getResponseCode()==500) {
                result = "delete non andata a buon fine";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (connection!=null) connection.disconnect();
        }
        return result;
    }
}
