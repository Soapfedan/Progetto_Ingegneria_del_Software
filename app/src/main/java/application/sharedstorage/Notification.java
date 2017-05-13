package application.sharedstorage;


import java.util.ArrayList;

import application.maps.components.Notify;

/**
 * Created by Federico-PC on 21/03/2017.
 */

public class Notification extends SharedData{

    private ArrayList<Notify> notifies;

    public Notification() {
       notifies = new ArrayList<>();
    }

    public ArrayList<Notify> getNotifies() {
        return notifies;
    }

    public void setNotifies(ArrayList<Notify> notifies) {
        this.notifies = notifies;
        updateInformation();
    }
}
