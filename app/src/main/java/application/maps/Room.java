package application.maps;

/**
 * Created by Niccolò on 28/12/2016.
 */

public class Room {
    private String roomName;

    public Room() {}

    public Room(String name) {
        roomName = name;
    }

    public void setName(String name) {
        roomName = name;
    }

    public String getName() {
        return roomName;
    }

}
