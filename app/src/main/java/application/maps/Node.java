package application.maps;

import android.widget.ImageView;

import java.util.HashMap;

/**
 * Created by Federico-PC on 05/12/2016.
 */

public class Node {

    private int[] coords;
    private int id;
    private ImageView position;
    private HashMap<String,Emergency> emergencies;
    private boolean presence;



    public Node(int i,int[] crds){

        this.id = i;
        this.coords = crds;
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

}
