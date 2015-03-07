package es.uem.david.samuel.nacho.yepnsd.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by usuario.apellido on 07/03/2015.
 *
 * @author david.sancho
 */
public class StandardAdapter<T> extends ArrayAdapter<String> {

    private List<T> objects;
    private List<String> names;

    private StandardAdapter(Context context, int resource, List<T> objects, List<String> names) {
        super(context, resource, names);
        this.names = names;
        this.objects = objects;
    }

    public StandardAdapter(Context context, int resource, List<T> objects) {
        this(context, resource, objects, getStringList(objects));
    }

    private static List<String> getStringList(List<?> objects) {
        ArrayList<String> lista = new ArrayList<>();
        for (Object object : objects) {
            if (object instanceof ParseUser) {
                ParseUser user = (ParseUser) object;
                lista.add(user.getUsername());
            } else {
                lista.add(object.toString());
            }
        }
        return lista;
    }

    public T get(int i) {
        return objects.get(i);
    }

    public String getName(int i) {
        String name = names.get(i);
        return name == null ? "" : name;
    }
}
