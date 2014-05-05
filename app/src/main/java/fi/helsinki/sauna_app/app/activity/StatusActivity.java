package fi.helsinki.sauna_app.app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import fi.helsinki.sauna_app.app.Result;
import fi.helsinki.sauna_app.app.model.SensorData;
import fi.helsinki.sauna_app.app.service.SensorService;

public class StatusActivity extends Activity {

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
            showCoInfo(context, coView, coValue);

            Toast.makeText(context, R.string.sensor_data_updated, Toast.LENGTH_SHORT).show();
        }

        private void showCoInfo(Context context, TextView view, float coValue) {
            if (coValue >= CO_HIGH_LIMIT) {
                view.setTextAppearance(context, R.style.RedTextView);
                showAlert(R.string.high_co_warning);
            } else if (coValue >= CO_LOW_LIMIT) {
                view.setTextAppearance(context, R.style.YellowTextView);
                showAlert(R.string.co_warning);
            } else {
                view.setTextAppearance(context, R.style.NormalTextView);
            }
        }

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
