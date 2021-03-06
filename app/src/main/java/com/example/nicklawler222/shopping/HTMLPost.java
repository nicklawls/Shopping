package com.example.nicklawler222.shopping;

/**
 * Created by Eric on 11/25/2014.
 */

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public abstract class HTMLPost extends AsyncTask<Void, Void, String> {

    private static final String TAG = HTMLPost.class.getSimpleName();

    private Activity mActivity;
    public static final String UPLOAD_URL = "https://camfind.p.mashape.com/image_requests";
    public static final String DOWN_URL = "https://camfind.p.mashape.com/image_responses/";

    public HTMLPost( Activity activity) {
        this.mActivity = activity;
    }

    @Override
    protected String doInBackground(Void... params) {

        String result = "";
        InputStream responseIn = null;

        try {
            HttpClient httpclient = new DefaultHttpClient();
            String json = "";
            JSONObject holder = new JSONObject();

            //Connect
            HttpPost httpost = new HttpPost(UPLOAD_URL);

            //Builds the JSON object needed for the API, loads it into the StrinEnt for httpost
            holder.put("language", "en");
            holder.put("locale", "en_US");
            holder.put("remote_image_url", DataHolder.getInstance().getURL());
            json = holder.toString();
            StringEntity se = new StringEntity(json);

            //Set request properties for the json request
            httpost.setEntity(se);
            httpost.setHeader("Accept", "application/json");
            httpost.setHeader("Content-type", "application/json");
            httpost.setHeader("X-Mashape-Key", "texfF9WVUCmsh8883iQ9Vsv3bd1Qp1s1nSqjsn0DLOfWCABB3d");

            //execute the json request and grab the results
            HttpResponse httpResponse = httpclient.execute(httpost);
            responseIn = httpResponse.getEntity().getContent();
            if (responseIn != null)
                result = convertInputStreamToString(responseIn);
            else
                result = "Didnt work";
            responseIn.close();
            se.consumeContent();
            return result;


        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

}
