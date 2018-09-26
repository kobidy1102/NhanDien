package com.example.pc_asus.testconnectwebapi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;

public class MainActivity extends AppCompatActivity {

    private static final String URL="";
    private Retrofit retrofit;
    Call<String> call;
    ImageView img;
    File f;


    int PICK_IMAGE_MULTIPLE = 1;
    String imageEncoded;
    List<String> imagesEncodedList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_album = findViewById(R.id.btn_album);
        Button btn_camera= findViewById(R.id.btn_camera);
        Button btn_send= findViewById(R.id.btn_send);
         img= findViewById(R.id.imageView);


        btn_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_MULTIPLE);
            }
        });

        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,2);
            }
        });














        Button btn= findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                retrofit = new Retrofit.Builder()
                        .baseUrl(API.Base_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                API api= retrofit.create(API.class);

                call= api.getResult();


      //          new ReadData().execute();

//                try {
//                    String result = call.execute().body();
//                    Log.e("abc", result);
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }



                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Toast.makeText(MainActivity.this, response.body(), Toast.LENGTH_SHORT).show();
                        Log.e("abc"," . "+response.toString()+"-"+ response.body());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e("abc","lỗi "+call+" "+t);
                        Toast.makeText(MainActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });






        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RequestBody requestBody= RequestBody.create(MediaType.parse("multipart/form-data"),f);
                MultipartBody.Part body= MultipartBody.Part.createFormData("upload_image","/data/test.jpg",requestBody);

                retrofit = new Retrofit.Builder()
                        .baseUrl(API.Base_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                API api= retrofit.create(API.class);

                call= api.upLoadPhoto(body);

                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Toast.makeText(MainActivity.this,"result= "+ response.body(), Toast.LENGTH_SHORT).show();
                        Log.e("abc","result="+response.body());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e("abc","lỗi ");
                        Toast.makeText(MainActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });



    }


    private  class ReadData extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String result = call.execute().body();
                Log.e("abcp","a:"+result);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {              //chọn ảnh
            if (data == null) {
                return;
            }
            try {
                Uri uri= data.getData();
                InputStream inputStream = MainActivity.this.getContentResolver().openInputStream(data.getData());
                Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                img.setImageBitmap(bitmap);

                f=convertBitmapToFile(bitmap);
                Log.e("abc","path "+f.getAbsolutePath());



            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
                else if (requestCode == 2 && resultCode == Activity.RESULT_OK && data!=null) {       //camera
            Bitmap bitmap= (Bitmap) data.getExtras().get("data");
            img.setImageBitmap(bitmap);

             f=convertBitmapToFile(bitmap);
            Log.e("abc","path "+f.getAbsolutePath());
        }

            // When an Image is picked
//            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && null != data) {
//                // Get the Image from data
//
//                String[] filePathColumn = { MediaStore.Images.Media.DATA };
//                imagesEncodedList = new ArrayList<String>();
//                if(data.getData()!=null){
//
//                    Uri mImageUri=data.getData();
//
//                    // Get the cursor
//                    Cursor cursor = getContentResolver().query(mImageUri,
//                            filePathColumn, null, null, null);
//                    // Move to first row
//                    cursor.moveToFirst();
//
//                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                    imageEncoded  = cursor.getString(columnIndex);
//                    cursor.close();
//
//                } else {
//                    if (data.getClipData() != null) {
//                        ClipData mClipData = data.getClipData();
//                        ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
//                        for (int i = 0; i < mClipData.getItemCount(); i++) {
//
//                            ClipData.Item item = mClipData.getItemAt(i);
//                            Uri uri = item.getUri();
//                            mArrayUri.add(uri);
//                            // Get the cursor
//                            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
//                            // Move to first row
//                            cursor.moveToFirst();
//
//                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                            imageEncoded  = cursor.getString(columnIndex);
//                            imagesEncodedList.add(imageEncoded);
//                            cursor.close();
//
//                        }
//                        Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
//                    }
//                }
//
//
//        }

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

}
