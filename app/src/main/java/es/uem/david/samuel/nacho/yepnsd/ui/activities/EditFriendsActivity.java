package es.uem.david.samuel.nacho.yepnsd.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import es.uem.david.samuel.nacho.yepnsd.R;
import es.uem.david.samuel.nacho.yepnsd.adapters.StandardAdapter;
import es.uem.david.samuel.nacho.yepnsd.adapters.UserAdapter;
import es.uem.david.samuel.nacho.yepnsd.constants.Constantes;
import es.uem.david.samuel.nacho.yepnsd.utils.UtilActivity;


public class EditFriendsActivity extends AbstractActionBarActivity {

    private ParseRelation<ParseUser> mFriendsRelation;
    private ParseUser mCurrentUser;
    private GridView mFriendsGrid;
    private UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_grid);

        UtilActivity util = getUtil();
        adapter = new UserAdapter(this, new ArrayList<ParseUser>());


        mFriendsGrid = (GridView) findViewById(R.id.friendsGrid);
        mFriendsGrid.setAdapter(adapter);
        mFriendsGrid.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);

        TextView emptyText = (TextView) findViewById(android.R.id.empty);
        mFriendsGrid.setEmptyView(emptyText);

        mFriendsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView checkImageView = (ImageView)view.findViewById(R.id.checkImageView);
                boolean selected = mFriendsGrid.isItemChecked(position);

                ParseUser user = adapter.getItem(position);
                if (selected) {
                    mFriendsRelation.add(user);
                    checkImageView.setVisibility(View.VISIBLE);
                } else {
                    mFriendsRelation.remove(user);
                    checkImageView.setVisibility(View.INVISIBLE);
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
                    adapter.refill(users);
                    addFriendCheckmarks();
                } else {
                    UtilActivity util = getUtil();
                    util.doAlertDialog(e);
                }
            }
        });
    }

    private void addFriendCheckmarks() {
        ParseQuery<ParseUser> qFriends = mFriendsRelation.getQuery();
        qFriends.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e == null) {
                    for (ParseUser user : users) {

                        int pos = adapter.getPosition(user);
                        if (pos != -1)
                            mFriendsGrid.setItemChecked(pos, true);
                    }
                } else {
                    UtilActivity util = getUtil();
                    util.doAlertDialog(e);
                }
            }
        });

    }
}
