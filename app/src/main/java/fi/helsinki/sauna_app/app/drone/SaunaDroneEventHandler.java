package fi.helsinki.sauna_app.app.drone;

import com.sensorcon.sensordrone.DroneEventHandler;
import com.sensorcon.sensordrone.DroneEventObject;
import com.sensorcon.sensordrone.android.Drone;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Handles SensorDrone events. This is the correct way to do it, I think.
 */
public class SaunaDroneEventHandler implements DroneEventHandler {
    private Drone drone;
    private float temperature;
    private float humidity;
    private float co;
    private CountDownLatch latch;

    public SaunaDroneEventHandler(Drone drone) {
        this.drone = drone;
        latch = new CountDownLatch(1);
    }

    @Override
    public void parseEvent(DroneEventObject deo) {
        if (deo.matches(DroneEventObject.droneEventType.CONNECTED)) {
            drone.enableTemperature();
        }
        if (deo.matches(DroneEventObject.droneEventType.TEMPERATURE_ENABLED)) {
            drone.measureTemperature();
        }
        if (deo.matches(DroneEventObject.droneEventType.TEMPERATURE_MEASURED)) {
            temperature = drone.temperature_Celsius;
            drone.enableHumidity();
        }
        if (deo.matches(DroneEventObject.droneEventType.HUMIDITY_ENABLED)) {
            drone.measureHumidity();
        }
        if (deo.matches(DroneEventObject.droneEventType.HUMIDITY_MEASURED)) {
            humidity = drone.humidity_Percent;
            drone.enablePrecisionGas();
        }
        if (deo.matches(DroneEventObject.droneEventType.PRECISION_GAS_ENABLED)) {
            drone.measurePrecisionGas();
        }
        if (deo.matches(DroneEventObject.droneEventType.PRECISION_GAS_MEASURED)) {
            co = drone.precisionGas_ppmCarbonMonoxide;
            // all measurements done, free latch
            latch.countDown();
        }
    }

    public boolean waitForEvents(long timeout) {
        try {
            return latch.await(timeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return false;
        }
    }

    public float getCo() {
        return co;
    }

    public float getTemperature() {
        return temperature;
    }

    public float getHumidity() {
        return humidity;
    }
}
