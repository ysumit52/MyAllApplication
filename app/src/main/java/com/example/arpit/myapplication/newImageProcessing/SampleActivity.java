package com.example.arpit.myapplication.newImageProcessing;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.arpit.myapplication.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SampleActivity extends AppCompatActivity implements OnClickListener {
    private final int RESPONSE_OK = 200;

    private final int IMAGE_PICKER_REQUEST = 1;

    private TextView picNameText;
    private EditText langCodeField;
    private EditText apiKeyFiled;

    final String apiKey ="5978bb78ad88957";
    final String langCode = "eng";
    private String fileName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sample);
        initToolbar();
        picNameText =  findViewById(R.id.imageName);
//        langCodeField =  findViewById(R.id.lanuageCode);
//        apiKeyFiled =  findViewById(R.id.apiKey);

        final Button pickButton =  findViewById(R.id.picImagebutton);
        pickButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Starting image picker activity
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI), IMAGE_PICKER_REQUEST);
            }
        });

        final Button convertButton =  findViewById(R.id.convert);
        convertButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        // Checking are all fields set
        if (fileName != null ) {
            final ProgressDialog dialog = ProgressDialog.show( SampleActivity.this, "Loading ...", "Converting to text.", true, false);
            final Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    final OCRServiceAPI apiClient = new OCRServiceAPI(apiKey);
                    apiClient.convertToText(langCode, fileName);

                    // Doing UI related code in UI thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();

                            // Showing response dialog
                            final AlertDialog.Builder alert = new AlertDialog.Builder(SampleActivity.this);
                            alert.setMessage(apiClient.getResponseText());

                            DialogInterface.OnClickListener dialogInterface = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case DialogInterface.BUTTON_POSITIVE:
                                            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                                            whatsappIntent.setType("text/plain");
                                            whatsappIntent.setPackage("com.whatsapp");
                                            whatsappIntent.putExtra(Intent.EXTRA_TEXT, apiClient.getResponseText());
                                            try {
                                                getApplicationContext().startActivity(whatsappIntent);
                                            } catch (android.content.ActivityNotFoundException ex) {
                                                Toast.makeText(SampleActivity.this, "Whatsapp not installed.", Toast.LENGTH_SHORT).show();
                                            }
                                            break;

                                            case DialogInterface.BUTTON_NEGATIVE:
                                                break;

                                    }
                                }
                            };

                            alert.setPositiveButton("OK",dialogInterface);

                            alert.setNegativeButton("No",dialogInterface);

                            // Setting dialog title related from response code
                            if (apiClient.getResponseCode() == RESPONSE_OK) {
                                alert.setTitle("Success");
                            } else {
                                alert.setTitle("Failed");
                            }

                            alert.show();
                        }
                    });
                }
            });
            thread.start();
        } else {
            Toast.makeText(SampleActivity.this, "All data are required.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICKER_REQUEST && resultCode == RESULT_OK) {
            fileName = getRealPathFromURI(data.getData());
            picNameText.setText("Selected: " + getStringNameFromRealPath(fileName));
        }
    }

    /*
     * Returns image real path.
     */
    private String getRealPathFromURI(final Uri contentUri) {
        final String[] proj = { MediaStore.Images.Media.DATA };
        final Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }

    /*
     * Cuts selected file name from real path to show in screen.
     */
    private String getStringNameFromRealPath(final String bucketName) {
        return bucketName.lastIndexOf('/') > 0 ? bucketName.substring(bucketName.lastIndexOf('/') + 1) : bucketName;
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle(getString(R.string.imageActivity));
        setSupportActionBar(toolbar);
    }
}