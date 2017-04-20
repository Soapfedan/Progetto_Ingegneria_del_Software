package application.sharedstorage;

import java.util.ArrayList;

/**
 * Created by Federico-PC on 20/03/2017.
 */

public class SharedData {

    protected ArrayList<DataListener> listeners = new ArrayList<DataListener>();

    public void addDataListener(DataListener listener) {
        listeners.add(listener);
    }

    protected void updateInformation(DataListener t) {
        for (DataListener dataListener : listeners) {
            if(!listeners.equals(t)){
                dataListener.retrive();
            }
        }

    }
}
