package fi.helsinki.sauna_app.app.model;

import java.io.Serializable;

/**
 * Data wrapper for SensorDrone measurement data.
 */
public class SensorData implements Serializable {

    private static final long serialVersionUID = 1L;

    private float temperature;
    private float humidity;
    private float coData;

    public SensorData(float temperature, float humidity, float coData) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.coData = coData;
    }

    public float getTemperature() {
        return this.temperature;
    }

    public float getHumidity() {
        return this.humidity;
    }

    public float getCoData() {
        return this.coData;
    }
}
