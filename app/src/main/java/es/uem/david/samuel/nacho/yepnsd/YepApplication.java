package es.uem.david.samuel.nacho.yepnsd;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseCrashReporting;
import com.parse.PushService;

import es.uem.david.samuel.nacho.yepnsd.ui.activities.MainActivityTabbed;
import es.uem.david.samuel.nacho.yepnsd.utils.ParseAD;

/**
 * Created by david.sancho on 16/01/2015.
 *
 * @author david.sancho
 */
public class YepApplication extends Application {

    private static YepApplication app;

    public static YepApplication getInstance() {
        return app;
    }

    @Override
    public void onCreate() {
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        ParseCrashReporting.enable(this);

        Parse.initialize(this, "t6VmmFjjxEJmJWlmMPD1OWX74QP0l2lxlMZAoNE1", "YXGs5UvIXHkDwpJ91mrOlzVw5gs0xutvNr7eW25K");

        PushService.setDefaultPushCallback(this, MainActivityTabbed.class,
                R.drawable.ic_action_camera);

        ParseAD adatos = ParseAD.getInstance();
        adatos.updateInstallation();

        app = this;
    }

}
