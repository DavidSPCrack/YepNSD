package es.uem.david.samuel.nacho.yepnsd;

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

    protected void doFieldAlertDialog(String field) {
        doAlertDialog(R.string.field_missing, field);
    }

    protected void doAlertDialog(int msgId, String var) {
        String msg = getResourceString(msgId, var);
        doAlertDialog(msg);
    }

    protected void doAlertDialog(int msgId) {
        String msg = getResourceString(msgId);
        doAlertDialog(msg);
    }

    protected void doAlertDialog(Throwable t) {
        String msg = t.getMessage();
        doAlertDialog(msg);
    }

    protected void doAlertDialog(String msg) {
        String title = getResourceString(R.string.alert);
        String button = getResourceString(R.string.alert_button);
        doDialog(title, msg, button);
    }

    protected Resources getResources() {
        return mActivity.getResources();
    }

    protected View findViewById(int id) {
        return mActivity.findViewById(id);
    }

    protected String getResourceString(int id) {
        Resources res = getResources();
        return res.getString(id);
    }

    protected String getResourceString(int id, String var1) {
        String s = getResourceString(id);
        return String.format(s, var1);
    }

    protected void doDialog(String title, String msg, String button) {
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

    protected String getEditTextValue(int id) {
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

    protected String getEditTextHint(int id) {
        View v = findViewById(id);
        if (v != null) {
            if (v instanceof EditText) {
                EditText et = (EditText) v;
                return et.getHint().toString();
            }
        }
        return "";
    }

    protected String getEditTextValueAndValidate(int id) {
        String text = getEditTextValue(id);
        String field = getEditTextHint(id);
        validateNoEmpty(text, field);
        return text;
    }

    protected boolean validateNoEmpty(String text, String field) {
        if (isTextEmpty(text)) {
            doFieldAlertDialog(field);
            return false;
        }
        return true;
    }

    protected boolean isTextEmpty(String s) {
        return s == null || s.isEmpty();
    }

    protected boolean isAnyEmpty(String... s) {
        for (int i = 0; i < s.length; i++) {
            if (isTextEmpty(s[i])) {
                return true;
            }
        }
        return false;
    }

    protected Drawable getDrawableRes(int id) {
        Resources res = getResources();
        return res.getDrawable(id);
    }

    protected ProgressDialog getProgressDialog(int title) {
        return ProgressDialog.show(mActivity,
                getResourceString(title),
                getResourceString(R.string.please_wait), true);
    }

    protected void openNewActivity(Class<?> dstClass) {
        openNewActivity(dstClass, true);
    }

    protected void openNewActivity(Class<?> dstClass, boolean swClearTask) {
        Intent intent = new Intent(mActivity, dstClass);
        if (swClearTask) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        mActivity.startActivity(intent);
    }

    protected void hideProgressDialog(ProgressDialog progressDialog) {
        progressDialog.dismiss();
    }

    protected void hideView(int id) {
        View vFound = findViewById(id);
        if (vFound != null) {
            vFound.setVisibility(View.GONE);
        }
    }
}
