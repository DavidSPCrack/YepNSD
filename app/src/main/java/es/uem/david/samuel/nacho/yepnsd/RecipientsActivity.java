package es.uem.david.samuel.nacho.yepnsd;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;


public class RecipientsActivity extends AbstractListActivity {

    private ParseRelation<ParseUser> mFriendsRelation;
    private ParseUser mCurrentUser;
    private List<ParseUser> mFriends;
    private ArrayList<String> usernames;
    private ArrayAdapter<String> adapter;
    private MenuItem mSendMenuItem;
    private Uri mMediaUri;
    private String mFileType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_recipients);

        Intent intent = getIntent();
        mMediaUri = intent.getData();
        mFileType = intent.getStringExtra(Constantes.ParseClasses.Messages.KEY_FILE_TYPE);
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
            ParseObject message = createMessage();
            if (message != null) {
                sendMessage(message);
            } else {
                //TODO Message Error
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final Activity act = this;
        final View progressBar = findViewById(R.id.progressBar);

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(Constantes.Users.FRIENDS_RELATION);

        usernames = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, usernames);

        ListView listView = getListView();
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        setListAdapter(adapter);

        ParseQuery<ParseUser> qFriends = mFriendsRelation.getQuery();
        qFriends.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e == null) {
                    mFriends = users;
                    for (ParseUser user : users) {
                        adapter.add(user.getUsername());
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    String title = getString(R.string.alert);
                    String msg = e.getMessage();
                    String button = getString(R.string.alert_button);

                    AlertDialog alertDialog = new AlertDialog.Builder(act).create();
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


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (l.getCheckedItemCount() > 0)
            mSendMenuItem.setVisible(true);
        else
            mSendMenuItem.setVisible(false);

    }

    private void sendMessage(ParseObject pObject) {
        pObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(RecipientsActivity.this, "Message sended!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    // TODO Error Message
                }
            }
        });
    }

    private ParseObject createMessage() {
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
            // TODO Mensaje de error
            return null;
        }
        return message;
    }

    private ArrayList<String> getRecipientsIds() {
        ArrayList<String> recipients = new ArrayList<String>();
        ListView listView = getListView();
        int count = listView.getCount();
        for (int i = 0; i < count; i++) {
            if (listView.isItemChecked(i)) {
                ParseUser pUser = mFriends.get(i);
                if (pUser != null)
                    recipients.add(pUser.getObjectId());
            }
        }
        return recipients;
    }
}
