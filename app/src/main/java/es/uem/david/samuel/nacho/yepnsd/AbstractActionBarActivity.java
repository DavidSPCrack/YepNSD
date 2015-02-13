package es.uem.david.samuel.nacho.yepnsd;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by usuario.apellido on 23/01/2015.
 *
 * @author david.sancho
 */
public abstract class AbstractActionBarActivity extends ActionBarActivity {

    public final static int TAKE_PHOTO_REQUEST = 0;
    public final static int MAKE_VIDEO_REQUEST = 1;
    public final static int CHOOSE_PHOTO_REQUEST = 2;
    public final static int CHOOSE_VIDEO_REQUEST = 3;

    public final static int FILE_SIZE_LIMIT = 10 * 1024 * 1024;

    private Uri mMediaUri;

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
            doAlertDialog(field);
            return false;
        }
        return true;
    }

    protected String getResourceString(int id) {
        Resources res = getResources();
        return res.getString(id);
    }

    protected String getResourceString(int id, String var1) {
        String s = getResourceString(id);
        return String.format(s, var1);
    }

    protected boolean isTextEmpty(String s) {
        return s == null || s.isEmpty();
    }

    protected void doAlertDialog(String field) {
        String title = getResourceString(R.string.alert);
        String msg = getResourceString(R.string.field_missing, field);
        String button = getResourceString(R.string.alert_button);
        doDialog(title, msg, button);
    }

    protected void doDialog(String title, String msg, String button) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, button,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    protected ProgressDialog getProgressDialog(int title) {
        return ProgressDialog.show(this,
                getString(title),
                getString(R.string.please_wait), true);
    }

    protected boolean isAnyEmpty(String... s) {
        for (int i = 0; i < s.length; i++) {
            if (isTextEmpty(s[i])) {
                return true;
            }
        }
        return false;
    }

    protected void openNewActivity(Class<?> dstClass) {
        openNewActivity(dstClass, true);
    }

    protected void openNewActivity(Class<?> dstClass, boolean swClearTask) {
        Intent intent = new Intent(this, dstClass);
        if (swClearTask) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        startActivity(intent);
    }

    protected Drawable getDrawableRes(int id) {
        Resources res = getResources();
        return res.getDrawable(id);
    }

    protected void doCameraDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(R.array.camera_choices, mDialogListener());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    protected DialogInterface.OnClickListener mDialogListener() {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case Constantes.CameraActions.TAKE_PHOTO:
                        takePhoto();
                        break;
                    case Constantes.CameraActions.MAKE_VIDEO:
                        makeVideo();
                        break;
                    case Constantes.CameraActions.CHOOSE_PHOTO:
                        choosePhoto();
                        break;
                    case Constantes.CameraActions.CHOOSE_VIDEO:
                        chooseVideo();
                        break;
                }
            }
        };
        return listener;
    }

    private void chooseVideo() {
        Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
        choosePhotoIntent.setType("video/*");
        startActivityForResult(choosePhotoIntent, CHOOSE_VIDEO_REQUEST);
    }

    private void choosePhoto() {
        Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
        choosePhotoIntent.setType("image/*");
        startActivityForResult(choosePhotoIntent, CHOOSE_PHOTO_REQUEST);
    }

    private void makeVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        mMediaUri = FileUtilities.getOutputMediaFileUri(FileUtilities.MEDIA_TYPE_VIDEO);
        if (mMediaUri != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
            startActivityForResult(intent, MAKE_VIDEO_REQUEST);
        } else {
            Toast.makeText(this, "Error in external storage", Toast.LENGTH_SHORT).show();
        }
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        mMediaUri = FileUtilities.getOutputMediaFileUri(FileUtilities.MEDIA_TYPE_IMAGE);
        if (mMediaUri != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
            startActivityForResult(intent, TAKE_PHOTO_REQUEST);
        } else {
            Toast.makeText(this, "Error in external storage", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PHOTO_REQUEST:
                    Intent mediaScantIntentPhoto = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    mediaScantIntentPhoto.setData(mMediaUri);
                    sendBroadcast(mediaScantIntentPhoto);
                    break;
                case MAKE_VIDEO_REQUEST:
                    Intent mediaScantIntentVideo = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    mediaScantIntentVideo.setData(mMediaUri);
                    sendBroadcast(mediaScantIntentVideo);
                    break;
                case CHOOSE_PHOTO_REQUEST:
                    if (data != null) {
                        mMediaUri = data.getData();
                    } else {
                        // TODO Mensaje de error
                    }
                    break;
                case CHOOSE_VIDEO_REQUEST:
                    if (data != null) {
                        mMediaUri = data.getData();
                        InputStream is = null;
                        try {
                            is = getContentResolver().openInputStream(mMediaUri);
                            int fileSize = is.available();
                            if (fileSize > FILE_SIZE_LIMIT) {
                                String title = getResourceString(R.string.alert);
                                String msg = getString(R.string.video_size_limit);
                                String button = getResourceString(R.string.alert_button);
                                doDialog(title, msg, button);
                            }
                        } catch (FileNotFoundException e) {
                            // TODO Mensaje de error
                        } catch (IOException e) {
                            // TODO Mensaje de error
                        } finally {
                            if (is != null) {
                                try {
                                    is.close();
                                } catch (IOException e) {
                                }
                            }
                        }
                    } else {
                        // TODO Mensaje de error
                    }
                    break;
            }
        } else if (resultCode != RESULT_CANCELED) {
            //TODO Mensaje de error
        }
    }
}
