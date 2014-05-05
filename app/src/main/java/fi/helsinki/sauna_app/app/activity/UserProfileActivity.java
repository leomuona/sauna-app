package fi.helsinki.sauna_app.app.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import fi.helsinki.sauna_app.app.R;

public class UserProfileActivity extends ActionBarActivity {

    private static final int RESULT_STATUS = 100;
    private static final int RESULT_USER_PROFILE = 101;
    private static final int RESULT_SETTINGS = 102;
    private static final int RESULT_QUIT = 103;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
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

                return true;
            case R.id.action_user_profile:

                return true;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivityForResult(settingsIntent, RESULT_SETTINGS);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
