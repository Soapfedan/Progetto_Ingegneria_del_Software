package application.maps.components;

/**
 * Classe che identifica una notifica
 */

public class Notify {

    private int id;
    private int cod_cat;
    private String floor;
    private String room;

    public Notify(){

    }

    public Notify(int id, int cod_cat, String floor, String room) {
        this.id = id;
        this.cod_cat = cod_cat;
        this.floor = floor;
        this.room = room;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCod_cat() {
        return cod_cat;
    }

    public void setCod_cat(int cod_cat) {
        this.cod_cat = cod_cat;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}
