package com.example.pc_asus.testconnectwebapi.liveVideo;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pc_asus.testconnectwebapi.API;
import com.example.pc_asus.testconnectwebapi.AppUtil;
import com.example.pc_asus.testconnectwebapi.MainActivity;
import com.example.pc_asus.testconnectwebapi.R;
import com.example.pc_asus.testconnectwebapi.liveVideo.CameraSourcePreview;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.Timestamp;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class FaceTrackerActivity extends AppCompatActivity {
    private static final String TAG = "FaceTracker";

    private CameraSource mCameraSource = null;

    private com.example.pc_asus.testconnectwebapi.liveVideo.CameraSourcePreview mPreview;
    //private TextView tv_face;

    private static final int RC_HANDLE_GMS = 9001;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    boolean checkStartCamera=false;

    //==============================================================================================
    // Activity Methods
    //==============================================================================================


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_face_tracker);

        mPreview = (CameraSourcePreview) findViewById(R.id.cameraSource);
        //tv_face = findViewById(R.id.tv_face);



        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
            Log.e("abc","create camera source");
        } else {
            requestCameraPermission();
        }
    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.e("abc", "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

    }


    private void createCameraSource() {

        final int[] faceId={-1};
        final int[] newFaceId = {-1};
        final Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        final MyFaceDetector myFaceDetector = new MyFaceDetector(detector);

        myFaceDetector.setProcessor(
                new MultiProcessor.Builder<>(new MultiProcessor.Factory<Face>() {
                    @Override
                    public Tracker<Face> create(Face face) {
                      //  Log.e("abc","face"+face.getId());

                        newFaceId[0] =face.getId();
                        if(newFaceId[0]!=faceId[0]) {
                            faceId[0]=newFaceId[0];


                            byte[] bytes= MyFaceDetector.jpegArray;
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            bitmap = AppUtil.getResizedBitmap(bitmap, 350, 350);

                       //     AppUtil.Exif.getOrientation(bytes);

                            int orientation = AppUtil.Exif.getOrientation(bytes);
                            Log.e("abc","oriencation "+orientation);
                            Bitmap bitmapPicture = bitmap;
                            //   Bitmap   bitmap3 = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            switch (orientation) {
                                case 90:
                                    bitmapPicture = rotateImage(bitmap, 90);

                                    break;
                                case 180:
                                    bitmapPicture = rotateImage(bitmap, 180);

                                    break;
                                case 270:
                                    bitmapPicture = rotateImage(bitmap, 270);

                                    break;
                                case 0:

                                default:
                                    break;
                            }

                            File f = convertBitmapToFile(bitmapPicture);
                            getDataApi(f);
                            Log.e("abc", "đã chụp");
//                             final CameraSource.PictureCallback mPicture= new CameraSource.PictureCallback() {
//                                @Override
//                                public void onPictureTaken(byte[] bytes) {
//
//                                    Log.e("abc", "đã chụp");
//                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                                        bitmap = AppUtil.getResizedBitmap(bitmap, 350, 350);
//
//                                        AppUtil.Exif.getOrientation(bytes);
//
//                                        int orientation = AppUtil.Exif.getOrientation(bytes);
//                                        Bitmap bitmapPicture = bitmap;
//                                        //   Bitmap   bitmap3 = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                                        switch (orientation) {
//                                            case 90:
//                                                bitmapPicture = rotateImage(bitmap, 90);
//
//                                                break;
//                                            case 180:
//                                                bitmapPicture = rotateImage(bitmap, 180);
//
//                                                break;
//                                            case 270:
//                                                bitmapPicture = rotateImage(bitmap, 270);
//
//                                                break;
//                                            case 0:
//
//                                            default:
//                                                break;
//                                        }
//
//                                        File f = convertBitmapToFile(bitmapPicture);
//                                        getDataApi(f);
//
//
//
//                                    checkStartCamera=true;
//                                }
//                            };
//
//                            if(checkStartCamera==true){
//
//                                mCameraSource.takePicture(null, mPicture);
//
//                                checkStartCamera=false;
//
//
//                            }

                        }
                        return null;
                    }
                })
                        .build());




        if (!myFaceDetector.isOperational()) {
            new AlertDialog.Builder(this)
                    .setMessage("Face detector dependencies are not yet available.")
                    .show();

            Log.e("abc", "Face detector dependencies are not yet available.");
            return;
        }

        mCameraSource = new CameraSource.Builder(context, myFaceDetector)
                .setRequestedPreviewSize(1024, 720)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(30.0f)
                .setAutoFocusEnabled(true)
                .build();
    }


    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();

        startCameraSource();
    }


    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.e("abc", "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.e("abc", "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            createCameraSource();
            return;
        }

        Log.e("abc", "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Face Tracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    //==============================================================================================
    // Camera Source Preview
    //==============================================================================================

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() {

        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource);
                checkStartCamera=true;
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
                checkStartCamera=false;
            }
        }
    }

    private File convertBitmapToFile(Bitmap bitmap) {
        File imageFile = new File(getCacheDir(), "test.jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e("abc", "Error writing bitmap", e);
        }


        return imageFile;
    }


    public void getDataApi(File f){
//        final ProgressDialog dialog= new ProgressDialog(FaceTrackerActivity.this);
    //    dialog.setMessage("         please wait...");
     //   dialog.show();

        Log.e("abc",f.getAbsolutePath()+" "+f.length());
        RequestBody requestBody= RequestBody.create(MediaType.parse("multipart/form-data"),f);
        MultipartBody.Part body= MultipartBody.Part.createFormData("upload_image","/data/test.jpg",requestBody);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API.Base_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        API api= retrofit.create(API.class);

        Call<String> call= api.recognitionFace("kpop",body);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
            //    dialog.dismiss();
                Toast.makeText(FaceTrackerActivity.this, response.body(), Toast.LENGTH_SHORT).show();
                Log.e("abc","result="+response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("abc","lỗi "+t);
                Toast.makeText(FaceTrackerActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
           //     dialog.dismiss();
            }
        });
    }





    
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(),   source.getHeight(), matrix,
                true);
    }

    }




