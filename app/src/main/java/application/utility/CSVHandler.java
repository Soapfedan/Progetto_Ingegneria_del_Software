package application.utility;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

import application.maps.MapLoader;

/**
 * Created by Niccolo on 09/05/2017.
 */

public class CSVHandler {

    private static HashMap<String,File> files;
    private FileOutputStream outputStream;

    private static final String fileBeacon = "beaconlist";
    private static final String fileRoom = "roomlist";


    public static void createCSV(Context context) {
        files = new HashMap<>();
        files.put(fileBeacon,new File(context.getFilesDir(), fileBeacon+".csv"));
        files.put(fileRoom,new File(context.getFilesDir(), fileRoom+".csv"));
    }

    public static HashMap<String,File> getFiles() {
        return files;
    }

    public static String getFileBeacon() {
        return fileBeacon;
    }

    public static String getFileRoom() {
        return fileRoom;
    }

    public static void updateCSV(HashMap<String,String>[] lists, Context context, String fileName) throws IOException {

        FileOutputStream outputStreamWriter = null;
        try {
            outputStreamWriter = context.openFileOutput(fileName+".csv", context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if(fileName.equals(fileBeacon)) {
            for (int i=0; i<lists.length; i++) {
                outputStreamWriter.write(((lists[i].get("beacon_ID")+";").getBytes()));
                outputStreamWriter.write(((lists[i].get("floor")+";").getBytes()));
                outputStreamWriter.write(((lists[i].get("x")+";").getBytes()));
                outputStreamWriter.write(((lists[i].get("y")+";").getBytes()));
                outputStreamWriter.write('\n');
            }
        }
        else if(fileName.equals(fileRoom)) {
            for (int i=0; i<lists.length; i++) {
                outputStreamWriter.write(((lists[i].get("x")+";").getBytes()));
                outputStreamWriter.write(((lists[i].get("y")+";").getBytes()));
                outputStreamWriter.write(((lists[i].get("floor")+";").getBytes()));
                outputStreamWriter.write(((lists[i].get("width")+";").getBytes()));
                outputStreamWriter.write(((lists[i].get("room")+";").getBytes()));
                outputStreamWriter.write('\n');
            }
        }

        outputStreamWriter.close();

    }

    public static void printCSV(ArrayList<String[]> s) {
        for (String[] str: s) {
            for (int i=0; i<str.length; i++) {
                Log.i("vet",str[i]);
            }
        }
    }


    public static ArrayList<String[]> readCSV(String fileName, Context context) {

        FileInputStream inputStream = null;
        try {
            inputStream = context.openFileInput(fileName.concat(".csv"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        ArrayList<String[]> resultList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine;
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
