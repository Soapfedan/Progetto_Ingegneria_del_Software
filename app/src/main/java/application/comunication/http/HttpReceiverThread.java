package application.comunication.http;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * Created by picci on 09/05/2017.
 */

public class HttpReceiverThread extends Thread {

    Socket socket;

    HttpReceiverThread(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        BufferedReader is;
        PrintWriter os;
        String line;



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
}

