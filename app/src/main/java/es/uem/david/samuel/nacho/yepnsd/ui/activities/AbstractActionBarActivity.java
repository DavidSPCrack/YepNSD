package es.uem.david.samuel.nacho.yepnsd.ui.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import es.uem.david.samuel.nacho.yepnsd.R;
import es.uem.david.samuel.nacho.yepnsd.constants.Constantes;
import es.uem.david.samuel.nacho.yepnsd.utils.FileUtilities;
import es.uem.david.samuel.nacho.yepnsd.utils.UtilActivity;

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

    protected void doCameraDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(R.array.camera_choices, mDialogListener());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    protected DialogInterface.OnClickListener mDialogListener() {
        return new DialogInterface.OnClickListener() {
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
        UtilActivity util = getUtil();
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        mMediaUri = FileUtilities.getOutputMediaFileUri(FileUtilities.MEDIA_TYPE_VIDEO);
        if (mMediaUri != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
            startActivityForResult(intent, MAKE_VIDEO_REQUEST);
        } else {
            util.doToast(R.string.error_external_storage);
        }
    }

    private void takePhoto() {
        UtilActivity util = getUtil();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        mMediaUri = FileUtilities.getOutputMediaFileUri(FileUtilities.MEDIA_TYPE_IMAGE);
        if (mMediaUri != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
            startActivityForResult(intent, TAKE_PHOTO_REQUEST);
        } else {
            util.doToast(R.string.error_external_storage);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UtilActivity util = getUtil();
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PHOTO_REQUEST:
                    Intent mediaScantIntentPhoto = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    mediaScantIntentPhoto.setData(mMediaUri);
                    sendBroadcast(mediaScantIntentPhoto);
                    startRecipentsList(Constantes.FileTypes.IMAGE);
                    break;
                case MAKE_VIDEO_REQUEST:
                    Intent mediaScantIntentVideo = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    mediaScantIntentVideo.setData(mMediaUri);
                    sendBroadcast(mediaScantIntentVideo);
                    startRecipentsList(Constantes.FileTypes.VIDEO);
                    break;
                case CHOOSE_PHOTO_REQUEST:
                    if (data != null) {
                        mMediaUri = data.getData();
                        startRecipentsList(Constantes.FileTypes.IMAGE);
                    } else {
                        util.doAlertDialog(R.string.image_not_found);
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
                                util.doAlertDialog(R.string.video_size_limit);
                            } else {
                                startRecipentsList(Constantes.FileTypes.VIDEO);
                            }
                        } catch (FileNotFoundException e) {
                            util.doAlertDialog(R.string.video_not_found);
                        } catch (IOException e) {
                            util.doAlertDialog(R.string.unknow_error);
                        } finally {
                            if (is != null) {
                                try {
                                    is.close();
                                } catch (IOException e) {
                                    util.doAlertDialog(e);
                                }
                            }
                        }
                    } else {
                        util.doAlertDialog(R.string.unknow_error);
                    }
                    break;
            }
        } else if (resultCode != RESULT_CANCELED) {
            util.doAlertDialog(R.string.unknow_error);
        }
    }

    private void startRecipentsList(String fileType) {
        Intent intent = new Intent(this, RecipientsActivity.class);
        intent.setData(mMediaUri);
        intent.putExtra(Constantes.ParseClasses.Messages.KEY_FILE_TYPE, fileType);
        startActivity(intent);
    }

    protected UtilActivity getUtil() {
        return new UtilActivity(this);
    }
}
