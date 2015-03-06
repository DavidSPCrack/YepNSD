package es.uem.david.samuel.nacho.yepnsd;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class SignUpActivity extends AbstractActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }

    public void cancelClick(View v) {
        UtilActivity util = getUtil();
        util.openNewActivity(LoginActivity.class);
    }

    public void signUpClick(View v) {
        final UtilActivity util = getUtil();
        String username = util.getEditTextValueAndValidate(R.id.usernameField);
        if (!username.isEmpty()) {
            String password = util.getEditTextValueAndValidate(R.id.passwordField);
            if (!password.isEmpty()) {
                String email = util.getEditTextValueAndValidate(R.id.emailField);
                if (!email.isEmpty()) {

                    boolean valid = !util.isAnyEmpty(username, password, email);

                    if (valid) {
                        ParseUser newUser = new ParseUser();
                        newUser.setUsername(username);
                        newUser.setPassword(password);
                        newUser.setEmail(email);

                        final ProgressDialog pd = util.getProgressDialog(R.string.signing_up);
                        newUser.signUpInBackground(new SignUpCallback() {
                            @Override
                            public void done(ParseException e) {
                            util.hideProgressDialog(pd);
                            if (e == null) {
                                util.openNewActivity(MainActivityTabbed.class);
                            } else {
                                util.doAlertDialog(e);
                            }
                            }
                        });
                    }
                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_sign_up, menu);
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
