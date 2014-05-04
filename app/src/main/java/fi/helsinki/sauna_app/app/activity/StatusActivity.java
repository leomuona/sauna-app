package fi.helsinki.sauna_app.app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.util.TypedValue;
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
        private static final float CO_LOW_LIMIT = 35.0f;
        private static final float CO_HIGH_LIMIT = 100.0f;

        @Override
        public void onReceive(Context context, Intent intent) {

            SensorData sData = (SensorData) intent.getSerializableExtra(SensorService.PARAM_OUT_DATA);
            if (sData == null) {
                String err_msg = intent.getStringExtra(SensorService.PARAM_ERROR_MSG);
                Toast.makeText(context, err_msg, Toast.LENGTH_SHORT).show();
                return;
            }

            // update view
            TextView temperatureView = (TextView) findViewById(R.id.temperature);
            temperatureView.setText(String.format(getString(R.string.temperature), sData.getTemperature()));

            TextView humidView = (TextView) findViewById(R.id.humidity);
            humidView.setText(String.format(getString(R.string.humidity), sData.getHumidity()));

            TextView coView = (TextView) findViewById(R.id.co);
            float coValue = sData.getCoData();
            coView.setText(String.format(getString(R.string.co), coValue));

            if (coValue >= CO_LOW_LIMIT) {
                createCoWarning(coView, coValue);
            }

            // TODO: restore the default text color for normal CO level

            Toast.makeText(context, R.string.sensor_data_updated, Toast.LENGTH_SHORT).show();
        }

        private void createCoWarning(TextView view, float coValue) {
            int textColor;
            int alertText;

            if (coValue >= CO_HIGH_LIMIT) {
                textColor = Color.RED;
                alertText = R.string.high_co_warning;
            } else {
                textColor = Color.YELLOW;
                alertText = R.string.co_warning;
            }

            view.setTextColor(textColor);

            new AlertDialog.Builder(StatusActivity.this)
                    .setMessage(getString(alertText))
                    .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
        }
    }
}
