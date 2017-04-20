package application.maps.components;

/**
 * Created by Niccolò on 28/12/2016.
 */

public class Room {

    private String cod;
    private int[] coords;
    private String floor;
    private double width;



    public Room(String cod, int[] crds, String floor, double width){

        this.cod = cod;

        this.coords = crds;
        this.floor = floor;
        this.width = width;
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

    public String getCod() {
        return cod;
    }
}
