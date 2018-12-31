package com.example.arpit.myapplication.imageProcessing;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.arpit.myapplication.GetImagesFilePath;
import com.example.arpit.myapplication.R;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ImageProcessing extends AppCompatActivity implements IOCRCallBack {
    ImageView imageView;
    TextView fileText;
    int PICK_IMAGE_REQUEST = 1;
    Button button, selectImage, btnCallAPI;
    TextRecognizer recognizer;
    Bitmap bitmap;
    ArrayList<String> list;
    ListView listView;

    String url = "https://api.ocr.space/parse/image";
    private String mAPiKey = "5978bb78ad88957"; //TODO Add your own Registered API key
    private boolean isOverlayRequired;
    private String mImageUrl;
    private String mLanguage;
    private TextView mTxtResult;
    private IOCRCallBack mIOCRCallBack;
    private String selectedImagePath;
    private Uri imageUri;
    private File finalFile;
    private static final String TAG = ImageProcessing.class.getName();
    private Button btnRequest;

    private RequestQueue mRequestQueue;
    private JsonObjectRequest mStringRequest;
    public GetImagesFilePath getImagesFilePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_processing);
        initToolbar();
        imageView = findViewById(R.id.imageProcess);
        fileText  = findViewById(R.id.imageFileText);
//        button = findViewById(R.id.imageButton);
        selectImage = findViewById(R.id.imageSelect);
        mTxtResult = findViewById(R.id.actual_result);
        btnCallAPI = findViewById(R.id.btn_call_api);
        mIOCRCallBack = (IOCRCallBack) this;
        mImageUrl = "http://dl.a9t9.com/blog/ocr-online/screenshot.jpg"; // Image url to apply OCR API
        mLanguage = "eng"; //Language
        isOverlayRequired = true;


//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(bitmap!=null) {
//
//                    //getTextFromImage(bitmap);
//
//                }
//                else Toast.makeText(getApplicationContext(),"Please select Image",Toast.LENGTH_SHORT).show();
//            }
//        });
       // listView = findViewById(R.id.idRecyclerViewHorizontalList);
        btnCallAPI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                Bitmap bitmap2 = bitmap;
                String uploadImage = getStringImage(bitmap2);
//                init(uploadImage);
                //Toast.makeText(getApplicationContext(),uploadImage,Toast.LENGTH_SHORT).show();
                sendAndRequestResponse(uploadImage);
            }
        });


       selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

           imageUri = data.getData();
////           selectedImagePath = getPath(imageUri);
//           String filePath = getRealPathFromURI(imageUri);
//            String file_extn = filePath.substring(filePath.lastIndexOf(".") + 1);

            getImagesFilePath = new GetImagesFilePath(getApplicationContext());
            String filePath = getImagesFilePath.getPath(imageUri);
            fileText.setText(filePath +"no");
//            try {
//                Bitmap b= MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
//                // Log.d(TAG, String.valueOf(bitmap));
//                 bitmap = Bitmap.createScaledBitmap(b,400,400,false);
//
//                imageView.setImageBitmap(bitmap);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }

    public String getRealPathFromURI(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String fileUrl =cursor.getString(column_index);
        return fileUrl;
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
        return encodedImage;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

//    public String getRealPathFromURI(Uri uri) {
//        String path = "";
//        if (getContentResolver() != null) {
//            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
//            if (cursor != null) {
//                cursor.moveToFirst();
//                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
//                path = cursor.getString(idx);
//                cursor.close();
//            }
//        }
//        return path;
//    }
//    public void getTextFromImage(Bitmap bitmap){
//        //Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.font);
//        //B
//        recognizer =  new TextRecognizer.Builder(getApplicationContext()).build();
//
//        if (!recognizer.isOperational()){
//            Toast.makeText(getApplicationContext(),"Could not get the text",Toast.LENGTH_SHORT).show();
//        }
//        else {
//            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
//            SparseArray<TextBlock> items = recognizer.detect(frame);
//             list = new ArrayList<>();
//
//            for (int i =0;i<items.size();i++){
//                TextBlock myItems = items.valueAt(i);
//                list.add(myItems.getValue());
//            }
//
//           // textView.setText(list.toString());
//            ArrayAdapter adapter = new ArrayAdapter(this,R.layout.horizontal_list_item,R.id.idProductName,list);
//            listView.setAdapter(adapter);
//        }
//
//
//    }

    public String getPath(Uri uri) {
        if( uri == null ) {
            return null;
        }
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;

        }
        // this is our fallback here
        return uri.getPath();
    }
    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle(getString(R.string.imageActivity));
        setSupportActionBar(toolbar);
    }

    private void init(String imageData) {

//
                    OCRAsyncTask oCRAsyncTask = new OCRAsyncTask(ImageProcessing.this, mAPiKey, isOverlayRequired, imageData, mLanguage,mIOCRCallBack);
                    oCRAsyncTask.execute();
//        String dat = imageData;
//
//        Uri tempUri = getImageUri(getApplicationContext(), bitmap);
//        File finalFile = new File(getRealPathFromURI(tempUri));
//        if(finalFile != null){
//            Toast.makeText(getApplicationContext(),finalFile.getAbsolutePath().toString(),Toast.LENGTH_SHORT).show();
//        }
//        else Toast.makeText(getApplicationContext(),"No file",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getOCRCallBackResult(String response) {
        mTxtResult.setText(response);
    }

    private void sendAndRequestResponse(String img) {

        //RequestQueue initialized
        mRequestQueue = Volley.newRequestQueue(this);
        String imageData = "data:image/png;base64,"+img.trim();

        JSONObject postDataParams = new JSONObject();

        try {
            //postDataParams.put("apikey", mAPiKey);//TODO Add your Registered API key
            postDataParams.put("isOverlayRequired", isOverlayRequired);
            postDataParams.put("base64Image", imageData);
            postDataParams.put("language", "eng");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //String Request initialized
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postDataParams,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mTxtResult.setText(response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mTxtResult.setText(error.toString());
            }
        }){
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("apikey", mAPiKey);
                return headers;
            }
        };
        //jsonObjectRequest.setTag(REQ_TAG);
        mRequestQueue.add(jsonObjectRequest);

    }


}
