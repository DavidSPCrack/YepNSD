package es.uem.david.samuel.nacho.yepnsd;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by usuario.apellido on 13/02/2015.
 *
 * @author david.sancho
 */
public class FileUtilities {

    public static final String TAG = FileUtilities.class.getSimpleName();

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    public static final String APP_NAME = "yep";

    public static Uri getOutputMediaFileUri(int mediaType) {
        if (isExternalStorageAvailable()) {
            File mediaStorageDir = null;
            switch (mediaType) {
                case MEDIA_TYPE_IMAGE:
                    mediaStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    break;
                case MEDIA_TYPE_VIDEO:
                    mediaStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
                    break;
            }
            if (mediaStorageDir != null) {
                File appDir = new File(mediaStorageDir, APP_NAME);
                if (!mediaStorageDir.exists()) {
                    Log.d(TAG, appDir.getAbsolutePath() + " not exists");
                    if (!mediaStorageDir.mkdirs()) {
                        Log.d(TAG, "Directory " + appDir.getAbsolutePath() + " not created");
                        return null;
                    }
                }
                String fileName = "";
                Date now = new Date();
                String timestamp = new SimpleDateFormat(
                        "yyyyMMdd_HHmmss", new Locale("es", "ES")).format(now);
                switch (mediaType) {
                    case MEDIA_TYPE_IMAGE:
                        fileName = "IMG_" + timestamp + ".jpg";
                        break;
                    case MEDIA_TYPE_VIDEO:
                        fileName = "VID_" + timestamp + ".mp4";
                        break;
                }

                String pathFile = appDir.getAbsolutePath() +
                        File.separator +
                        fileName;

                File mediaFile = null;
                try {
                    mediaFile = File.createTempFile(
                            fileName,  /* prefix */
                            ".jpg",         /* suffix */
                            mediaStorageDir      /* directory */
                    );
                } catch (IOException e) {
                    return null;
                }
                if (mediaFile != null) {
                    Log.d(TAG, "File : " + mediaFile.getAbsolutePath());

                    return Uri.fromFile(mediaFile);
                }
            }
        }
        return null;
    }

    public static boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


}
