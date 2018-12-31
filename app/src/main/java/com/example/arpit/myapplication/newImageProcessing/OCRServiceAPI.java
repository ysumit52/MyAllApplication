package com.example.arpit.myapplication.newImageProcessing;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OCRServiceAPI {
//    public final String SERVICE_URL = "http://api.ocrapiservice.com/1.0/rest/ocr";
    public final String SERVICE_URL = "https://api.ocr.space/parse/image";

    private final String PARAM_FILE = "file";
    private final String PARAM_LANGUAGE = "language";
    private final String PARAM_APIKEY = "apikey";
    private final String PARAM_PROCESS = "isOverlayRequired";
    String jsData;

    private String apiKey;

    private int responseCode;
    private String responseText;
    JSONObject mainObj,mainObj2,mainObj3,mainObj4,mainObj5;
    JSONArray jsonArray,jsonArray2,jsonArray3,jsonArray4,jsonArray5;


    public OCRServiceAPI(final String apiKey) {
        this.apiKey = apiKey;
    }

    /*
     * Convert image to text.
     *
     * @param language The image text language.
     * @param filePath The image absolute file path.
     *
     * @return true if everything went okay and false if there is an error with sending and receiving data.
     */
    public boolean convertToText(final String language, final String filePath) {
        try {
            sendImage(language, filePath);

            return true;
        } catch (ClientProtocolException e) {
            e.printStackTrace();

            return false;
        } catch (IOException e) {
            e.printStackTrace();

            return false;
        }
    }

    /*
     * Send image to OCR service and read response.
     *
     * @param language The image text language.
     * @param filePath The image absolute file path.
     *
     */
    private void sendImage(final String language, final String filePath) throws IOException {
        final HttpClient httpclient = new DefaultHttpClient();
        final HttpPost httppost = new HttpPost(SERVICE_URL);
         String server_response = null;
        final FileBody image = new FileBody(new File(filePath));

        final MultipartEntity reqEntity = new MultipartEntity();
        reqEntity.addPart("file", image);
        reqEntity.addPart("language", new StringBody(language));
        reqEntity.addPart("apikey", new StringBody(getApiKey()));
        reqEntity.addPart("isOverlayRequired",new StringBody("true"));
        reqEntity.addPart("isCreateSearchablePdf",new StringBody("true"));
        reqEntity.addPart("isSearchablePdfHideTextLayer",new StringBody("false"));
        httppost.setEntity(reqEntity);

        final HttpResponse response = httpclient.execute(httppost);
//        final HttpEntity resEntity = response.getEntity();
//        final StringBuilder sb = new StringBuilder();
        StringBuilder stringBuilder = new StringBuilder();


//        if (resEntity != null) {
//            final InputStream stream = resEntity.getContent();
//            byte bytes[] = new byte[4096];
//            int numBytes;
//            while ((numBytes=stream.read(bytes))!=-1) {
//                if (numBytes!=0) {
//                    sb.append(new String(bytes, 0, numBytes));
//                }
//            }
//
//        }
        if(response.getStatusLine().getStatusCode()==200){
            server_response = EntityUtils.toString(response.getEntity());
            Log.e("Server response :", server_response );
        } else {
            Log.e("Server response : ", "Failed to get server response" );
        }

        try {
            mainObj = new JSONObject(server_response);
            jsonArray = mainObj.getJSONArray("ParsedResults");
            String jsonArray6=mainObj.getString("SearchablePDFURL");
            Log.e("Searachable Pdf ",jsonArray6);
            for (int i =0; i<jsonArray.length();i++){
                mainObj2 = jsonArray.getJSONObject(i);
                mainObj3 = mainObj2.getJSONObject("TextOverlay");

                if (mainObj3!=null){
                    jsonArray2 = mainObj3.getJSONArray("Lines");
                    for (int j =0;j<jsonArray2.length();j++){
                        mainObj4 = jsonArray2.getJSONObject(j);
                        if (mainObj4 != null){
                            jsonArray3 = mainObj4.getJSONArray("Words");
                            if (jsonArray3 !=null){
                                for (int k =0; k<jsonArray3.length();k++){
                                    String data = jsonArray3.getJSONObject(k).getString("WordText");

                                    stringBuilder.append(data);
                                    stringBuilder.append("\n");
//                                    if (mainObj2 !=null){
//                                        jsonArray = mainObj2.
//                                    }
                                }
                            }
                        }
                    }
                }

            }
//            jsData = mainObj.getJSONObject("TextOverlay").getString("Lines");
            //jsData = mainObj2.getString("WordText");
            jsData = stringBuilder.toString();

//            JSONObject mainObj = new JSONObject(myString);

        } catch (JSONException e) {
            Log.e("Json Exception : ",e.getMessage());
        }

        setResponseCode(response.getStatusLine().getStatusCode());

       // setResponseText(sb.toString());
        setResponseText(jsData);
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    public String getApiKey() {
        return apiKey;
    }
    public String getProcess() {
        return "true";
    }
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

//private void ddjd{
//        if(mainObj != null){
//            jsonArray = mainObj.getJSONArray("ParsedResults");
//            if(jsonArray != null){
//                for(int i = 0; i < jsonArray.length();i++){
//                    mainObj = null;
//                    mainObj = jsonArray.getJSONObject(i);
//                    if(mainObj != null){
//                        jsonArray = null;
//                        jsonArray = mainObj.getJSONArray("TextOverlay");
//                        if(jsonArray != null){
//                            for(int j = 0; j < jsonArray.length();j++){
//                                mainObj = null;
//                                mainObj = jsonArray.getJSONObject(j);
//                                if(mainObj != null){
////                                        int cat_id = mainObj.getInt("cat_id");
////                                        int pos = mainObj.getInt("position");
////                                        String sku = mainObj.getString("sku");
//                                    jsonArray = null;
//                                    jsonArray = mainObj.getJSONArray("Lines");
//                                    if (jsonArray!=null){
//                                        for (int k= 0;k<jsonArray.length();k++){
//                                            mainObj = null;
//                                            mainObj = jsonArray.getJSONObject(k);
//                                            if (mainObj != null){
//                                                jsonArray = null;
//                                                jsonArray = mainObj.getJSONArray("Words");
//                                                if (jsonArray !=null){
//                                                    for (int l =0 ; l <jsonArray.length();i++){
//                                                        mainObj = null;
//                                                        mainObj
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//    }
}
