package fi.helsinki.sauna_app.app.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.sensorcon.sensordrone.android.Drone;

import fi.helsinki.sauna_app.app.MeasurementException;
import fi.helsinki.sauna_app.app.R;
import fi.helsinki.sauna_app.app.Status;
import fi.helsinki.sauna_app.app.drone.SaunaDroneEventHandler;
import fi.helsinki.sauna_app.app.model.SensorData;

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
        dMAC = res.getString(R.string.drone_mac_address);
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
        broadcastIntent.setAction(Status.SensorDataReceiver.ACTION_RESP);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(PARAM_OUT_DATA, data);
        broadcastIntent.putExtra(PARAM_ERROR_MSG, err_msg);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }

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

    private void assertTrue(boolean val) throws AssertException {
        if (!val) {
            throw new AssertException("Value was not true.");
        }
    }

    private class AssertException extends Exception {
        public AssertException() { super(); }
        public AssertException(String message) { super(message); }
        public AssertException(String message, Throwable cause) { super(message, cause); }
        public AssertException(Throwable cause) { super(cause); }
    }

}
