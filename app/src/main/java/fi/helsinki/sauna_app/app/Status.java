package fi.helsinki.sauna_app.app;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.sensorcon.sensordrone.android.Drone;

import fi.helsinki.sauna_app.app.model.SensorData;
import fi.helsinki.sauna_app.app.service.SensorService;

public class Status extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.status, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void measure(View view) {
        SensorService sService = SensorService.getInstance();
        SensorData sData = sService.measureData();

        TextView tempValue = (TextView) findViewById(R.id.temperature);
        tempValue.setText("Temperature: " + sData.getTemperature() + " °C");

        TextView humidValue = (TextView) findViewById(R.id.humidity);
        humidValue.setText("Humidity: " + sData.getHumidity() + " %");

    }
}
