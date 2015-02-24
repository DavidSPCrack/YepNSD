package es.uem.david.samuel.nacho.yepnsd;

import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.text.Editable;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseUser;

/**
 * Created by nacho on 21/02/2015.
 *
 * @author nacho
 */
public class LoginTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    private static final String USERNAME = "N A C H O";
    private static final String PASSWORD = "N A C H O";

    private LoginActivity actividad;
    private EditText username;
    private EditText password;
    private Button login;

    public LoginTest() {
        super(LoginActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        actividad = getActivity();
        username = (EditText) actividad.findViewById(R.id.usernameField);
        password = (EditText) actividad.findViewById(R.id.passwordField);
        login = (Button) actividad.findViewById(R.id.loginbutton);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testLogin() {
        // Primero hacemos logout
        ParseUser.logOut();

        // Rellenamos los campos
        TouchUtils.tapView(this, username);
        sendKeys(USERNAME);

        TouchUtils.tapView(this, password);
        sendKeys(PASSWORD);

        // Hacemos click al boton de login
        TouchUtils.clickView(this, login);

        // Comprobamos que tenemos usuario
        ParseUser pUser = ParseUser.getCurrentUser();
        assertNotNull(pUser);
    }
}
