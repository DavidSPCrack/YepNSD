package es.uem.david.samuel.nacho.yepnsd;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;


public class LoginActivity extends AbstractActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }

    public void openSignUp(View v) {
        openNewActivity(SignUpActivity.class, false);
    }

    public void loginClick(View v) {
        String username = getEditTextValueAndValidate(R.id.usernameField);
        if (!username.isEmpty()) {
            String password = getEditTextValueAndValidate(R.id.passwordField);
            if (!password.isEmpty()) {

                final ProgressDialog pd = getProgressDialog(R.string.login_in);
                ParseUser.logInInBackground(username, password, new LogInCallback() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        pd.dismiss();
                        if (parseUser != null) {
                            openNewActivity(MainActivityTabbed.class);
                        } else {
                            String title = getResourceString(R.string.alert);
                            String msg =  e.getMessage();
                            String button = getResourceString(R.string.alert_button);
                            doDialog(title, msg, button);
                        }
                    }
                });
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
