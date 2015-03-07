package es.uem.david.samuel.nacho.yepnsd.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import es.uem.david.samuel.nacho.yepnsd.R;
import es.uem.david.samuel.nacho.yepnsd.adapters.StandardAdapter;
import es.uem.david.samuel.nacho.yepnsd.constants.Constantes;
import es.uem.david.samuel.nacho.yepnsd.utils.UtilActivity;


public class EditFriendsActivity extends AbstractListActivity {

    private ParseRelation<ParseUser> mFriendsRelation;
    private ParseUser mCurrentUser;
    private StandardAdapter<ParseUser> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_friends);

        UtilActivity util = getUtil();
        adapter = util.getAdapterUsers(android.R.layout.simple_list_item_checked);

        ListView listView = getListView();
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        boolean selected = getListView().isItemChecked(position);

        ParseUser user = adapter.get(position);
        if (selected) {
            mFriendsRelation.add(user);
        } else {
            mFriendsRelation.remove(user);
        }

        mCurrentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
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


        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.orderByAscending(Constantes.Users.FIELD_USERNAME);
        query.setLimit(Constantes.Users.MAX_USERS);

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e == null) {
                    final View progressBar = findViewById(R.id.progressBarFriends);
                    progressBar.setVisibility(View.GONE);
                    for (int i = 0; i < users.size(); i++) {
                        ParseUser pUser = users.get(i);
                        if (mCurrentUser.getUsername().equals(pUser.getUsername())) {
                            users.remove(i);
                            break;
                        }
                    }
                    adapter.refresh(users);
                    addFriendCheckmarks();
                } else {
                    UtilActivity util = getUtil();
                    util.doAlertDialog(e);
                }
            }
        });
    }

    private void addFriendCheckmarks() {
        final ListView listView = getListView();

        ParseQuery<ParseUser> qFriends = mFriendsRelation.getQuery();
        qFriends.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e == null) {
                    for (ParseUser user : users) {

                        int pos = adapter.getPosition(user.getUsername());
                        if (pos != -1)
                            listView.setItemChecked(pos, true);
                    }
                } else {
                    UtilActivity util = getUtil();
                    util.doAlertDialog(e);
                }
            }
        });

    }
}
