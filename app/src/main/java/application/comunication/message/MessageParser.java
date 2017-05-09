package application.comunication.message;


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

    public static ArrayList<String> messageElements;

    public static ArrayList<String>[] array;

    public static ArrayList<String> analyzeMessage(String s,ArrayList<String> keys) throws JSONException {
        messageElements.clear();
        JSONObject obj = new JSONObject(s);
        for(int k=0;k<keys.size();k++){
            try {
                messageElements.add(obj.getString(keys.get(k)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return messageElements;
    }

    public static ArrayList<String>[] analyzeMessageArray(String s,ArrayList<String> keys) throws JSONException {
        JSONArray jsonArray = new JSONArray(s);
        array = new ArrayList[jsonArray.length()];

        JSONObject jsonobject;

        messageElements.clear();
        for (int i = 0; i < jsonArray.length(); i++) {
            jsonobject = jsonArray.getJSONObject(i);
            for(int k=0;k<keys.size();k++){
                try {
                    messageElements.add(jsonobject.getString(keys.get(k)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            array[i] = messageElements;
        }
        return array;
    }
}
