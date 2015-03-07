package es.uem.david.samuel.nacho.yepnsd.utils;

import com.parse.ParseInstallation;
import com.parse.ParseUser;

import es.uem.david.samuel.nacho.yepnsd.constants.Constantes;

/**
 * Created by usuario.apellido on 06/02/2015.
 *
 * @author david.sancho
 */
public class ParseAD {

    private static ParseAD instance = null;

    private ParseAD() {
    }

    public static ParseAD getInstance() {
        if (instance == null) {
            instance = new ParseAD();
        }
        return instance;
    }

    public ParseUser getCurrentUser() {
        return ParseUser.getCurrentUser();
    }

    public ParseInstallation getCurrentInstallation() {
        return ParseInstallation.getCurrentInstallation();
    }

    public synchronized void updateInstallation() {
        ParseUser user = getCurrentUser();
        ParseInstallation installation = getCurrentInstallation();
        if (user == null) {
            installation.remove(Constantes.Installation.USER);
        } else {
            installation.put(Constantes.Installation.USER, ParseUser.getCurrentUser());
        }
        installation.saveInBackground();
    }

}
