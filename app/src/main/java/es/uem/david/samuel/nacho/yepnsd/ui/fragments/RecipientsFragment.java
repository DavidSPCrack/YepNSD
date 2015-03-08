package es.uem.david.samuel.nacho.yepnsd.ui.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import es.uem.david.samuel.nacho.yepnsd.R;
import es.uem.david.samuel.nacho.yepnsd.adapters.StandardAdapter;
import es.uem.david.samuel.nacho.yepnsd.constants.Constantes;
import es.uem.david.samuel.nacho.yepnsd.ui.activities.RecipientsActivity;
import es.uem.david.samuel.nacho.yepnsd.utils.FileHelper;
import es.uem.david.samuel.nacho.yepnsd.utils.UtilActivity;


public class RecipientsFragment extends AbstractListFragment {


    private static RecipientsFragment recipientsFragment;
    private ParseRelation<ParseUser> mFriendsRelation;
    private ParseUser mCurrentUser;
    private StandardAdapter<ParseUser> adapter;

    public static RecipientsFragment getInstance() {
        return recipientsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipients, container, false);

        recipientsFragment = this;

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        UtilActivity util = getUtil();
        adapter = util.getAdapterUsers(android.R.layout.simple_list_item_checked);

        ListView listView = getListView();
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    @Override
    public void onResume() {
        super.onResume();

        final UtilActivity util = getUtil();
        final FragmentActivity fAct = getActivity();
        final View progressBar = fAct.findViewById(R.id.progressBar);

        mCurrentUser = ParseUser.getCurrentUser();
        mFriendsRelation = mCurrentUser.getRelation(Constantes.Users.FRIENDS_RELATION);

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
        final UtilActivity util = getUtil();
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
            util.doAlertDialog(R.string.error_sending_message);
            return null;
        }
        return message;
    }

    private ArrayList<String> getRecipientsIds() {
        ArrayList<String> recipients = new ArrayList<>();
        ListView listView = getListView();
        int count = listView.getCount();
        for (int i = 0; i < count; i++) {
            if (listView.isItemChecked(i)) {
                ParseUser pUser = adapter.get(i);
                if (pUser != null)
                    recipients.add(pUser.getObjectId());
            }
        }
        return recipients;
    }
}
