package application.maps;

/**
 * Created by Niccolò on 28/12/2016.
 */

public class Room {

    String cod;
    int[] coords = new int[2];

    public Room(String c) {
        this.cod = c;
    }

    public String getCod(){
        return cod;
    }



}
