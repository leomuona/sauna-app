package fi.helsinki.sauna_app.app.service;

import com.sensorcon.sensordrone.android.Drone;

import fi.helsinki.sauna_app.app.MeasurementException;
import fi.helsinki.sauna_app.app.drone.SaunaDroneEventHandler;
import fi.helsinki.sauna_app.app.model.SensorData;

public class SensorService {

    private static SensorService instance;

    private String dMAC;
    private SensorData currentData;
    private long timeout;

    private SensorService() {
        // TODO: set MAC address to configuration file?
        dMAC = "00:17:E9:50:E1:75";
        timeout = 5;
    }

    public static SensorService getInstance() {
        if (instance == null) {
            instance = new SensorService();
        }
        return instance;
    }

    public SensorData measureData() throws MeasurementException {
        // TODO: move drone away from UI thread
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
