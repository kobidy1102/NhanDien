package com.example.pc_asus.testconnectwebapi;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.engine.impl.PicassoEngine;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.internal.entity.IncapableCause;
import com.zhihu.matisse.internal.entity.Item;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TrainingActivity extends AppCompatActivity {
    private ImageAdapter adapter;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    List<Bitmap> arrBitMapImage;
    RecyclerView recyclerView;
    private Retrofit retrofit;
    Call<String> call;
    File mfile;
     EditText edt_name;
     int i=0;
    private ProgressDialog dialog;
     API api;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        ImageView btn_addImage= findViewById(R.id.img_train_addImage);
        edt_name= findViewById(R.id.edt_train_name);

        btn_addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(TrainingActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(TrainingActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                } else {
                    Matisse.from(TrainingActivity.this)
                            .choose(MimeType.ofImage())
                            .countable(true)
                            .maxSelectable(10)
                            .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                            .thumbnailScale(0.85f)
                            .imageEngine(new PicassoEngine())
                            .forResult(1);
                }



            }
        });


        dialog= new ProgressDialog(this);
            dialog.setMessage("         please wait...");
        Button btnSave = findViewById(R.id.btn_train_save);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(edt_name.getText().toString().trim().isEmpty()){
                    Toast.makeText(TrainingActivity.this, "Bạn chưa nhập tên", Toast.LENGTH_SHORT).show();
                }else {
                    dialog.show();
                    addPersontoGroup();
                }

            }
        });










    }



    private  void addPersontoGroup(){


        retrofit = new Retrofit.Builder()
                .baseUrl(API.Base_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final API api = retrofit.create(API.class);

        call = api.addPersontoGroup("kpop",edt_name.getText().toString().trim());

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                Toast.makeText(TrainingActivity.this, "Add person complete: "+response.body(), Toast.LENGTH_SHORT).show();
                Log.e("abc", "result=" + response.body());

                    addFaceToPerson(response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("abc", "lỗi ");
                Toast.makeText(TrainingActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private  void addFaceToPerson(final String personId){

        mfile = convertBitmapToFile(arrBitMapImage.get(i));
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), mfile);
        final MultipartBody.Part body = MultipartBody.Part.createFormData("upload_image", "/data/test.jpg", requestBody);

        retrofit = new Retrofit.Builder()
                .baseUrl(API.Base_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final API api = retrofit.create(API.class);

        call = api.addFaceToPerson("kpop",personId,body);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                Toast.makeText(TrainingActivity.this, response.body() + " "+i, Toast.LENGTH_SHORT).show();
                Log.e("abc", "result=" + response.body());


                i++;
                if(i<arrBitMapImage.size()) {
                    addFaceToPerson(personId);
                }else{
                    trainingPerson();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("abc", "lỗi ");
                Toast.makeText(TrainingActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void trainingPerson(){

        retrofit = new Retrofit.Builder()
                .baseUrl(API.Base_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api= retrofit.create(API.class);
        // api.personName=edt_name.getText().toString().trim();

        call= api.trainingPerson("kpop");

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Toast.makeText(TrainingActivity.this, response.body(), Toast.LENGTH_SHORT).show();
                Log.e("abc","result="+response.body());
                dialog.dismiss();


            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("abc","lỗi ");
                Toast.makeText(TrainingActivity.this, "Lỗi", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Uri> mSelected;
       // List<String> paths;
        arrBitMapImage= new ArrayList<Bitmap>();
        if (requestCode == 1 && resultCode == RESULT_OK) {
            mSelected = Matisse.obtainResult(data);

            for(int i=0;i<mSelected.size();i++) {
                InputStream inputStream = null;
                try {
                    inputStream = TrainingActivity.this.getContentResolver().openInputStream(mSelected.get(i));
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    arrBitMapImage.add(bitmap);



                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
            Log.e("abc","size="+arrBitMapImage.size());

            initRecycleView();

        }
    }



    private void initRecycleView(){
        recyclerView =findViewById(R.id.rv_train_image);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(layoutManager);
        adapter= new ImageAdapter(arrBitMapImage,getApplicationContext());
        recyclerView.setAdapter(adapter);
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

        Log.e("abc","convert="+imageFile.length()+"- listFile="+imageFile.listFiles());

        return imageFile;
    }
}
