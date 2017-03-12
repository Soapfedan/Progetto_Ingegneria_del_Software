package application.maps;

import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Federico-PC on 05/12/2016.
 */

public class Floor {

    private HashMap<String,Room> rooms;
    private HashMap<String,Node> nodes;
    private String floorName;
    private ImageView mapName;
    private HashMap<String,Notify> notifications; // per ogni nodo ho un insieme di notifiche

    public Floor(String s){
        floorName = s;
        rooms = new HashMap<>();
        nodes = new HashMap<>();
    }

    public HashMap<String,Room> getRooms() {
        return rooms;
    }

    public HashMap<String, Node> getNodes() {
        return nodes;
    }

    public void addNode(String cod, Node n){
        nodes.put(cod,n);
    }

    public void deleteNode(int idNode){

    }

    public void addNotification(Notify n){

    }

    public void deleteNotification(String n){

    }

    public void addRoom(String name,Room r) {
        rooms.put(name,r);
    }



    public ArrayList<String> nameStringRoom() {
        ArrayList<String> s = new ArrayList();
        Iterator it = rooms.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            s.add(pair.getKey().toString());
           // it.remove(); // avoids a ConcurrentModificationException
        }
        return s;
    }

    public ArrayList<String> nameStringNode() {
        ArrayList<String> s = new ArrayList();
        Iterator it = nodes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            s.add(pair.getKey().toString());
           // it.remove(); // avoids a ConcurrentModificationException
        }
        return s;
    }

    public String getFloorName() {
        return floorName;
    }
}
