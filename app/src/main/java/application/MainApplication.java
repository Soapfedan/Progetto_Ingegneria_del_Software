package application;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;

import application.database.UserAdapter;
import application.maps.Node;
import application.maps.Floor;
import application.user.UserHandler;

/**
 * Created by Federico-PC on 05/12/2016.
 */

public class MainApplication {

    /**
     * array dinamico che contiene tutti i piani di una mappa scaricata
     */
    // TODO: 06/12/2016  in teoria io potrei switchare tra più mappe es. medicinia o ingengeria ...
    // devo prevedere come gestire le diverse mappe scaricate, tenendone in memoria solo una???
    // lasciando le altre sul db??


    private static HashMap<String,Floor> floors;
    private static UserAdapter db;



    /**
     * Method used t
     * @param n
     */
    public void researchNode(Node n){

    }

    public static UserAdapter getDB () {
        return db;
    }

    public static void start(Context context) {
        UserHandler.init();
        //crea il db, ma ancora non è ne leggibile ne scrivibile
        db = new UserAdapter(context);

    }

    public void loadMap(String tipe){
        // TODO: 06/12/2016  devo caricare la mappa desiderata dal db
    }

    public void activateBluetooth(){

    }

    public static void setFloors(HashMap<String,Floor> f){
        floors = f;
    }

    public static HashMap<String,Floor> getFloors(){
        return floors;
    }

}

