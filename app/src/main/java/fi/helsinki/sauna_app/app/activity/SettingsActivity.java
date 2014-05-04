package fi.helsinki.sauna_app.app.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import fi.helsinki.sauna_app.app.R;

/**
 * Created by leo on 5/4/14.
 */
public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_PREF_DRONE_MAC_ADDRESS = "pref_drone_mac_address";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Preference dMacPref = findPreference(KEY_PREF_DRONE_MAC_ADDRESS);
        dMacPref.setSummary(sharedPref.getString(KEY_PREF_DRONE_MAC_ADDRESS, ""));
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (KEY_PREF_DRONE_MAC_ADDRESS.equals(key)) {
            Preference pref = findPreference(key);
            pref.setSummary(sharedPreferences.getString(key, ""));
        }
    }
}
