package application.sharedstorage;

/**
 * Created by Federico-PC on 20/03/2017.
 */

public class DataContainer {

    //Classe statica che conterrà tutte le strutture dati da cui dovrà accedere
    private static Positions userPosition = new Positions();


    public static Positions getUserPosition() {
        return userPosition;
    }
}
