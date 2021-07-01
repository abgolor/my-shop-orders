package com.example.myshop;

import android.util.Log;

import androidx.annotation.NonNull;

import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class Controller {
    //TAG to track logs in logcat
    public static final String TAG = "CONTROLLER";

    //WebApp Script URL:
    public static final String WAURL = "https://script.google.com/macros/s/AKfycbxX0U551TlTzCl82pfPlPurg2FcGULrteyIgUEVvFS5bmSkjWSJ-Ruh42sOxsf5bMP8Nw/exec?";

    private static String serverResponse;
    private static Response response;

    public static JSONObject readAllData(){
        try {
            com.squareup.okhttp.OkHttpClient client = new com.squareup.okhttp.OkHttpClient();
            com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                    .url(WAURL + "action=get&link=" + StartUpActivity.sheetUrl)
                    .build();
            response = client.newCall(request).execute();
            return new JSONObject(response.body().string());
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String updateData(String purchaseOrderNumber, ArrayList<String> productNameList,
                                        ArrayList<String> qtyPackedList,
                                        ArrayList<String> productLansList) throws IOException {
        try {
            int totalProductSentToServer = 0;
            OkHttpClient client = new OkHttpClient();

            for (int i = 0; i < productNameList.size(); i++) {
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("action", "update")
                        .addFormDataPart("link", StartUpActivity.sheetUrl)
                        .addFormDataPart("purchase", purchaseOrderNumber)
                        .addFormDataPart("qty", qtyPackedList.get(i))
                        .addFormDataPart("productName", productNameList.get(i))
                        .addFormDataPart("productLans", productLansList.get(i))
                        .build();

                Request request = new Request.Builder()
                        .url(WAURL)
                        .post(requestBody)
                        .build();

                Call call = client.newCall(request);
                okhttp3.Response response = call.execute();
                JSONObject result = new JSONObject(response.body().string());

                if(result != null){
                    String serverResult = result.getString("result");

                    if(serverResult.equals("value updated successfully")){
                        totalProductSentToServer = totalProductSentToServer + 1;
                    }
                }
            }
            serverResponse = "Successfully packed " + totalProductSentToServer + " products";
            return serverResponse;

        } catch (JSONException e){
            Log.e("TAG", "recieving null" + e.getLocalizedMessage());
        }
        return null;
    }
}
