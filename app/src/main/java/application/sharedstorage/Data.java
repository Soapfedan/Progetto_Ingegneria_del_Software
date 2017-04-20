package application.sharedstorage;

/**
 * Created by Federico-PC on 20/03/2017.
 */

public class Data {

    //Classe statica che conterrà tutte le strutture dati da cui dovrà accedere
    private static UserPositions userPosition = new UserPositions();


    public static UserPositions getUserPosition() {
        return userPosition;
    }
}
