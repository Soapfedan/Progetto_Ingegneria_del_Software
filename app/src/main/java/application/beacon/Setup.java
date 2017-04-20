package application.beacon;
import android.util.Log;

/**
 * Created by Niccolo on 06/04/2017.
 */

public class Setup {
    public static final String normalCondition = "NORMAL";
    public static final String emergencyCondition = "EMERGENCY";
    public static final String searchCondition = "SEARCHING";

    private static final long scanPeriodNormal = 3000l;
    private static final long scanPeriodSearching = 1500l;
    private static final long scanPeriodEmergency = 1000l;

    private static final long periodBetweenScanNormal = 15000l;
    private static final long periodBetweenScanSearching = 5000l;
    private static final long periodBetweenScanEmergency = 3000l;

    private String state;
    private long SCAN_PERIOD;
    private long STOP_PERIOD;
    private boolean analyzeBeacon;

    public Setup() {
        state = normalCondition;
        SCAN_PERIOD = scanPeriodNormal;
        STOP_PERIOD = periodBetweenScanNormal;
        analyzeBeacon = true;
    }

    public Setup(String condition) {
        switch (condition) {
            case ("NORMAL"):
                state = normalCondition;
                SCAN_PERIOD = scanPeriodNormal;
                STOP_PERIOD = periodBetweenScanNormal;
                analyzeBeacon = true;
                break;
            case ("EMERGENCY"):
                state = emergencyCondition;
                SCAN_PERIOD = scanPeriodEmergency;
                STOP_PERIOD = periodBetweenScanEmergency;
                analyzeBeacon = false;
                break;
            case ("SEARCHING"):
                state = searchCondition;
                SCAN_PERIOD = scanPeriodSearching;
                STOP_PERIOD = periodBetweenScanSearching;
                analyzeBeacon = false;
                break;
            default:
                Log.e("ERROR", "stato sbagliato");
                break;
        }
    }

    public long getScanPeriod() {
        return SCAN_PERIOD;
    }

    public long getPeriodBetweenScan() {
        return STOP_PERIOD;
    }

    public String getState() {
        return state;
    }

    public boolean mustAnalyze() {
        return analyzeBeacon;
    }

    public void setCondition(String condition) {
        switch (condition) {
            case ("NORMAL"):
                state = normalCondition;
                SCAN_PERIOD = scanPeriodNormal;
                STOP_PERIOD = periodBetweenScanNormal;
                analyzeBeacon = true;
                break;
            case ("EMERGENCY"):
                state = emergencyCondition;
                SCAN_PERIOD = scanPeriodEmergency;
                STOP_PERIOD = periodBetweenScanEmergency;
                analyzeBeacon = false;
                break;
            case ("SEARCHING"):
                state = searchCondition;
                SCAN_PERIOD = scanPeriodSearching;
                STOP_PERIOD = periodBetweenScanSearching;
                analyzeBeacon = false;
                break;
            default:
                Log.e("ERROR", "stato sbagliato");
                break;
        }
    }
}