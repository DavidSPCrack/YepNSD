package es.uem.david.samuel.nacho.yepnsd;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by david.sancho on 16/01/2015.
 * @author david.sancho
 */
public class YepApplication extends Application {

    @Override
    public void onCreate() {
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "t6VmmFjjxEJmJWlmMPD1OWX74QP0l2lxlMZAoNE1", "YXGs5UvIXHkDwpJ91mrOlzVw5gs0xutvNr7eW25K");
    }
}
