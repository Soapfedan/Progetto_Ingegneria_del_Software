package application.sharedstorage;

/**
 * Created by Federico-PC on 20/03/2017.
 */

public class Data {

    //Classe statica che conterrà tutte le strutture dati da cui dovrà accedere
    private static UserPositions userPosition = new UserPositions();
    private static Notification notification = new Notification();

    public static UserPositions getUserPosition() {
        return userPosition;
    }

    public static Notification getNotification() {
        return notification;
    }
}
