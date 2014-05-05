package fi.helsinki.sauna_app.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import fi.helsinki.sauna_app.app.R;
import fi.helsinki.sauna_app.app.Result;

public class UserProfileActivity extends Activity {

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
}
