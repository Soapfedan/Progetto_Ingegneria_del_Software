package application.maps;

import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Federico-PC on 05/12/2016.
 */

public class Floor {

    private ArrayList<Room> rooms;
    private ArrayList<Node> nodes;
    private String floorName;
    private ImageView mapName;
    private HashMap<String,Notify> notifications; // per ogni nodo ho un insieme di notifiche

    public Floor(){
        // TODO: 06/12/2016  carico tutti i nodi di un piano
    }

    public Floor(String name){
        floorName = name;
        rooms = new ArrayList<>();
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public void addNode(Node n){

    }

    public void deleteNode(int idNode){

    }

    public void addNotification(Notify n){

    }

    public void deleteNotification(String n){

    }

    public void addRoom(Room r) {
        rooms.add(r);
    }

    public void addRoom(String s) {
        rooms.add(new Room(s));
    }

    public ArrayList<String> nameStringRoom() {
        ArrayList<String> s = new ArrayList();
        for (Room f : rooms) {
            s.add(f.getName());
        }
        return s;
    }

    public String getName() {
        return floorName;
    }

}
