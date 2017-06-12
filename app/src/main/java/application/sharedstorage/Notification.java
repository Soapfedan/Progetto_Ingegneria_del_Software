package application.sharedstorage;


import java.util.ArrayList;
import application.maps.components.Notify;

/**
 * Classe che estende SharedData che contiene le diverse notifiche.
 * All'atto della setPosition viene richiamata la updateInformation che richiama la serie di retrive.
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
            //Vengono richiamate tutte le diverse retrive dei subscriber
        updateInformation();
    }
}
