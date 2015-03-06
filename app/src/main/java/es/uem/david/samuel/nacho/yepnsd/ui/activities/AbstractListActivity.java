package es.uem.david.samuel.nacho.yepnsd.ui.activities;

import android.app.ListActivity;

import es.uem.david.samuel.nacho.yepnsd.utils.UtilActivity;

/**
 * Created by usuario.apellido on 23/01/2015.
 *
 * @author david.sancho
 */
public abstract class AbstractListActivity extends ListActivity {

    protected UtilActivity getUtil() {
        return new UtilActivity(this);
    }

}
