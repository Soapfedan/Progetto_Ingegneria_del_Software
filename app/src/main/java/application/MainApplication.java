package application;

import java.util.ArrayList;
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


    private ArrayList<Floor> edi;

    /**
     * Method used t
     * @param n
     */
    public void researchNode(Node n){

    }

    public static void start() {
        UserHandler.init();
    }

    public void loadMap(String tipe){
        // TODO: 06/12/2016  devo caricare la mappa desiderata dal db
    }

    public void activateBluetooth(){

    }
}
