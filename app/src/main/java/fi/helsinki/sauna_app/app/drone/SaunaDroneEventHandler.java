package fi.helsinki.sauna_app.app.drone;

import com.sensorcon.sensordrone.DroneEventHandler;
import com.sensorcon.sensordrone.DroneEventObject;
import com.sensorcon.sensordrone.android.Drone;

/**
 * Handles SensorDrone events. This is the correct way to do it, I think.
 */
public class SaunaDroneEventHandler implements DroneEventHandler {
    @Override
    public void parseEvent(DroneEventObject deo) {
        if (deo.matches(DroneEventObject.droneEventType.CONNECTED)) {
            // TODO: call measure battery
        }
        if (deo.matches(DroneEventObject.droneEventType.BATTERY_VOLTAGE_MEASURED)) {
            // TODO: call measure temperature
        }
        if (deo.matches(DroneEventObject.droneEventType.TEMPERATURE_MEASURED)) {
            // TODO: call measure humidity
        }
        if (deo.matches(DroneEventObject.droneEventType.HUMIDITY_MEASURED)) {
            // TODO: call measure carbonmonoxide
        }
        if (deo.matches(DroneEventObject.droneEventType.PRECISION_GAS_MEASURED)) {
            // TODO
        }
    }
}
