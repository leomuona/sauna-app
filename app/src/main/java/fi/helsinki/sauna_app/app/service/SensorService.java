package fi.helsinki.sauna_app.app.service;

import com.sensorcon.sensordrone.android.Drone;

import fi.helsinki.sauna_app.app.model.SensorData;

public class SensorService {

    private static SensorService instance;

    private String dMAC;
    private Drone drone;

    private SensorData currentData;

    private SensorService() {
        drone = new Drone();
        // TODO: set MAC address to configuration file?
        dMAC = "00:17:E9:50:E1:75";
    }

    public static SensorService getInstance() {
        if (instance == null) {
            instance = new SensorService();
        }
        return instance;
    }

    public SensorData measureData() {
        // TODO: START USING SaunaDroneEventHandler
        float temp = 0f, humid = 0f;
        float co = 0;
        try {
            assertTrue(drone.btConnect(dMAC));

            assertTrue(drone.enableTemperature());
            assertTrue(drone.enableHumidity());
            assertTrue(drone.enablePrecisionGas());

            assertTrue(drone.measureTemperature());
            assertTrue(drone.measureHumidity());
            assertTrue(drone.measurePrecisionGas());

            temp = drone.temperature_Celsius;
            humid = drone.humidity_Percent;
            co = drone.precisionGas_ppmCarbonMonoxide;
        } catch (AssertException ae) {
            // TODO: Error message
            System.out.println("ERROR: SensorService.measureData(): Unable to measure drone data.");
        }
        drone.disconnect();
        return new SensorData(temp, humid, co);
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
