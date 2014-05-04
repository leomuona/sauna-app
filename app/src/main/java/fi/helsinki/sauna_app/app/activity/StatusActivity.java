package fi.helsinki.sauna_app.app.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import fi.helsinki.sauna_app.app.R;
import fi.helsinki.sauna_app.app.model.SensorData;
import fi.helsinki.sauna_app.app.service.SensorService;

public class StatusActivity extends Activity {

    private static final int RESULT_SETTINGS = 1;

    private SensorDataReceiver receiver;
    private IntentFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        filter = new IntentFilter(SensorDataReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new SensorDataReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
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
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivityForResult(settingsIntent, RESULT_SETTINGS);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void measure(View view) {
        // Starts SensorService, response is handled by receiver.
        Intent sensorIntent = new Intent(this, SensorService.class);
        startService(sensorIntent);

    }

    public class SensorDataReceiver extends BroadcastReceiver {

        public static final String ACTION_RESP = "SENSOR_DATA_ACTION_RESP";

        @Override
        public void onReceive(Context context, Intent intent) {

            SensorData sData = (SensorData) intent.getSerializableExtra(SensorService.PARAM_OUT_DATA);
            if (sData == null) {
                String err_msg = intent.getStringExtra(SensorService.PARAM_ERROR_MSG);
                Toast.makeText(context, err_msg, Toast.LENGTH_SHORT).show();
                return;
            }

            // update view
            TextView tempValue = (TextView) findViewById(R.id.temperature);
            tempValue.setText(String.format(getString(R.string.temperature), sData.getTemperature()));

            TextView humidValue = (TextView) findViewById(R.id.humidity);
            humidValue.setText(String.format(getString(R.string.humidity), sData.getHumidity()));

            TextView coValue = (TextView) findViewById(R.id.co);
            coValue.setText(String.format(getString(R.string.co), sData.getCoData()));

            Toast.makeText(context, R.string.sensor_data_updated, Toast.LENGTH_SHORT).show();
        }
    }
}
