package fi.helsinki.sauna_app.app;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.sensorcon.sensordrone.android.Drone;

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
        Drone d = new Drone();
        d.btConnect("00:17:E9:50:E1:75");

        d.enableTemperature();
        d.measureTemperature();
        TextView tempValue = (TextView) findViewById(R.id.temperature);
        tempValue.setText("Temperature: " + d.temperature_Celsius + " °C");

        d.enableHumidity();
        d.measureHumidity();

        TextView humidValue = (TextView) findViewById(R.id.humidity);
        humidValue.setText("Humidity: " + d.humidity_Percent + " %");

        d.disconnect();
    }
}
