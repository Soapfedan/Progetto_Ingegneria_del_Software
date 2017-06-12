package application.maps.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Classe che definisce un piano che ha come proprietà un insieme di:
 * - stanze/aule (rooms)
 * - nodi/beacons (nodes)
 * Viene identificata da un nome
 */

public class Floor {

    private HashMap<String,Room> rooms;
    private HashMap<String,Node> nodes;
    private String floorName;

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


    // metodo che costruisce un arraylist di stringhe che contiente tutti i nomi delle aule
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

    // metodo che costruisce un arraylist di stringhe che contiente tutti i nomi dei beacon
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
