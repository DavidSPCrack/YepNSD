package es.uem.david.samuel.nacho.yepnsd;


import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by usuario.apellido on 06/02/2015.
 *
 * @author david.sancho
 */
public class InboxFragment extends ListFragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static InboxFragment newInstance(int sectionNumber) {
        InboxFragment fragment = new InboxFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);

        Util.hideView(rootView, R.id.progressBar);

        return rootView;
    }
}
