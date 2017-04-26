package application.maps.components;

import android.widget.ImageView;

import java.util.HashMap;

/**
 * Created by Federico-PC on 05/12/2016.
 */

public class Node {

    private int[] coords;
    private String floor;
    private ImageView position;
    private HashMap<String,Emergency> emergencies;
    private boolean presence;

    public Node(){

    }

    public Node(int[] crds,String fl){

        coords = crds;
        floor = fl;
    }

    public boolean isPresence(){
       return presence;
    }

    public void setPresence(boolean a){
        this.presence = a;
    }

    public void setEmergencies(){
        //// TODO: 06/12/2016  setto tutte le emergenze
    }

    public int[] getCoords() {
        return coords;
    }

    public String getFloor() {
        return floor;
    }


}
