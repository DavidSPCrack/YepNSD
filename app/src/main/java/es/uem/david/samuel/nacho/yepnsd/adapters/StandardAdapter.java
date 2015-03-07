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
        this.objects = objects;
        this.names = names;
    }

    public StandardAdapter(Context context, int resource) {
        this(context, resource, new ArrayList<T>(), new ArrayList<String>());
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

    public void refresh(List<T> objects) {
        this.objects = objects;
        this.names.clear();
        List<String> newNames = getStringList(objects);
        this.names.addAll(newNames);
        notifyDataSetChanged();
    }

    public T get(int i) {
        return objects.get(i);
    }

    public String getName(int i) {
        String name = names.get(i);
        return name == null ? "" : name;
    }

    public T get(String name) {
        for (int i = 0; i < names.size(); i++) {
            if (name.equals(names.get(i))) {
                return objects.get(i);
            }
        }
        return null;
    }

    public int getPosition(String name) {
        for (int i = 0; i < names.size(); i++) {
            if (name.equals(names.get(i))) {
                return i;
            }
        }
        return -1;
    }
}
