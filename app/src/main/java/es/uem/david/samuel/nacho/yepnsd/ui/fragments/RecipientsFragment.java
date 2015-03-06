package es.uem.david.samuel.nacho.yepnsd.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
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
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import es.uem.david.samuel.nacho.yepnsd.constants.Constantes;
import es.uem.david.samuel.nacho.yepnsd.R;
import es.uem.david.samuel.nacho.yepnsd.ui.activities.RecipientsActivity;
import es.uem.david.samuel.nacho.yepnsd.utils.FileHelper;


public class RecipientsFragment extends ListFragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private static RecipientsFragment recipientsFragment;
    private ParseRelation<ParseUser> mFriendsRelation;
    private ParseUser mCurrentUser;
    private List<ParseUser> mFriends;
    private ArrayList<String> usernames;
    private ArrayAdapter<String> adapter;

    public static RecipientsFragment getInstance() {
        return recipientsFragment;
    }

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
        View rootView = inflater.inflate(R.layout.fragment_recipients, container, false);

        recipientsFragment = this;

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        final FragmentActivity fAct = getActivity();
        final View progressBar = fAct.findViewById(R.id.progressBar);

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(Constantes.Users.FRIENDS_RELATION);

        usernames = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(fAct, android.R.layout.simple_list_item_checked, usernames);

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


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (l.getCheckedItemCount() > 0) {
            RecipientsActivity rAct = RecipientsActivity.getInstance();
            if (rAct != null) {
                rAct.showSendMessage();
            }
        } else {
            RecipientsActivity rAct = RecipientsActivity.getInstance();
            if (rAct != null) {
                rAct.hideSendMessage();
            }
        }

    }

    public ParseObject createMessage(Uri mMediaUri, String mFileType) {
        final FragmentActivity fAct = getActivity();
        ParseUser mCurrentUser = ParseUser.getCurrentUser();

        ParseObject message = new ParseObject(Constantes.ParseClasses.Messages.CLASS);
        message.put(Constantes.ParseClasses.Messages.KEY_ID_SENDER, mCurrentUser.getObjectId());
        message.put(Constantes.ParseClasses.Messages.KEY_SENDER_NAME, mCurrentUser.getUsername());
        message.put(Constantes.ParseClasses.Messages.KEY_ID_RECIPIENTS, getRecipientsIds());

        byte[] fileBytes = FileHelper.getByteArrayFromFile(fAct, mMediaUri);
        if (fileBytes != null) {
            if (mFileType.equals(Constantes.FileTypes.IMAGE)) {
                fileBytes = FileHelper.reduceImageForUpload(fileBytes);
            }
            String fileName = FileHelper.getFileName(fAct, mMediaUri, mFileType);

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
