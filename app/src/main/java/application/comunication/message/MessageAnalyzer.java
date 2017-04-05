package application.comunication.message;

import android.util.JsonReader;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import application.comunication.ServerComunication;

/**
 * Created by Federico-PC on 23/03/2017.
 */

public class MessageAnalyzer {

    public static HashMap<String,String> messageElements;


    public static HashMap<String,String> analyzeMessage() throws IOException, JSONException {

        if(!messageElements.isEmpty())
            messageElements.clear();


       /* JSONArray temp = jsonObject.getJSONArray("name");
        int length = temp.length();
        if (length > 0) {
            String [] recipients = new String [length];
            for (int i = 0; i < length; i++) {
                recipients[i] = temp.getString(i);
            }
        }
        */



        return messageElements;
    }
}
