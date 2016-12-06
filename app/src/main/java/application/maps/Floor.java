package application.maps;

import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Federico-PC on 05/12/2016.
 */

public class Floor {

    private ArrayList<Node> nodes;
    private String floorName;
    private ImageView mapName;
    private HashMap<String,Notify> notifications; // per ogni nodo ho un insieme di notifiche

    public Floor(){
        // TODO: 06/12/2016  carico tutti i nodi di un piano
    }

    public void addNode(Node n){

    }

    public void deleteNode(int idNode){

    }

    public void addNotification(Notify n){

    }

    public void deleteNotification(String n){

    }
}
