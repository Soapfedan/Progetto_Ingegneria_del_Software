package com.core.progettoingegneriadelsoftware;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import application.MainApplication;
import application.comunication.ServerComunication;
import application.utility.CSVHandler;
import application.validation.FormControl;

/**
 * Created by Niccolo on 09/05/2017.
 */

public class WelcomeActivity extends AppCompatActivity {

        //versione dei file CSV a disposizione dell'applicazione
    private int version;
        //nomi dei file CSV
    private static final String BEACONLISTFILE = CSVHandler.getFileBeacon();
    private static final String ROOMLISTFILE = CSVHandler.getFileRoom();

        //stringa usata come chiave nello sharedpreferencies per l'indirizzo ip
    private static final String serverIp = "serverIp";
        //stringa usata come chiave nello sharedpreferencies per il numero della versione
    private static final String versionID = "versionID";
        //stringa usata come chiave nello sharedpreferencies per l'edificio
    private static final String buildingID = "buildingID";

        //elementi per l'interfaccia grafica
    private Spinner spinner;
    private EditText editText;
    private CheckBox checkBox;
        //elemento usato per salvare in maniera permanente sul dispositivo alcuni dati
    SharedPreferences prefer;
        //indirizzo ip del server a cui ci si vuole collegare
    private String ip;

    private boolean maps;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        MainApplication.setCurrentActivity(this);

        CSVHandler.createCSV(this);

        prefer = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        checkBox = (CheckBox) findViewById(R.id.chkOnline);

        spinner = (Spinner) findViewById(R.id.spinner);

        Button select = (Button) findViewById(R.id.button_map);

        TextView textView = (TextView) findViewById(R.id.textView_ip);

        editText = (EditText) findViewById(R.id.ip_text);
        editText.setText(prefer.getString(serverIp,""));

            //preso l'indirizzo ip salvato in memoria
        ip = prefer.getString(serverIp,"no ip");

            //flag che verifica se possibile accedere all'app o meno
        boolean b = controlAccess();

        if (b) access(b);
        else {
                //creato il bottone
            select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                boolean bb = false;

                if (checkBox.isChecked()) {
                    bb = controlAccess();
                }
                else {
                    ip = editText.getText().toString();
                        //controllo sull'indirizzo ip immesso
                    if(checkIp(ip)) {
                        String building = spinner.getSelectedItem().toString().toLowerCase();

                        boolean beaconFile = false;
                        boolean roomFile = false;

                        //scaricata la versione del server
                        int currentVersion = ServerComunication.checkVersion();

                        Log.e("current","current " + currentVersion);

                        if (currentVersion!=-1) {
                            ServerComunication.setHostMaster(ip);
                            MainApplication.setOnlineMode(true);
                            //scaricati i dati per aggiornare i file CSV
                            beaconFile = downloadCSV(building,BEACONLISTFILE);
                            roomFile = downloadCSV(building,ROOMLISTFILE);
                            //aggiornata la versione salvata sul dispositivo
                            version = currentVersion;
                        }

                            //controllo per poter accedere (si controlla la lunghezza dei documenti csv)
                        if(beaconFile && roomFile) {
                                //aggiornati i valori nelle preferencies
                            downloadSetup();
                            SharedPreferences.Editor edit = prefer.edit();
                            edit.putString(serverIp,ip);
                            edit.putInt(versionID,version);
                            edit.putString(buildingID,building);
                            edit.commit();
                            bb = true;
                        }
                        else {
                            Toast.makeText(getApplicationContext(), " Ci sono problemi con il download delle mappe ", Toast.LENGTH_SHORT).show();
                            bb = false;
                        }
                    }
                }
                access(bb);
                }
            });
        }



    }

    protected void onResume() {
        super.onResume();
        MainApplication.setVisible(true);
    }

    protected void onPause() {
        super.onPause();
        MainApplication.setVisible(false);
    }

    /**
     * Metodo per controllare se il CSV è vuoto o meno
     * @param f, file CSV da analizzare
     * @return valore boolean (true se CSV contiene valori, false se vuoto)
     */
    private boolean csvContainsElements(File f) {
        boolean b = false;
        if (f.length()>0) b=true;
        Log.e("csv","bool" + b);
        return b;
    }

    /**
     * Metodo per controllare se l'indirizzo ip contenuto
     * @param ip, indirizzo ip da controllare
     * @return valore boolean (true se CSV contiene valori, false se vuoto)
     */
    private boolean checkIp(String ip) {
        boolean b;
            //flag per il controllo formale di come è scritto l'ip
        boolean check;
            //flag per per controllare se il server sia disponibile
        boolean handshake = false;
        check = FormControl.ipControl(ip);
        if (check) {
                //contattato il server per verificare la disponibilità
            handshake = ServerComunication.handShake(ip);
            if (!handshake) Toast.makeText(getApplicationContext(), " La comunicazione col server è fallita ", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(), " L'ip è scritto male ", Toast.LENGTH_SHORT).show();
        }

            //se entrambi veri ci si può collegare al server
        b = (check && handshake);
        if (b) {
                //aggiornati i dati relativi all'ip
            SharedPreferences.Editor edit = prefer.edit();
            edit.putString(serverIp,ip);
            edit.commit();
            ServerComunication.setHostMaster(ip);
        }

        return b;
    }

    /**
     * Metodo per controllare se la versione salvata dei CSV è corretta
     * @return valore boolean (true se versione corretta, false altrimenti)
     */
    private boolean checkVersion() {
            //versione dei file salvata in memoria
        version = prefer.getInt(versionID,0);
            //chiesta al server versione corrente dei file
        int currentVersion = ServerComunication.checkVersion();

        Log.i("version","version " + version + " current " + currentVersion);
            //confronto tra le due versioni
        boolean b = (version==currentVersion);
        return b;
    }

    private void downloadSetup () {
        HashMap<String,String> s;
        HashMap<String,Long> setup;
        try {
            s = ServerComunication.getScanParameters();
            if (!s.isEmpty() && s!=null) {
                setup = createParameters(s);
                MainApplication.setScanParameters(setup);
            }
            else {
                MainApplication.setScanParameters(null);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private HashMap<String,Long> createParameters(HashMap<String,String> s) {
        HashMap<String,Long> setup = new HashMap<>();
        for (String str: s.keySet()) {
            if (!s.get(str).equals("")) {
                Long l = Long.parseLong(s.get(str));
                setup.put(str,l);
            }
        }
        return setup;
    }

    /**
     * Metodo per accedere all'applicazione vera e propria
     * @param b, booleano in base al quale si passa o meno all'applicazione vera e propria
     */
    private void access(boolean b) {
        if (b) {
            changeActivity();
            if(checkBox.isChecked()){
                MainApplication.setOnlineMode(false);
            }else {
                MainApplication.setOnlineMode(true);
            }
        }
        else {
            if(maps) {
                maps=false;
            }else{
                Toast.makeText(getApplicationContext(), "Accesso non riuscito, controllare l'ip", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Metodo per controllare se l'indirizzo ip contenuto
     * @return valore boolean (true se possibile passare ad app, false altrimenti)
     */
    private boolean controlAccess() {
        boolean b = false;
        if (checkBox.isChecked()) {
            if(csvContainsElements(CSVHandler.getFiles().get(BEACONLISTFILE)) && csvContainsElements(CSVHandler.getFiles().get(ROOMLISTFILE))) b = true;
            else {
                Toast.makeText(getApplicationContext(), "Collegarsi ad un server per scaricare le mappe", Toast.LENGTH_SHORT).show();
                maps=true;
            }
            MainApplication.setOnlineMode(false);
        }
        else {
                //controllo su ip salvato
            if(ip.equals("no ip")) {
                b = false;
            }
            else {
                ServerComunication.setHostMaster(ip);
                    //controlla che server sia raggiungibile
                if (ServerComunication.handShake(ip)) {
                    if (checkVersion()) {
                        b = true;
                        downloadSetup();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), " La versione del file CSV non è aggiornata, la sto richiedendo", Toast.LENGTH_SHORT).show();
                        String building = prefer.getString(buildingID,"");

                        boolean beaconFile = false;
                        boolean roomFile = false;

                        int currentVersion = ServerComunication.checkVersion();

                        if(currentVersion!=-1) {
                            //scarica i file aggiornati
                            beaconFile = downloadCSV(building,BEACONLISTFILE);
                            roomFile = downloadCSV(building,ROOMLISTFILE);
                            //aggiorna la versione in memoria

                            MainApplication.setOnlineMode(true);
                        }

                        version = currentVersion;

                        if(beaconFile && roomFile) {
                            if(beaconFile && roomFile) {
                                SharedPreferences.Editor edit = prefer.edit();
                                edit.putInt(versionID,version);
                                edit.putString(buildingID,building);
                                edit.commit();
                                Toast.makeText(getApplicationContext(), " Ho aggiornato il CSV, riclicca il bottone", Toast.LENGTH_SHORT).show();
                                b = false;
                            }

                        }
                        else {
                            b = false;
                        }
                    }
                }
                else {
                    b = false;
                    Toast.makeText(getApplicationContext(), " l'indirizzo ip non è corretto", Toast.LENGTH_SHORT).show();
                }
            }
        }
        return b;
    }

    /**
     * Metodo scaricare un file CSV dal server
     * @param building, edificio del quale si vuole scaricare un file
     * @param fileName, nome del file che si vuole scaricare
     * @return valore boolean per indicare se il file è stato scaricato o meno
     */
    private boolean downloadCSV(String building, String fileName) {
        boolean b = true;
            //struttura dati che deve contenere i dati
        HashMap<String,String>[] ma = null;
        try {
            if (fileName.equals(BEACONLISTFILE)) {
                ma = ServerComunication.getBuildingBeacon(building);
            }
            else if (fileName.equals(ROOMLISTFILE)) {
                ma = ServerComunication.getBuildingRoom(building);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
            b = false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            b = false;
        } catch (JSONException e) {
            e.printStackTrace();
            b = false;
        }

            //una volta scaricato il file viene aggiornato il CSV
        try {
            CSVHandler.updateCSV(ma,getBaseContext(),fileName);
        } catch (IOException e) {
            e.printStackTrace();
            b = false;
        } catch (NullPointerException e) {
            Log.e("CSV Error","Update CSV Error");
            b = false;
        } catch (Exception e) {
            b = false;
        }
            //verifica che il download sia andato a buon fine
        if (b) b = csvContainsElements(CSVHandler.getFiles().get(fileName));

        return b;
    }

    /**
     * Metodo per accedere all'applicazione vera e propria
     */
    private void changeActivity() {
        startActivity(new Intent(getBaseContext(),Home.class));
        this.finish();
    }
}
