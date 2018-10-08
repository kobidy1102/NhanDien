package com.example.pc_asus.testconnectwebapi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * --> Created by phong.nguyen@beesightsoft.com on 3/12/18.
 */

public class AppUtil {
    private static final int BUFFER_SIZE = 1024 * 4;

    public static final long SECOND_MILLIS = 1000;
    public static final long MINUTE_MILLIS = 60 * SECOND_MILLIS;
    public static final long HOUR_MILLIS = 60 * MINUTE_MILLIS;
    public static final long DAY_MILLIS = 24 * HOUR_MILLIS;
    public static final long WEEK_MILLIS = 7 * DAY_MILLIS;
    public static final long MONTH_MILLIS = 30 * DAY_MILLIS;
    private static final long YEAR_MILLIS = 365 * DAY_MILLIS;



    public static void hideKeyBoard(View currentView, Context context) {
        if (currentView != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(currentView.getWindowToken(), 0);
            }
        }
    }

    public static void showKeyBoard(View currentView, Context context) {
        if (currentView != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.showSoftInput(currentView, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }

    public static boolean isAllowGetLocation(Context context) {
        return ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public static int dp2px(Context context, float dpValue) {
        return (int) (dpValue * context.getResources().getDisplayMetrics().density);
    }

    public static Bitmap rotateImage(String imagePath, int maxWidth, int maxHeight) {
        if (!TextUtils.isEmpty(imagePath)) {
            Matrix matrix = new Matrix();
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imagePath, bitmapOptions);
            getResizedBitmapOptions(bitmapOptions, maxWidth, maxHeight);
            bitmapOptions.inJustDecodeBounds = false;
            Bitmap srcBitmap = BitmapFactory.decodeFile(imagePath, bitmapOptions);
            try {
                ExifInterface exifInterface = new ExifInterface(imagePath);
                int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        matrix.postRotate(90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        matrix.postRotate(180);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        matrix.postRotate(270);
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return srcBitmap == null ? null
                    : Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
                    srcBitmap.getHeight(), matrix, true);
        }
        return null;
    }

    private static void getResizedBitmapOptions(BitmapFactory.Options bitmapOptions, int maxWidth, int maxHeight) {
        int inSampleSize = 0;
        int width = bitmapOptions.outWidth;
        int height = bitmapOptions.outHeight;

        float ratio = (float) width / (float) height;
        if (maxWidth != 0 && maxHeight != 0) {
            if (ratio > 1) {
                if (width > maxWidth) { //prevent zoom image
                    inSampleSize = width / maxWidth;
                    width = maxWidth;
                    height = (int) (width / ratio);
                }
            } else {
                if (height > maxHeight) { //prevent zoom image
                    inSampleSize = height / maxHeight;
                    height = maxHeight;
                    width = (int) (height * ratio);
                }
            }
        } else {
            if (maxWidth == 0) {
                if (height > maxHeight) { //prevent zoom image
                    inSampleSize = height / maxHeight;
                    height = maxHeight;
                    width = (int) (height * ratio);
                }
            } else {
                if (width > maxWidth) { //prevent zoom image
                    inSampleSize = width / maxWidth;
                    width = maxWidth;
                    height = (int) (width / ratio);
                }
            }
        }
        bitmapOptions.outWidth = width;
        bitmapOptions.outHeight = height;
        bitmapOptions.inSampleSize = inSampleSize;
    }

    /**
     * Handle loading image
     */


    public static String splitCamelCase(String s) {
        return s.replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"
                ),
                " "
        );
    }



    public static Bitmap getResizedBitmap(Bitmap srcBitmap, int maxWidth, int maxHeight) {
        int width = srcBitmap.getWidth();
        int height = srcBitmap.getHeight();

        float ratio = (float) width / (float) height;
        if (maxWidth != 0 && maxHeight != 0) {
            if (ratio > 1) {
                if (width > maxWidth) { //prevent zoom image
                    width = maxWidth;
                    height = (int) (width / ratio);
                }
            } else {
                if (height > maxHeight) { //prevent zoom image
                    height = maxHeight;
                    width = (int) (height * ratio);
                }
            }
        } else {
            if (maxWidth == 0) {
                if (height > maxHeight) { //prevent zoom image
                    height = maxHeight;
                    width = (int) (height * ratio);
                }
            } else {
                if (width > maxWidth) { //prevent zoom image
                    width = maxWidth;
                    height = (int) (width / ratio);
                }
            }
        }
        return Bitmap.createScaledBitmap(srcBitmap, width, height, true);
    }

    public static File createTempFileFromBitmap(Bitmap bitmapToUpload) {
        File tempFile = null;

        if (bitmapToUpload != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmapToUpload.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            ByteArrayInputStream byteArrayInputStream
                    = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

            try {
                tempFile = File.createTempFile("temp", ".jpg");
                tempFile.deleteOnExit();
                FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
                copy(byteArrayInputStream, fileOutputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return tempFile;
    }

    private static void copy(InputStream in, OutputStream out)
            throws IOException {
        final byte[] buf = new byte[BUFFER_SIZE];
        int n;
        while ((n = in.read(buf)) > -1) {
            out.write(buf, 0, n);
        }
    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
                for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                    if (processInfo.importance ==
                            ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        for (String activeProcess : processInfo.pkgList) {
                            if (activeProcess.equals(context.getPackageName())) {
                                isInBackground = false;
                            }
                        }
                    }
                }
            } else {
                List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
                ComponentName componentInfo = taskInfo.get(0).topActivity;
                if (componentInfo.getPackageName().equals(context.getPackageName())) {
                    isInBackground = false;
                }
            }
        }
        return isInBackground;
    }




    public static class Exif {
        private static final String TAG = "CameraExif";

        // Returns the degrees in clockwise. Values are 0, 90, 180, or 270.
        public static int getOrientation(byte[] jpeg) {
            if (jpeg == null) {
                return 0;
            }

            int offset = 0;
            int length = 0;

            // ISO/IEC 10918-1:1993(E)
            while (offset + 3 < jpeg.length && (jpeg[offset++] & 0xFF) == 0xFF) {
                int marker = jpeg[offset] & 0xFF;

                // Check if the marker is a padding.
                if (marker == 0xFF) {
                    continue;
                }
                offset++;

                // Check if the marker is SOI or TEM.
                if (marker == 0xD8 || marker == 0x01) {
                    continue;
                }
                // Check if the marker is EOI or SOS.
                if (marker == 0xD9 || marker == 0xDA) {
                    break;
                }

                // Get the length and check if it is reasonable.
                length = pack(jpeg, offset, 2, false);
                if (length < 2 || offset + length > jpeg.length) {
                    Log.e(TAG, "Invalid length");
                    return 0;
                }

                // Break if the marker is EXIF in APP1.
                if (marker == 0xE1 && length >= 8 &&
                        pack(jpeg, offset + 2, 4, false) == 0x45786966 &&
                        pack(jpeg, offset + 6, 2, false) == 0) {
                    offset += 8;
                    length -= 8;
                    break;
                }

                // Skip other markers.
                offset += length;
                length = 0;
            }

            // JEITA CP-3451 Exif Version 2.2
            if (length > 8) {
                // Identify the byte order.
                int tag = pack(jpeg, offset, 4, false);
                if (tag != 0x49492A00 && tag != 0x4D4D002A) {
                    Log.e(TAG, "Invalid byte order");
                    return 0;
                }
                boolean littleEndian = (tag == 0x49492A00);

                // Get the offset and check if it is reasonable.
                int count = pack(jpeg, offset + 4, 4, littleEndian) + 2;
                if (count < 10 || count > length) {
                    Log.e(TAG, "Invalid offset");
                    return 0;
                }
                offset += count;
                length -= count;

                // Get the count and go through all the elements.
                count = pack(jpeg, offset - 2, 2, littleEndian);
                while (count-- > 0 && length >= 12) {
                    // Get the tag and check if it is orientation.
                    tag = pack(jpeg, offset, 2, littleEndian);
                    if (tag == 0x0112) {
                        // We do not really care about type and count, do we?
                        int orientation = pack(jpeg, offset + 8, 2, littleEndian);
                        switch (orientation) {
                            case 1:
                                return 0;
                            case 3:
                                return 180;
                            case 6:
                                return 90;
                            case 8:
                                return 270;
                        }
                        Log.i(TAG, "Unsupported orientation");
                        return 0;
                    }
                    offset += 12;
                    length -= 12;
                }
            }

            Log.i(TAG, "Orientation not found");
            return 0;
        }

        private  static int pack(byte[] bytes, int offset, int length,
                                boolean littleEndian) {
            int step = 1;
            if (littleEndian) {
                offset += length - 1;
                step = -1;
            }

            int value = 0;
            while (length-- > 0) {
                value = (value << 8) | (bytes[offset] & 0xFF);
                offset += step;
            }
            return value;
        }
    }


}
