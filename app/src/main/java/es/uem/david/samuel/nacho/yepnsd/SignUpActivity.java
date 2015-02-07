package es.uem.david.samuel.nacho.yepnsd;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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

    public void signUpClick(View v) {
        String username = getEditTextValueAndValidate(R.id.usernameField);
        if (!username.isEmpty()) {
            String password = getEditTextValueAndValidate(R.id.passwordField);
            if (!password.isEmpty()) {
                String email = getEditTextValueAndValidate(R.id.emailField);
                if (!email.isEmpty()) {

                    boolean valid = !isAnyEmpty(username, password, email);

                    if (valid) {
                        ParseUser newUser = new ParseUser();
                        newUser.setUsername(username);
                        newUser.setPassword(password);
                        newUser.setEmail(email);

                        final ProgressDialog pd = getProgressDialog(R.string.signing_up);
                        newUser.signUpInBackground(new SignUpCallback() {
                            @Override
                            public void done(ParseException e) {
                                pd.dismiss();
                                if (e == null) {
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
