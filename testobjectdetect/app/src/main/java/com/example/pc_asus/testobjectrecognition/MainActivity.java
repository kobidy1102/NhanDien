package com.example.pc_asus.testobjectrecognition;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions;
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabel;
import com.google.firebase.ml.vision.cloud.label.FirebaseVisionCloudLabelDetector;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetector;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetectorOptions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.AnalysisResult;
import com.microsoft.projectoxford.vision.contract.Caption;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.ClarifaiResponse;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;

public class MainActivity extends AppCompatActivity {
    ClarifaiClient client;
    //public VisionServiceClient visionServiceClient = new VisionServiceRestClient("865c9dd23dd74757998b995653e04185");
    public VisionServiceClient visionServiceClient = new VisionServiceRestClient("865c9dd23dd74757998b995653e04185", "https://westcentralus.api.cognitive.microsoft.com/vision/v2.0");
    Bitmap mBitmap;
    byte[] byteArray;

    ArrayList<String> arrResultOfMicrosoft= new ArrayList<String>();
    ArrayList<String> arrResultOfGoogle= new ArrayList<String>();
    ArrayList<String> arrResultOfClarifai= new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        client = new ClarifaiBuilder("d63fa757189848da8aa0f1885e93b8b0")
                //  .client(new OkHttpClient()) // OPTIONAL. Allows customization of OkHttp by the user
                .buildSync();

//        ClarifaiResponse<List<ClarifaiOutput<Concept>>> result =
//                 client.getDefaultModels().generalModel().predict()
//                .withInputs(ClarifaiInput.forImage("https://cdn-images-1.medium.com/max/1600/1*T-v8_DZn98dgl3SWp2V0dg.jpeg"))
//                .executeSync();
//
//
//        client.addInputs()
//                .plus(
//                        ClarifaiInput.forImage("https://cdn-images-1.medium.com/max/1600/1*T-v8_DZn98dgl3SWp2V0dg.jpeg"),
//                        ClarifaiInput.forImage("https://cdn-images-1.medium.com/max/1600/1*T-v8_DZn98dgl3SWp2V0dg.jpeg")
//                )
//                .executeSync();
        //    new GetData().execute();

         mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.smile);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        byteArray = outputStream.toByteArray();

        Button btn = findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Bitmap bm  = BitmapFactory.decodeResource(getResources(), R.mipmap.nghe);
//                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//                bm.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
//                final ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

                new getDataMicrosoft().execute(inputStream);
            }
        });


        Button btn2 = findViewById(R.id.btn2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               getDataGoogle(mBitmap);
            }
        });

        Button btn3 = findViewById(R.id.btn3);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new GetDataClarifai().execute("http://www.trungnguyencorp.com.vn/uploads/images/butgosua-hrs2-6.jpg");
            }
        });


        Button btn4 = findViewById(R.id.btn4);
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SplitResultIntoThreeCharacters(arrResultOfMicrosoft);
                SplitResultIntoThreeCharacters(arrResultOfClarifai);
                FilterResult(SplitResultIntoThreeCharacters(arrResultOfMicrosoft),SplitResultIntoThreeCharacters(arrResultOfClarifai));
            }
        });

    }


    class GetDataClarifai extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... str) {
            ClarifaiResponse<List<ClarifaiOutput<Concept>>> result =
                    client.getDefaultModels().generalModel().predict()

                            .withInputs(ClarifaiInput.forImage(byteArray))
                        //    .withInputs(ClarifaiInput.forImage(str[0]))
                            .executeSync();


            //   Log.e("abc","kq="+result.rawBody().toString());
            return result.rawBody().toString();

        }

        @Override
        protected void onPostExecute(String str) {
            // super.onPostExecute(str);

            try {
                JSONObject j1 = new JSONObject(str);
                JSONArray j2 = j1.getJSONArray("outputs");
                JSONObject j3 = j2.getJSONObject(0);

                JSONObject j4 = j3.getJSONObject("data");
                JSONArray j5 = j4.getJSONArray("concepts");

                for (int i = 0; i < j5.length(); i++) {
                    JSONObject j6 = j5.getJSONObject(i);
                    String name = j6.getString("name");
                    arrResultOfClarifai.add(name);

                    Log.e("abc", "" + name);
                }


            } catch (JSONException e) {
                Log.e("abc","lỗi getdata clarifai");
                e.printStackTrace();
            }

        }
    }


    class getDataMicrosoft extends AsyncTask<InputStream, String, String> {

        ProgressDialog mDialog = new ProgressDialog(MainActivity.this);

        @Override
        protected String doInBackground(InputStream... params) {
            try {
                publishProgress("Recognizing....");
                String[] features = {"Description", "Tags", "Categories"};
                //  String[] features = {"ImageType", "Color", "Faces", "Adult", "Categories"};
                String[] details = {};

                AnalysisResult result = visionServiceClient.analyzeImage(params[0], features, details);


                String strResult = new Gson().toJson(result);
                return strResult;

            } catch (Exception e) {
                return e.getLocalizedMessage();
            }
        }

        @Override
        protected void onPreExecute() {
            mDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            mDialog.dismiss();

            AnalysisResult result = new Gson().fromJson(s, AnalysisResult.class);
            try {
                String descrip = result.description.captions.get(0).text;

                Log.e("abc", "descrip: " + descrip);
            } catch (Exception e) { }


            try {
                int tagsDesSize = result.description.tags.size();
                for (int i = 0; i < tagsDesSize; i++) {
                    Log.e("abc", "descrip tags: " + result.description.tags.get(i).toString());
                    arrResultOfMicrosoft.add(result.description.tags.get(i).toString());
                }

            } catch (Exception e) { }


//            try {
//            int tagsSize = result.tags.size();
//            for (int i = 0; i < tagsSize; i++) {
//                Log.e("abc", "tags: " + result.tags.get(i).name + " -- " + result.tags.get(i).confidence);
//            }
//
//            } catch (Exception e) { }
//
//
//            try {
//            int type = result.categories.size();
//            if (type > 0) {
//                for (int i = 0; i < type; i++) {
//                    Log.e("abc", "categories: " + result.categories.get(i).name);
//                }
//            }
//            } catch (Exception e) { }
        }
    }






    private  void getDataGoogle(Bitmap bitmap){
        final ProgressDialog dialog= new ProgressDialog(this);
        dialog.show();
        FirebaseVisionImage image= FirebaseVisionImage.fromBitmap(bitmap);



        FirebaseVisionCloudDetectorOptions options= new FirebaseVisionCloudDetectorOptions.Builder()
                                                        .setMaxResults(10)
                                                        .build();
        FirebaseVisionCloudLabelDetector detector= FirebaseVision.getInstance().getVisionCloudLabelDetector(options);
        detector.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionCloudLabel>>() {
            @Override
            public void onSuccess(List<FirebaseVisionCloudLabel> firebaseVisionCloudLabels) {

                for(int i=0; i<firebaseVisionCloudLabels.size();i++){
                    Log.e("abc"," ket qua on: "+firebaseVisionCloudLabels.get(i).getLabel().toString());
                    arrResultOfGoogle.add(firebaseVisionCloudLabels.get(i).getLabel().toString());
                }
                dialog.dismiss();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.e("abc","lỗi gg:"+e);

                        dialog.dismiss();
                    }
                });






        FirebaseVisionLabelDetectorOptions options2= new FirebaseVisionLabelDetectorOptions.Builder()
                .setConfidenceThreshold(0.5f)
                .build();
        FirebaseVisionLabelDetector detector2= FirebaseVision.getInstance().getVisionLabelDetector(options2);

        detector2.detectInImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionLabel>>() {
            @Override
            public void onSuccess(List<FirebaseVisionLabel> firebaseVisionLabels) {

                for(int i=0; i<firebaseVisionLabels.size();i++){
                    Log.e("abc"," ket qua off: "+firebaseVisionLabels.get(i).getLabel().toString());
                }
                dialog.dismiss();

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.e("abc","lỗi off  gg:"+e);

                        dialog.dismiss();
                    }
                });


    }












    private ArrayList<ResultThreeCharacters>  SplitResultIntoThreeCharacters(ArrayList<String> A){

        ArrayList<ResultThreeCharacters> arrResult= new ArrayList<ResultThreeCharacters>();
        for(int i=0;i<A.size();i++){
            if(A.get(i).length()>4) {

                for (int j = 0; j < A.get(i).length() - 3; j++) {

                    arrResult.add(new ResultThreeCharacters(A.get(i).substring(j, j + 4), i));

                }
            }else  arrResult.add(new ResultThreeCharacters(A.get(i), i));
        }


        return arrResult;
    }



    private void FilterResult(ArrayList<ResultThreeCharacters> A, ArrayList<ResultThreeCharacters> B){

        for(int i=0;i<A.size();i++){

            for(int j=0;j<B.size();j++){

                if(A.get(i).result.equalsIgnoreCase(B.get(j).result)){

                    Log.e("abc","3 kí từ giống: "+A.get(i).result+ " từ được chọn: "+ arrResultOfMicrosoft.get(A.get(i).index) +" vs "+ arrResultOfClarifai.get(B.get(j).index));
                }

            }

        }

    }







}
