package application.comunication.message;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Federico-PC on 23/03/2017.
 */

public class MessageParser {

    public static HashMap<String,String> messageElements;

    public static HashMap<String,String>[] array;

    public static HashMap<String,String> analyzeMessage(String s,ArrayList<String> keys) throws JSONException {
        messageElements = new HashMap<>();
        //messageElements.clear();
        Log.i("Messaggio da scomporre",s);
        JSONObject obj = new JSONObject(s);
        for(int k=0;k<keys.size();k++){
            try {
                messageElements.put(keys.get(k),obj.getString(keys.get(k)));
                Log.i("key and value :",keys.get(k)+" "+obj.getString(keys.get(k)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return messageElements;
    }

    public static HashMap<String,String>[] analyzeMessageArray(String s,ArrayList<String> keys, String name) throws JSONException {
        JSONObject json = new JSONObject(s);
        JSONArray jsonArray = json.getJSONArray(name);
//        messageElements = new HashMap<>();
        array = new HashMap[jsonArray.length()];

        JSONObject jsonobject;

//        messageElements.clear();
        for (int i = 0; i < jsonArray.length(); i++) {
            jsonobject = jsonArray.getJSONObject(i);
            messageElements = new HashMap<>();
            messageElements.clear();
            for(int k=0;k<keys.size();k++){
                try {
                    messageElements.put(keys.get(k),jsonobject.getString(keys.get(k)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            array[i] = messageElements;
        }
        return array;
    }
}
