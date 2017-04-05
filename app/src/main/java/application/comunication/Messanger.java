package application.comunication;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;

import application.comunication.message.MessageAnalyzer;

/**
 * Created by Federico-PC on 23/03/2017.
 */

public class Messanger {

    //JSON Token which define the message type
    private static final String MESSAGE_TYPE = "Message_Type";


    //message types and sets of JSON tokens


    private static final String WELCOME_MSG = "Welcome";
        //tokens
        private static final String WELCOME_BODY = "Welcome_Body";

}
