package es.uem.david.samuel.nacho.yepnsd.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import es.uem.david.samuel.nacho.yepnsd.constants.Constantes;
import es.uem.david.samuel.nacho.yepnsd.R;
import es.uem.david.samuel.nacho.yepnsd.utils.UtilActivity;


public class EditFriendsActivity extends AbstractListActivity {

    public static final String TAG = EditFriendsActivity.class.getSimpleName();

    private ParseRelation<ParseUser> mFriendsRelation;
    private ParseUser mCurrentUser;
    private List<ParseUser> mUsers;
    private ArrayList<String> usernames;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_friends);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        boolean selected = getListView().isItemChecked(position);

        String username = usernames.get(position);

        ParseUser user = null;
        if (username != null)
            for (ParseUser mUser : mUsers) {
                if (username.equals(mUser.getUsername())) {
                    user = mUser;
                    break;
                }
            }
        if (selected) {
            mFriendsRelation.add(user);
        } else {
            mFriendsRelation.remove(user);
        }

        mCurrentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {

                } else {
                    UtilActivity util = getUtil();
                    util.doAlertDialog(e);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(Constantes.Users.FRIENDS_RELATION);

        usernames = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, usernames);

        ListView listView = getListView();
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);


        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.orderByAscending(Constantes.Users.FIELD_USERNAME);
        query.setLimit(Constantes.Users.MAX_USERS);

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e == null) {
                    mUsers = users;
                    for (ParseUser user : mUsers) {
                        if (!mCurrentUser.getUsername().equals(user.getUsername())) {
                            adapter.add(user.getUsername());
                        }
                    }
                    addFriendCheckmarks();
                } else {
                    UtilActivity util = getUtil();
                    util.doAlertDialog(e);
                }
            }
        });
    }

    private void addFriendCheckmarks() {
        final View progressBar = findViewById(R.id.progressBarFriends);
        final ListView listView = getListView();

        ParseQuery<ParseUser> qFriends = mFriendsRelation.getQuery();
        qFriends.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e == null) {
                    for (ParseUser user : mUsers) {
                        boolean exist = existRelation(users, user);
                        int pos = -1;
                        for (int i = 0; i < usernames.size(); i++) {
                            if (usernames.get(i).equals(user.getUsername())) {
                                pos = i;
                                break;
                            }
                        }
                        if (pos != -1)
                            listView.setItemChecked(pos, exist);
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    UtilActivity util = getUtil();
                    util.doAlertDialog(e);
                }
            }
        });

    }

    private boolean existRelation(List<ParseUser> relations, ParseUser user) {
        if (relations != null)
            for (ParseUser rUser : relations) {
                if (user.getUsername().equals(rUser.getUsername())) {
                    return true;
                }
            }
        return false;
    }
}
