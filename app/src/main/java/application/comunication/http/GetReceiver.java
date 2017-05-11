package application.comunication.http;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.logging.Logger;


/**
 * Created by picci on 03/05/2017.
 */

public class GetReceiver extends Thread {
    ServerSocket httpServerSocket;
    static final int HttpServerPORT = 8888;

    @Override
    public void run() {
        Socket socket = null;

        try {
            httpServerSocket = new ServerSocket(HttpServerPORT);

            while(true){
                socket = httpServerSocket.accept();
                HttpReceiverThread httpResponseThread =
                        new HttpReceiverThread(
                                socket);
                httpResponseThread.start();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    public boolean status()
    {
        if (httpServerSocket == null)
            return false;
        else
            return true;
    }
    public void closeConnection() throws IOException {
        try {
            httpServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
