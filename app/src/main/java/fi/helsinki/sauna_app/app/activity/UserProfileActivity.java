package fi.helsinki.sauna_app.app.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import fi.helsinki.sauna_app.app.R;
import fi.helsinki.sauna_app.app.Result;

public class UserProfileActivity extends Activity {

    public static final String PREF_USER_TEMP_KEY = "pref_user_temp_key";
    public static final String PREF_USER_HUMI_KEY = "pref_user_humi_key";

    private Button modTempButton;
    private Button modHumiButton;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // set default values if able
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        float defTemp = getResources().getInteger(R.integer.favourite_temperature_default_value);
        float useTemp = sharedPref.getFloat(PREF_USER_TEMP_KEY, defTemp);
        updateTemperature(useTemp);

        float defHumi = getResources().getInteger(R.integer.favourite_humidity_default_value);
        float useHumi = sharedPref.getFloat(PREF_USER_HUMI_KEY, defHumi);
        updateHumidity(useHumi);

        // temperature button
        modTempButton = (Button) findViewById(R.id.button_set_temperature);
        modTempButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(getString(R.string.button_set_temperature));
                final EditText input =  new EditText(context);
                input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                builder.setView(input);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String s = input.getText().toString();
                        try {
                            float f = Float.parseFloat(s);
                            updateTemperature(f);
                            dialog.dismiss();
                        } catch (NumberFormatException nfe) {
                            dialog.dismiss();
                            Toast.makeText(context, getString(R.string.invalid_input), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        // humidity button
        modHumiButton = (Button) findViewById(R.id.button_set_humidity);
        modHumiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(getString(R.string.button_set_humidity));
                final EditText input =  new EditText(context);
                input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                builder.setView(input);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String s = input.getText().toString();
                        try {
                            float f = Float.parseFloat(s);
                            updateHumidity(f);
                            dialog.dismiss();
                        } catch (NumberFormatException nfe) {
                            dialog.dismiss();
                            Toast.makeText(context, getString(R.string.invalid_input), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
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
                this.setResult(Result.RESULT_USER_PROFILE);
                finish(); // returns to status activity
                return true;
            case R.id.action_user_profile:
                // do nothing, we are already here.
                return true;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivityForResult(settingsIntent, Result.RESULT_SETTINGS);
                return true;
            case R.id.action_quit:
                this.setResult(Result.RESULT_QUIT);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateTemperature(float temperature) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat(PREF_USER_TEMP_KEY, temperature);
        editor.commit();

        TextView tempView = (TextView) findViewById(R.id.liking_temperature_range);
        tempView.setText(String.format(getString(R.string.favourite_temperature), temperature));
    }

    private void updateHumidity(float humidity) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat(PREF_USER_HUMI_KEY, humidity);
        editor.commit();

        TextView humiView = (TextView) findViewById(R.id.liking_humidity_range);
        humiView.setText(String.format(getString(R.string.favourite_humidity), humidity));
    }
}
