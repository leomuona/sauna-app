package fi.helsinki.sauna_app.app.model;

public class SensorData {
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
