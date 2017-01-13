/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package digify.tv.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.File;
import java.util.UUID;

import digify.tv.db.models.Media;
import digify.tv.db.models.MediaType;

/**
 * A collection of utility methods, all static.
 */
public class Utils {

    /*
     * Making sure public utility methods remain static
     */
    private Utils() {
    }

    /**
     * Returns the screen/display size
     */
    public static Point getDisplaySize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    /**
     * Shows a (long) toast
     */
    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * Shows a (long) toast.
     */
    public static void showToast(Context context, int resourceId) {
        Toast.makeText(context, context.getString(resourceId), Toast.LENGTH_LONG).show();
    }

    public static int convertDpToPixel(Context ctx, int dp) {
        float density = ctx.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    /**
     * Formats time in milliseconds to hh:mm:ss string format.
     */
    public static String formatMillis(int millis) {
        String result = "";
        int hr = millis / 3600000;
        millis %= 3600000;
        int min = millis / 60000;
        millis %= 60000;
        int sec = millis / 1000;
        if (hr > 0) {
            result += hr + ":";
        }
        if (min >= 0) {
            if (min > 9) {
                result += min + ":";
            } else {
                result += "0" + min + ":";
            }
        }
        if (sec > 9) {
            result += sec;
        } else {
            result += "0" + sec;
        }
        return result;
    }

    /**
     * Return pseudo unique ID
     *
     * @return ID
     */
    public static String getUniquePsuedoID() {

        String m_szDevIDShort = "35" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10) + (Build.CPU_ABI.length() % 10) + (Build.DEVICE.length() % 10) + (Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10);

        String serial = null;
        try {
            serial = android.os.Build.class.getField("SERIAL").get(null).toString();

            // Go ahead and return the serial for api => 9
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            // String needs to be initialized
            serial = "serial"; // some value
        }

        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }

    public static String capitalizeString(String string) {
        char[] chars = string.toLowerCase().toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (Character.isWhitespace(chars[i]) || chars[i] == '.' || chars[i] == '\'') { // You can add other chars here
                found = false;
            }
        }
        return String.valueOf(chars);
    }

    /**
     * Checks to see if the device is online before carrying out any operations.
     *
     * @return
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public static String possesed(String word) {
        if ((word.charAt(word.length() - 1) == 's')) {
            word = word + "\'";
        } else
            word = word + "'s";

        return word;
    }

    public static boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show();
            }
            return false;
        }
        return true;
    }

    public static File getMediaFile(Media media, Context context) {

        Integer id = media.getId();
        String mediaType = media.getType();
        String extension = "." + media.getExtension();


        File file = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + context.getPackageName()
                + "/media/" + mediaType + "/" + mediaType + "_" + id + extension);

        File folder = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + context.getPackageName()
                + "/media/" + mediaType);

        File noMedia = new File(folder.getAbsolutePath() + "/.nomedia");


        if (!file.exists()) {
            return null;
        }
        return file;
    }

    /*
    *Used to create a new file. If the file already exists it's deleted.
    * */
    public static File createMediaFile(Media media, Context context) {

        Integer id = media.getId();
        String mediaType = media.getType();
        String extension = "." + media.getExtension();

        File file = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + context.getPackageName()
                + "/media/" + mediaType + "/" + mediaType + "_" + id + extension);

        File folder = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + context.getPackageName()
                + "/media/" + mediaType);

        File noMedia = new File(folder.getAbsolutePath() + "/.nomedia");


        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                return null;
            }
        }


        if (file.exists()) {
            file.delete();
        }
        return file;
    }


    /*
*Used to create a new file. If the file already exists it's deleted.
* */
    public static File createThumbnailFile(Media media, Context context) {

        Integer id = media.getId();
        String mediaType = "thumbnail";
        String extension = ".png";

        File file = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + context.getPackageName()
                + "/media/" + mediaType + "/" + mediaType + "_" + id + extension);

        File folder = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + context.getPackageName()
                + "/media/" + mediaType);

        File noMedia = new File(folder.getAbsolutePath() + "/.nomedia");


        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                return null;
            }
        }


        if (file.exists()) {
            file.delete();
        }
        return file;
    }

    public static File getThumbnailFile(Media media, Context context) {

        Integer id = media.getId();
        String mediaType = "thumbnail";
        String extension = ".png";


        File file = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + context.getPackageName()
                + "/media/" + mediaType + "/" + mediaType + "_" + id + extension);

        File folder = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + context.getPackageName()
                + "/media/" + mediaType);

        File noMedia = new File(folder.getAbsolutePath() + "/.nomedia");


        if (!file.exists()) {
            return null;
        }
        return file;
    }


    public static String returnExtensionByMediaType(String mediaType) {
        if (mediaType.toLowerCase().equals("video"))
            return ".mp4";

        if (mediaType.toLowerCase().equals("picture"))
            return ".jpg";

        return null;
    }

    public static MediaType getStrongMediaType(String mediaType) {
        if (mediaType.toLowerCase().equals("picture"))
            return MediaType.Picture;
        else if (mediaType.toLowerCase().equals("video"))
            return MediaType.Video;

        return null;
    }


}
