package fi.helsinki.sauna_app.app.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import com.sensorcon.sensordrone.android.Drone;

import fi.helsinki.sauna_app.app.MeasurementException;
import fi.helsinki.sauna_app.app.R;
import fi.helsinki.sauna_app.app.activity.SettingsActivity;
import fi.helsinki.sauna_app.app.activity.StatusActivity;
import fi.helsinki.sauna_app.app.drone.SaunaDroneEventHandler;
import fi.helsinki.sauna_app.app.model.SensorData;

/**
 * Service to handle SensorDrone measurements. Uses a worker thread.
 */
public class SensorService extends IntentService {
    public static final String PARAM_OUT_DATA = "OUTPUT_SENSOR_DATA";
    public static final String PARAM_ERROR_MSG = "OUTPUT_ERROR_MSG";

    private String dMAC;
    private long timeout;

    public SensorService() {
        super("SensorService");

    }

    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Resources res = getResources();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        dMAC = sharedPreferences.getString(SettingsActivity.KEY_PREF_DRONE_MAC_ADDRESS, "");
        timeout = res.getInteger(R.integer.sensor_timeout);

        SensorData data = null;
        String err_msg = "";
        try {
            data = measureData();
        } catch (MeasurementException me) {
            err_msg = me.getLocalizedMessage();
        }
        // broadcast result
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(StatusActivity.SensorDataReceiver.ACTION_RESP);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(PARAM_OUT_DATA, data);
        broadcastIntent.putExtra(PARAM_ERROR_MSG, err_msg);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }

    /**
     * Measure data with SensorDrone
     * @return SensorData
     * @throws MeasurementException
     */
    public SensorData measureData() throws MeasurementException {
        Drone drone = new Drone();
        SaunaDroneEventHandler handler = new SaunaDroneEventHandler(drone);
        drone.registerDroneListener(handler);

        try {
            assertTrue(drone.btConnect(dMAC));
            assertTrue(handler.waitForEvents(timeout));
        } catch (AssertException e) {
           throw new MeasurementException("Failed to measure data");
        } finally {
            drone.disconnect();
        }

        return new SensorData(handler.getTemperature(), handler.getHumidity(), handler.getCo());
    }

    /**
     * Our own assert function.
     * @param val that should be true
     * @throws AssertException
     */
    private void assertTrue(boolean val) throws AssertException {
        if (!val) {
            throw new AssertException("Value was not true.");
        }
    }

    /**
     * Our own AssertException.
     */
    private class AssertException extends Exception {
        public AssertException() { super(); }
        public AssertException(String message) { super(message); }
        public AssertException(String message, Throwable cause) { super(message, cause); }
        public AssertException(Throwable cause) { super(cause); }
    }

}
