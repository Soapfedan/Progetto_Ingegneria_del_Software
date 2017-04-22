package application.comunication.message;


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


    public static ArrayList<String> analyzeMessage(JSONObject obj,ArrayList<String> keys){
        messageElements.clear();
        for(int k=0;k<keys.size();k++){
            try {
                messageElements.add(obj.getString(keys.get(k)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return messageElements;
    }
}
