package application.comunication.http;

import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

import application.MainApplication;
import application.comunication.message.MessageParser;
import application.maps.components.Notify;
import application.sharedstorage.Data;
import application.sharedstorage.DataListener;

/**
 * Created by picci on 09/05/2017.
 */

public class HttpReceiverThread extends Thread implements DataListener{

    private Socket socket;
    private String notifies;
    private final ArrayList<String> notifyKeys = new ArrayList<String>(){{
        add("id");
        add("cod_cat");
        add("floor");
        add("room");
    }};

    HttpReceiverThread(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedReader is;
        PrintWriter os;
        String line;
        Data.getNotification().addDataListener(this);


        try {
            InputStreamReader lettore = new InputStreamReader(socket.getInputStream());
            is = new BufferedReader(lettore);
            line = is.readLine();
            StringBuilder raw = new StringBuilder();
            raw.append("" + line);
            boolean isPost = line.startsWith("POST");
            int contentLength = 0;
            String request;
            /*while ((request = is.readLine()) != null){
                Log.i("prova",request);
            }*/

            while (!(line = is.readLine()).equals("")) {
                raw.append('\n' + line);
                if (isPost) {
                    final String contentHeader = "Content-Length: ";
                    if (line.startsWith(contentHeader)) {
                        contentLength = Integer.parseInt(line.substring(contentHeader.length()));
                    }
                }
            }
            StringBuilder body = new StringBuilder();
            if (isPost) {
                int c = 0;
                for (int i = 0; i < contentLength; i++) {
                    c = is.read();
                    if(c>20)
                        body.append( (char) c);
                    //Log.i("JCD", "POST: " + ((char) c) + " " + c);
                }
            }
            //Charset.forName("utf-8").encode(body.toString());
            Log.i("POST: ", body.toString());
            notifies = body.toString();
            update();

            os = new PrintWriter(socket.getOutputStream(), true);

            String response =
                    "<html><head></head>" +
                            "<body>" +
                            "<h1>" + "INVIO RIUSCITO" + "</h1>" +
                            "</body></html>";

            os.print("HTTP/1.0 200" + "\r\n");
            os.print("Content type: text/html" + "\r\n");
            os.print("Content length: " + response.length() + "\r\n");
            os.print("\r\n");
            os.print(response + "\r\n");
            os.flush();
            socket.close();


           /* msgLog += "Request of " + request
                    + " from " + socket.getInetAddress().toString() + "\n";
            MainActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    infoMsg.setText(msgLog);
                }
            }); */

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return;
    }

    @Override
    public void update() {
        HashMap<String,String>[] not;
        try {
          not = MessageParser.analyzeMessageArray(notifies,notifyKeys,"notifications");
            ArrayList<Notify> n = new ArrayList<>();
            for(int i = 0;i<not.length;i++){
                n.add(new Notify(Integer.parseInt(not[i].get("id")),
                                 Integer.parseInt(not[i].get("cod_cat")),
                                 not[i].get("floor"),
                                 not[i].get("room")));
            }
            //controllo se ci sono notifiche se è vero vado in modalita emergenza, in caso contrario uscirò da tale modalità
            Log.e("Lunghezza not ",not.length+"");
            if(not.length==0 && MainApplication.getEmergency()){
                MainApplication.setEmergency(false);
                Data.getNotification().getNotifies().clear();
            }else if (not.length>0 && !MainApplication.getEmergency()){
                if(MainApplication.getVisible()) {
                    MainApplication.setEmergency(true);
                }
                else {
                    MainApplication.launchNotification();
                }
//                MainApplication.launchNotification();
                Data.getNotification().setNotifies(n);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void retrive() {

    }
}

