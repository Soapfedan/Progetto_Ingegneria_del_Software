package application.maps.components;

/**
 * Classe che identifica un beacon
 */

public class Node {

    private int[] coords;
    private String floor;

    public Node(){

    }

    public Node(int[] crds,String fl){

        coords = crds;
        floor = fl;
    }


    public int[] getCoords() {
        return coords;
    }

    public String getFloor() {
        return floor;
    }


}
