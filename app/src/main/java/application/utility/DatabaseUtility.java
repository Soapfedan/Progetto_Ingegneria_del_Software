package application.utility;

import java.util.ArrayList;

import application.MainApplication;
import application.user.UserProfile;

/**
 * Created by Federico-PC on 20/01/2017.
 */

public class DatabaseUtility {

    public static void viewColumn(){

        ArrayList<UserProfile> users = MainApplication.getDB().open().getAllUsers();

        for(int i=0;i<users.size();i++){
            System.out.println("Utente: "+ i +
                                " email: "+users.get(i).getEmail()+
                                " nome: "+ users.get(i).getNome() +
                                " cognome: "+ users.get(i).getCognome());
        }

    }

    public static void deleteColumn(String email){

        System.out.print("Email cancellata: "+ email + "risultato: " + MainApplication.getDB().open().deleteProfile(email));

    }
}
