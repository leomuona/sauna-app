package fi.helsinki.sauna_app.app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import fi.helsinki.sauna_app.app.R;
import fi.helsinki.sauna_app.app.Result;
import fi.helsinki.sauna_app.app.model.SensorData;
import fi.helsinki.sauna_app.app.service.SensorService;

/**
 * Applications main/initial activity. Indicates sauna's state.
 */
public class StatusActivity extends Activity {

    private SensorDataReceiver receiver = null;
    private IntentFilter filter = null;

    private SensorData sensorData = null;

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
        updateFeelViews();
    }

    @Override
    protected void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_status:
                // do nothing, we are already here.
                return true;
            case R.id.action_user_profile:
                Intent upIntent = new Intent(this, UserProfileActivity.class);
                startActivityForResult(upIntent, Result.RESULT_USER_PROFILE);
                return true;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivityForResult(settingsIntent, Result.RESULT_SETTINGS);
                return true;
            case R.id.action_quit:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case Result.RESULT_QUIT:
                finish();
                break;
            default:
                // do nothing.
        }
    }

    /**
     * Measure new values from SensorDrone. Uses SensorService class.
     * @param view
     */
    public void measure(View view) {
        // Starts SensorService, response is handled by receiver.
        Intent sensorIntent = new Intent(this, SensorService.class);
        startService(sensorIntent);

    }

    /**
     * Update view to indicate how temperature and humidity refer's to user's liking.
     */
    private void updateFeelViews() {
        if (sensorData == null) {
            return;
        }

        // temperature
        TextView tempFeelView = (TextView) findViewById(R.id.temperature_feel);
        int t = compareProfileTemperatureDelta(sensorData.getTemperature());
        if (t < 0) {
            tempFeelView.setTextColor(Color.YELLOW);
            tempFeelView.setText(R.string.cold);
        } else if (t > 0) {
            tempFeelView.setTextColor(Color.YELLOW);
            tempFeelView.setText(R.string.hot);
        } else {
            tempFeelView.setTextColor(Color.GREEN);
            tempFeelView.setText(R.string.good);
        }

        // humidity
        TextView humiFeelView = (TextView) findViewById(R.id.humidity_feel);
        int h = compareProfileHumidityDelta(sensorData.getHumidity());
        if (h < 0) {
            humiFeelView.setTextColor(Color.YELLOW);
            humiFeelView.setText(R.string.dry);
        } else if (h > 0) {
            humiFeelView.setTextColor(Color.YELLOW);
            humiFeelView.setText(R.string.humid);
        } else {
            humiFeelView.setTextColor(Color.GREEN);
            humiFeelView.setText(R.string.good);
        }

    }

    /**
     * Compare temperature with profile's favourite temperature +- delta
     * @param temperature
     * @return -1 if too cold, 0 if within delta, 1 if too hot
     */
    private int compareProfileTemperatureDelta(float temperature) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        float defTemp = getResources().getInteger(R.integer.favourite_temperature_default_value);
        float favTemp = sharedPref.getFloat(getString(R.string.pref_user_temperature_key), defTemp);
        float tempDelta = (float) getResources().getInteger(R.integer.temperature_liking_max_delta);

        if (favTemp + tempDelta < temperature) {
            return 1;
        } else if (favTemp - tempDelta > temperature) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * Compare humidity with profiles favourable humidity +- delta
     * @param humidity
     * @return -1 if too dry, 0 if within delta, 1 if too humid
     */
    private int compareProfileHumidityDelta(float humidity) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        float defHumi = getResources().getInteger(R.integer.favourite_humidity_default_value);
        float favHumi = sharedPref.getFloat(getString(R.string.pref_user_humidity_key), defHumi);
        float humiDelta = (float) getResources().getInteger(R.integer.humidity_liking_max_delta);

        if (favHumi + humiDelta < humidity) {
            return 1;
        } else if (favHumi - humiDelta > humidity) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * Receiver for sensor data that is sent by SensorService.
     */
    public class SensorDataReceiver extends BroadcastReceiver {

        /**
         * Response code to be used by SensorService.
         */
        public static final String ACTION_RESP = "SENSOR_DATA_ACTION_RESP";

        @Override
        public void onReceive(Context context, Intent intent) {
            SensorData sData = (SensorData) intent.getSerializableExtra(SensorService.PARAM_OUT_DATA);
            if (sData == null) {
                String err_msg = intent.getStringExtra(SensorService.PARAM_ERROR_MSG);
                Toast.makeText(context, err_msg, Toast.LENGTH_SHORT).show();
                return;
            }
            sensorData = sData;

            // update view
            TextView temperatureView = (TextView) findViewById(R.id.temperature);
            temperatureView.setText(String.format(getString(R.string.temperature), sData.getTemperature()));

            TextView humidView = (TextView) findViewById(R.id.humidity);
            humidView.setText(String.format(getString(R.string.humidity), sData.getHumidity()));

            TextView coView = (TextView) findViewById(R.id.co);
            float coValue = sData.getCoData();
            coView.setText(String.format(getString(R.string.co), coValue));
            showCoInfo(context, coView, coValue);

            updateFeelViews();

            Toast.makeText(context, R.string.sensor_data_updated, Toast.LENGTH_SHORT).show();
        }

        /**
         * Show CO information and publish additional error messages.
         * @param context
         * @param view
         * @param coValue
         */
        private void showCoInfo(Context context, TextView view, float coValue) {
            float coLowLimit = (float) getResources().getInteger(R.integer.co_low_limit);
            float coHighLimit = (float) getResources().getInteger(R.integer.co_high_limit);

            if (coValue >= coHighLimit) {
                view.setTextAppearance(context, R.style.RedTextView);
                showAlert(R.string.high_co_warning);
            } else if (coValue >= coLowLimit) {
                view.setTextAppearance(context, R.style.YellowTextView);
                showAlert(R.string.co_warning);
            } else {
                view.setTextAppearance(context, R.style.NormalTextView);
            }
        }

        /**
         * Show alert popup.
         * @param alertText
         */
        private void showAlert(int alertText) {
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
