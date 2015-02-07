package es.uem.david.samuel.nacho.yepnsd;

import android.app.Activity;
import android.view.View;

/**
 * Created by usuario.apellido on 06/02/2015.
 *
 * @author david.sancho
 */
public class Util {

    public static final void hideView(Activity activity, int id) {
        View vFound = activity.findViewById(id);
        if (vFound != null) {
            vFound.setVisibility(View.INVISIBLE);
        }
    }

    public static final void hideView(View view, int id) {
        View vFound = view.findViewById(id);
        if (vFound != null) {
            vFound.setVisibility(View.INVISIBLE);
        }
    }
}
