package es.uem.david.samuel.nacho.yepnsd.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseUser;

import es.uem.david.samuel.nacho.yepnsd.R;
import es.uem.david.samuel.nacho.yepnsd.adapters.StandardAdapter;

/**
 * Created by usuario.apellido on 28/02/2015.
 *
 * @author david.sancho
 */
public class UtilActivity {

    private Activity mActivity;

    public UtilActivity(Activity activity) {
        this.mActivity = activity;
    }

    public void doFieldAlertDialog(String field) {
        doAlertDialog(R.string.field_missing, field);
    }

    public void doAlertDialog(int msgId, String var) {
        String msg = getResourceString(msgId, var);
        doAlertDialog(msg);
    }

    public void doAlertDialog(int msgId) {
        String msg = getResourceString(msgId);
        doAlertDialog(msg);
    }

    public void doAlertDialog(Throwable t) {
        String msg = t.getMessage();
        doAlertDialog(msg);
    }

    public void doAlertDialog(String msg) {
        String title = getResourceString(R.string.alert);
        String button = getResourceString(R.string.alert_button);
        doDialog(title, msg, button);
    }

    public Resources getResources() {
        return mActivity.getResources();
    }

    public View findViewById(int id) {
        return mActivity.findViewById(id);
    }

    public String getResourceString(int id) {
        Resources res = getResources();
        return res.getString(id);
    }

    public String getResourceString(int id, String var1) {
        String s = getResourceString(id);
        return String.format(s, var1);
    }

    public String getResourceString(int id, String var1, String var2) {
        String s = getResourceString(id);
        return String.format(s, var1, var2);
    }

    public void doDialog(String title, String msg, String button) {
        AlertDialog alertDialog = new AlertDialog.Builder(mActivity).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setIcon(R.drawable.ic_warning_amber);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, button,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public String getEditTextValue(int id) {
        View v = findViewById(id);
        if (v != null) {
            if (v instanceof EditText) {
                EditText et = (EditText) v;
                Editable editable = et.getText();
                return editable.toString();
            }
        }
        return "";
    }

    public String getEditTextHint(int id) {
        View v = findViewById(id);
        if (v != null) {
            if (v instanceof EditText) {
                EditText et = (EditText) v;
                return et.getHint().toString();
            }
        }
        return "";
    }

    public String getEditTextValueAndValidate(int id) {
        String text = getEditTextValue(id);
        String field = getEditTextHint(id);
        validateNoEmpty(text, field);
        return text;
    }

    public boolean validateNoEmpty(String text, String field) {
        if (isTextEmpty(text)) {
            doFieldAlertDialog(field);
            return false;
        }
        return true;
    }

    public boolean isTextEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public boolean isAnyEmpty(String... strs) {
        for (String str : strs) {
            if (isTextEmpty(str)) {
                return true;
            }
        }
        return false;
    }

    public Drawable getDrawableRes(int id) {
        Resources res = getResources();
        return res.getDrawable(id);
    }

    public ProgressDialog getProgressDialog(int title) {
        return ProgressDialog.show(mActivity,
                getResourceString(title),
                getResourceString(R.string.please_wait), true);
    }

    public void openNewActivity(Class<?> dstClass) {
        openNewActivity(dstClass, true);
    }

    public void openNewActivity(Class<?> dstClass, boolean swClearTask) {
        Intent intent = new Intent(mActivity, dstClass);
        if (swClearTask) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        mActivity.startActivity(intent);
    }

    public void hideProgressDialog(ProgressDialog progressDialog) {
        progressDialog.dismiss();
    }

    public void hideView(int id) {
        View vFound = findViewById(id);
        if (vFound != null) {
            vFound.setVisibility(View.GONE);
        }
    }

    public void doToast(int textId) {
        String text = getResourceString(textId);
        doToast(text);
    }

    public void doToast(String text) {
        Toast.makeText(mActivity, text, Toast.LENGTH_SHORT).show();
    }

    public StandardAdapter<ParseUser> getAdapterUsers(int layout) {
        return new StandardAdapter<>(mActivity, layout);
    }

    public StandardAdapter<ParseObject> getAdapterObjects(int layout) {
        return new StandardAdapter<>(mActivity, layout);
    }

}
