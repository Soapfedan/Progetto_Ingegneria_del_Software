package application.sharedstorage;

/**
 * Created by Federico-PC on 20/03/2017.
 */

public class Positions extends SharedData {

    private int x;
    private int y;

    public Positions() {
        x=0;
        y=0;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public  void setX(int x) {
        this.x = x;
    }

    public  void setY(int y) {
        this.y = y;
    }
}
