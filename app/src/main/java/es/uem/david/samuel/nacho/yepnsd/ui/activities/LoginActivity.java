package es.uem.david.samuel.nacho.yepnsd.ui.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import es.uem.david.samuel.nacho.yepnsd.R;
import es.uem.david.samuel.nacho.yepnsd.utils.UtilActivity;


public class LoginActivity extends AbstractActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }

    public void openSignUp(View v) {
        UtilActivity util = getUtil();
        util.openNewActivity(SignUpActivity.class, false);
    }

    public void loginClick(View v) {
        final UtilActivity util = getUtil();
        String username = util.getEditTextValueAndValidate(R.id.usernameField);
        if (!username.isEmpty()) {
            String password = util.getEditTextValueAndValidate(R.id.passwordField);
            if (!password.isEmpty()) {

                final ProgressDialog pd = util.getProgressDialog(R.string.login_in);
                ParseUser.logInInBackground(username, password, new LogInCallback() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        util.hideProgressDialog(pd);
                        if (parseUser != null) {
                            util.openNewActivity(MainActivityTabbed.class);
                        } else {
                            util.doAlertDialog(e);
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
