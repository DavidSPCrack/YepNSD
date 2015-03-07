package es.uem.david.samuel.nacho.yepnsd.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import es.uem.david.samuel.nacho.yepnsd.R;
import es.uem.david.samuel.nacho.yepnsd.constants.Constantes;
import es.uem.david.samuel.nacho.yepnsd.ui.fragments.RecipientsFragment;
import es.uem.david.samuel.nacho.yepnsd.utils.UtilActivity;


public class RecipientsActivity extends AbstractActionBarActivity {

    private static RecipientsActivity act;
    private MenuItem mSendMenuItem;
    private Uri mMediaUri;
    private String mFileType;

    public static RecipientsActivity getInstance() {
        return act;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipients);

        act = this;

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
            final UtilActivity util = getUtil();
            final RecipientsFragment fragment = RecipientsFragment.getInstance();

            final ProgressDialog pd =  util.getProgressDialog(R.string.please_wait);

            AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... params) {
                    ParseObject message = null;
                    if (fragment != null) {
                        message = fragment.createMessage(mMediaUri, mFileType);
                    }
                    if (message != null) {
                        sendMessage(message);
                    } else {
                        util.doAlertDialog(R.string.error_sending_message);
                    }
                    pd.dismiss();
                    return null;
                }
            };
            asyncTask.execute((Void) null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendMessage(ParseObject pObject) {
        final UtilActivity util = getUtil();
        pObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    util.doToast(R.string.message_sended);
                    finish();
                } else {
                    util.doAlertDialog(e);
                }
            }
        });
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
