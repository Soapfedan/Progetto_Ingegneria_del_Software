package application.validation;

/**
 * Created by Niccolò on 24/01/2017.
 */

public abstract class FormControl {
        //lunghezza minima del numero di telefono
    private static final int phone_lenght = 8;
        //lunghezza minima della password
    private static final int pass_lenght = 8;
        //lunghezza stringa provincia
    private static final int provence_lenght = 2;

        //controlla che ci siano solamente lettere e spazi
    public static boolean letterControl(String s) {
        boolean b;
        String pattern= "^[a-zA-Z ]*$";
        if(s.matches(pattern)) b = true;
        else b = false;
        return b;
    }

    //controlla che ci siano solamente lettere, @ e punto
    public static boolean mailControl(String s) {
        boolean b;
        String pattern= "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        if(s.matches(pattern)) b = true;
        else b = false;
        return b;
    }

    //controlla che ci siano solamente numeri
    public static boolean numberControl(String s) {
        boolean b;
        String pattern= "^[0-9]*$";
        if(s.matches(pattern)) b = true;
        else b = false;
        return b;
    }

    public static boolean passwordControl(String s) {
        boolean b;
        if (s.length()<pass_lenght) b = false;
        else b = true;
        return b;
    }

    public static boolean provenceControl(String s) {
        boolean b;
        if (s.length()!=provence_lenght) b = false;
        else b = true;
        return b;
    }

    public static boolean phoneControl(String s) {
        boolean b;
        if (s.length()<phone_lenght) b = false;
        else b = true;
        return b;
    }
}
