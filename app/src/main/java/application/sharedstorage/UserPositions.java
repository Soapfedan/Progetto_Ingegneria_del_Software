package application.sharedstorage;

/**
 * Created by Federico-PC on 20/03/2017.
 */

public class UserPositions extends SharedData {

    private int x;
    private int y;
    private String floor;

    public UserPositions() {
        x=0;
        y=0;
    }

    public void setFloor(String f) {
        floor = f;
    }

    public String getFloor() {
        return floor;
    }

    public int[] getPosition() {
        int[] pos = {this.x,
                     this.y};
        return pos;
    }

    public void setPosition(int[] pos) {
        this.x = pos[0];
        this.y = pos[1];
//        updateInformation();
    }
}
