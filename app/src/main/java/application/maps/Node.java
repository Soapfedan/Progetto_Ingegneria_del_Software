package application.maps;

import android.widget.ImageView;

import java.util.HashMap;

/**
 * Created by Federico-PC on 05/12/2016.
 */

public class Node {

    private int[] coords;
    private String floor;
    private double width;
    private ImageView position;
    private HashMap<String,Emergency> emergencies;
    private boolean presence;

    public Node(){

    }

    public Node(int[] crds,String floor,double width){

             this.coords = crds;
        this.floor = floor;
        this.width = width;
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

    public double getWidth() {
        return width;
    }
}
