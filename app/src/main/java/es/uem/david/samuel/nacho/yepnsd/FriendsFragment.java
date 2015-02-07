package es.uem.david.samuel.nacho.yepnsd;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by usuario.apellido on 06/02/2015.
 *
 * @author david.sancho
 */
public class FriendsFragment extends ListFragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private ParseRelation<ParseUser> mFriendsRelation;
    private ParseUser mCurrentUser;
    private ArrayList<String> usernames;
    private ArrayAdapter<String> adapter;

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

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        final FragmentActivity fAct = getActivity();
        final View progressBar = fAct.findViewById(R.id.progressBarFriends);

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(Constantes.Users.FRIENDS_RELATION);

        usernames = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(fAct, android.R.layout.simple_list_item_1, usernames);


        setListAdapter(adapter);

        ParseQuery<ParseUser> qFriends = mFriendsRelation.getQuery();
        qFriends.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e == null) {
                    for (ParseUser user : users) {
                        adapter.add(user.getUsername());
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    String title = fAct.getString(R.string.alert);
                    String msg = e.getMessage();
                    String button = fAct.getString(R.string.alert_button);

                    AlertDialog alertDialog = new AlertDialog.Builder(fAct).create();
                    alertDialog.setTitle(title);
                    alertDialog.setMessage(msg);
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, button,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            }
        });
    }
}
