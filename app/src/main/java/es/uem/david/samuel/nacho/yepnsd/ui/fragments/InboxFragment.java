package es.uem.david.samuel.nacho.yepnsd.ui.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import es.uem.david.samuel.nacho.yepnsd.R;
import es.uem.david.samuel.nacho.yepnsd.adapters.MessageAdapter;
import es.uem.david.samuel.nacho.yepnsd.constants.Constantes;
import es.uem.david.samuel.nacho.yepnsd.ui.activities.ViewImageActivity;
import es.uem.david.samuel.nacho.yepnsd.utils.Util;
import es.uem.david.samuel.nacho.yepnsd.utils.UtilActivity;

/**
 * Created by usuario.apellido on 06/02/2015.
 *
 * @author david.sancho
 */
public class InboxFragment extends AbstractListFragment {

    private MessageAdapter adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

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

        final UtilActivity util = getUtil();
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                util.doToast(R.string.refreshing);
                refreshList();
            }
        });

        adapter = new MessageAdapter(getActivity(), new ArrayList<ParseObject>());
        setListAdapter(adapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        refreshList();
    }

    private void refreshList() {
        final UtilActivity util = getUtil();
        final FragmentActivity fAct = getActivity();
        final View progressBar = fAct.findViewById(R.id.progressBarInbox);
        setListAdapter(adapter);

        ParseUser pUser = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constantes.ParseClasses.Messages.CLASS);
        query.whereEqualTo(Constantes.ParseClasses.Messages.KEY_ID_RECIPIENTS, pUser.getObjectId());
        query.addDescendingOrder(Constantes.ParseClasses.Messages.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (parseObjects != null) {
                    adapter.refill(parseObjects);
                    if (mSwipeRefreshLayout.isRefreshing()) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
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

        ParseObject message = adapter.get(position);
        if (message != null) {
            String fileType = message.getString(Constantes.ParseClasses.Messages.KEY_FILE_TYPE);
            ParseFile file = message.getParseFile(Constantes.ParseClasses.Messages.KEY_FILE);
            Uri fileUri = Uri.parse(file.getUrl());
            if (fileType.equals(Constantes.FileTypes.IMAGE)) {

                Intent intent = new Intent(getActivity(), ViewImageActivity.class);
                intent.setData(fileUri);
                intent.putExtra(Constantes.ParseClasses.Messages.KEY_SENDER_NAME, message.getString(Constantes.ParseClasses.Messages.KEY_SENDER_NAME));
                intent.putExtra(Constantes.ParseClasses.Messages.KEY_CREATED_AT, Util.convertDate(message.getCreatedAt()));
                startActivity(intent);
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(fileUri, "video/*");
                startActivity(intent);
            }
        }
    }
}
