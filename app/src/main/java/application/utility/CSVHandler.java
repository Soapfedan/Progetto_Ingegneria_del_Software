package application.utility;

import android.content.Context;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Questa classe è stata creata per la gestione dei documenti CSV
 */

public class CSVHandler {

        //insieme dei file: ogni elemento dell'hashmap è un file (chiave: nome del file, valore:istanza del file)
    private static HashMap<String,File> files;

        //nome del file contenente i beacon
    public static final String fileBeacon = "beaconlist";
        //nome del file contenente le stanze
    public static final String fileRoom = "roomlist";

    /**
     * Metodo che crea i due file CSV, che contengono rispettivamente la lista dei beacon e delle stanze
     * @param context context dell'applicazione, necessario per accedere alle sue risorse durante la creazione dei file
     */
    public static void createCSV(Context context) {
        files = new HashMap<>();
        files.put(fileBeacon,new File(context.getFilesDir(), fileBeacon+".csv"));
        files.put(fileRoom,new File(context.getFilesDir(), fileRoom+".csv"));
    }

    /**
     * Metodo che restituisce l'istanza dell'HashMap contenente i files
     */
    public static HashMap<String,File> getFiles() {
        return files;
    }

    /**
     * Metodo che restituisce il nome del file contenente i beacon
     * @return nome del file contenente i beacon
     */
    public static String getFileBeacon() {
        return fileBeacon;
    }

    /**
     * Metodo che restituisce il nome del file contenente le stanze
     * @return nome del file contenente le stanze
     */
    public static String getFileRoom() {
        return fileRoom;
    }

    /**
     * Metodo che aggiorna il contenuto di un file CSV
     * @param lists, insieme degli elementi da scrivere nelle righe dei file CSV:
     *       l'hashmap rappresenta il singolo elemento (una riga del file)
     * @param context, context dell'applicazione, necessario per accedere alle sue risorse durante la modifica dei file.
     * @param fileName, indica il nome del file (con il quale è stato salvato nell'attributo files) che si vuole modificare.
     */
    public static void updateCSV(HashMap<String,String>[] lists, Context context, String fileName) throws IOException {
            //creato lo stream per scrivere sul file
        FileOutputStream outputStreamWriter = null;
        try {
                //si assegna allo stream il file su cui scrivere, in MODE_PRIVATE, in modo che il file sia scritto solo da questa applicazione
            outputStreamWriter = context.openFileOutput(fileName+".csv", context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
            //in base a che tipo di file si vuole scrivere, vanno impostate chiavi diverse per l'hashmap

            //caso in cui si voglia scrivere il file dei beacon
        if(fileName.equals(fileBeacon)) {
                //per ogni elemento dell'array viene scritta una riga (ogni elemento dell'hashmap viene scritto consecutivamente
                //separato solamente da un punto e virgola). Nel caso dei beacon una riga ha la seguente struttura:
                //"codice_address del beacon";"piano in cui si trova il beacon";"coordinata x sulla mappa";"coordinata y sulla mappa";
            for (int i=0; i<lists.length; i++) {
                outputStreamWriter.write(((lists[i].get("beacon_ID")+";").getBytes()));
                outputStreamWriter.write(((lists[i].get("floor")+";").getBytes()));
                outputStreamWriter.write(((lists[i].get("x")+";").getBytes()));
                outputStreamWriter.write(((lists[i].get("y")+";").getBytes()));
                    //bisogna andare a capo nel file, in quanto il termine dell'hashmap del file implica il termine della riga
                outputStreamWriter.write('\n');
            }
        }

            //caso in cui si voglia scrivere il file delle stanze
        else if(fileName.equals(fileRoom)) {
                //per ogni elemento dell'array viene scritta una riga (ogni elemento dell'hashmap viene scritto consecutivamente
                //separato solamente da un punto e virgola). Nel caso delle stanze una riga ha la seguente struttura:
                //"coordinata x sulla mappa";"coordinata y sulla mappa";"piano in cui si trova"; "altezza della stanza"; "codice della stanza"
            for (int i=0; i<lists.length; i++) {
                outputStreamWriter.write(((lists[i].get("x")+";").getBytes()));
                outputStreamWriter.write(((lists[i].get("y")+";").getBytes()));
                outputStreamWriter.write(((lists[i].get("floor")+";").getBytes()));
                outputStreamWriter.write(((lists[i].get("width")+";").getBytes()));
                outputStreamWriter.write(((lists[i].get("room")+";").getBytes()));
                    //bisogna andare a capo nel file, in quanto il termine dell'hashmap del file implica il termine della riga
                outputStreamWriter.write('\n');
            }
        }
            //terminate le operazioni di scrittura, viene chiuso lo stream
        outputStreamWriter.close();

    }

    public static void printCSV(ArrayList<String[]> s) {
        for (String[] str: s) {
            for (int i=0; i<str.length; i++) {
                Log.i("vet",str[i]);
            }
        }
    }

    /**
     * Metodo per leggere il contenuto di un file CSV
     * @param fileName, indica il nome del file (con il quale è stato salvato nell'attributo files) che si vuole leggere.
     * @param context, context dell'applicazione, necessario per accedere alle sue risorse durante la modifica dei file.
     * @return ArraList<String[]>, insieme degli elementi contenuti nel file: ogni array di String contiene il singolo elemento
     *      di una riga del CSV (separati dal ";"), l'arrayList ne rappresenta l'insieme
     */
    public static ArrayList<String[]> readCSV(String fileName, Context context) {
            //creato lo stream per leggere il file
        FileInputStream inputStream = null;
        try {
            //si assegna allo stream il file da leggere
            inputStream = context.openFileInput(fileName.concat(".csv"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        ArrayList<String[]> resultList = new ArrayList<>();
            //inizia la lettura effettiva del file
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine;
                //per ogni riga del documento viene preso ogni elemenento che la compone
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(";");
                resultList.add(row);

            }
        }
        catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: "+ex);
        }
        finally {
            try {
                inputStream.close();
            }
            catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: "+e);
            }
        }
        return resultList;
    }
}
