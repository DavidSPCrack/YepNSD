package es.uem.david.samuel.nacho.yepnsd.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import es.uem.david.samuel.nacho.yepnsd.R;
import es.uem.david.samuel.nacho.yepnsd.adapters.UserAdapter;
import es.uem.david.samuel.nacho.yepnsd.constants.Constantes;
import es.uem.david.samuel.nacho.yepnsd.utils.FileHelper;
import es.uem.david.samuel.nacho.yepnsd.utils.UtilActivity;


public class RecipientsActivity extends AbstractActionBarActivity {

    private static RecipientsActivity act;
    private MenuItem mSendMenuItem;
    private Uri mMediaUri;
    private String mFileType;
    private GridView mFriendsGrid;
    private UserAdapter adapter;

    public static RecipientsActivity getInstance() {
        return act;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_grid);

        act = this;

        Intent intent = getIntent();
        mMediaUri = intent.getData();
        mFileType = intent.getStringExtra(Constantes.ParseClasses.Messages.KEY_FILE_TYPE);

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
                if(selected) {
                    checkImageView.setVisibility(View.VISIBLE);
                } else {
                    checkImageView.setVisibility(View.INVISIBLE);
                }
                if (mFriendsGrid.getCheckedItemCount() > 0) {
                    showSendMessage();
                } else {
                    hideSendMessage();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipients, menu);
        mSendMenuItem = menu.findItem(R.id.action_send);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send) {
            final UtilActivity util = getUtil();

            final ProgressDialog pd = util.getProgressDialog(R.string.please_wait);

            AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... params) {
                    ParseObject message = createMessage(mMediaUri, mFileType);
                    if (message != null) {
                        sendMessage(message, pd);
                    } else {
                        pd.dismiss();
                        util.doAlertDialog(R.string.error_sending_message);
                    }
                    return null;
                }
            };
            asyncTask.execute((Void) null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final UtilActivity util = getUtil();
        final View progressBar = findViewById(R.id.progressBarFriends);

        ParseUser mCurrentUser = ParseUser.getCurrentUser();
        ParseRelation<ParseUser> mFriendsRelation = mCurrentUser.getRelation(Constantes.Users.FRIENDS_RELATION);

        ParseQuery<ParseUser> qFriends = mFriendsRelation.getQuery();
        qFriends.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e == null) {
                    adapter.refill(users);
                    progressBar.setVisibility(View.GONE);
                } else {
                    util.doAlertDialog(e);
                }
            }
        });
    }

    private void sendMessage(final ParseObject pObject, final ProgressDialog pd) {
        final UtilActivity util = getUtil();
        pObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    sendPush(pObject, pd);
                    util.doToast(R.string.message_sended);
                    finish();
                } else {
                    pd.dismiss();
                    util.doAlertDialog(e);
                }
            }
        });
    }

    private void sendPush(final ParseObject pObject, final ProgressDialog pd) {
        AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                ParseObject message = createMessage(mMediaUri, mFileType);
                if (message != null) {
                    ParsePush pPush = new ParsePush();

                    pd.dismiss();
                } else {
                    pd.dismiss();
                }
                return null;
            }
        };
        asyncTask.execute((Void) null);
    }

    public ParseObject createMessage(Uri mMediaUri, String mFileType) {
        final UtilActivity util = getUtil();
        ParseUser mCurrentUser = ParseUser.getCurrentUser();

        ParseObject message = new ParseObject(Constantes.ParseClasses.Messages.CLASS);
        message.put(Constantes.ParseClasses.Messages.KEY_ID_SENDER, mCurrentUser.getObjectId());
        message.put(Constantes.ParseClasses.Messages.KEY_SENDER_NAME, mCurrentUser.getUsername());
        message.put(Constantes.ParseClasses.Messages.KEY_ID_RECIPIENTS, getRecipientsIds());

        byte[] fileBytes = FileHelper.getByteArrayFromFile(this, mMediaUri);
        if (fileBytes != null) {
            if (mFileType.equals(Constantes.FileTypes.IMAGE)) {
                fileBytes = FileHelper.reduceImageForUpload(fileBytes);
            }
            String fileName = FileHelper.getFileName(this, mMediaUri, mFileType);

            ParseFile pFile = new ParseFile(fileName, fileBytes);
            message.put(Constantes.ParseClasses.Messages.KEY_FILE, pFile);
            message.put(Constantes.ParseClasses.Messages.KEY_FILE_TYPE, mFileType);
        } else {
            util.doAlertDialog(R.string.error_sending_message);
            return null;
        }
        return message;
    }

    private ArrayList<String> getRecipientsIds() {
        ArrayList<String> recipients = new ArrayList<>();
        int count = mFriendsGrid.getCount();
        for (int i = 0; i < count; i++) {
            if (mFriendsGrid.isItemChecked(i)) {
                ParseUser pUser = adapter.getItem(i);
                if (pUser != null)
                    recipients.add(pUser.getObjectId());
            }
        }
        return recipients;
    }

    public void hideSendMessage() {
        if (mSendMenuItem != null)
            mSendMenuItem.setVisible(false);
    }

    public void showSendMessage() {
        if (mSendMenuItem != null)
            mSendMenuItem.setVisible(true);
    }
}
