package es.uem.david.samuel.nacho.yepnsd.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

import es.uem.david.samuel.nacho.yepnsd.R;
import es.uem.david.samuel.nacho.yepnsd.adapters.StandardAdapter;
import es.uem.david.samuel.nacho.yepnsd.constants.Constantes;
import es.uem.david.samuel.nacho.yepnsd.utils.UtilActivity;

/**
 * Created by usuario.apellido on 06/02/2015.
 *
 * @author david.sancho
 */
public class FriendsFragment extends AbstractListFragment {


    private StandardAdapter<ParseUser> adapter;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FriendsFragment newInstance(int sectionNumber) {
        FriendsFragment fragment = new FriendsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        UtilActivity util = getUtil();
        adapter = util.getAdapterUsers(android.R.layout.simple_list_item_1);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        final UtilActivity util = getUtil();

        final FragmentActivity fAct = getActivity();
        final View progressBar = fAct.findViewById(R.id.progressBarFriends);

        ParseUser mCurrentUser = ParseUser.getCurrentUser();
        ParseRelation<ParseUser> mFriendsRelation = mCurrentUser.getRelation(Constantes.Users.FRIENDS_RELATION);

        setListAdapter(adapter);

        ParseQuery<ParseUser> qFriends = mFriendsRelation.getQuery();
        qFriends.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e == null) {
                    adapter.refresh(users);
                    progressBar.setVisibility(View.GONE);
                } else {
                    util.doAlertDialog(e);
                }
            }
        });
    }
}
