package com.example.nicklawler222.shopping;

/**
 * Created by Eric on 11/25/2014.
 */

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Scanner;

public abstract class HTMLGrab extends AsyncTask<Void, Void, String> {

    private static final String TAG = HTMLGrab.class.getSimpleName();

    private Activity mActivity;
    private Integer moption;
    public static final String UPLOAD_URL = "https://camfind.p.mashape.com/image_requests";
    public static final String DOWN_URL = "https://camfind.p.mashape.com/image_responses/";

    public HTMLGrab( Activity activity) {
        this.mActivity = activity;
    }

    @Override
    protected String doInBackground(Void... params) {

        String result = "";
        InputStream responseIn = null;
        HttpURLConnection urlConnection=null;

        try {
            Log.i(TAG, "before sleep");

            Thread.sleep(2000);
            Log.i(TAG, "after sleep");


            HttpClient httpclient = new DefaultHttpClient();
            String json = "";
            JSONObject holder = new JSONObject();
                         //THIS IS FOR GRABBING THE METADATA FROM THE API TO PARSE LATER
            //url with the post data
            String url = DOWN_URL.trim() + DataHolder.getInstance().getTOKEN().trim();
            HttpGet httget = new HttpGet(url);

            httget.setHeader("Accept", "application/json");
            httget.setHeader("Content-type", "application/json");
            httget.setHeader("X-Mashape-Key", "texfF9WVUCmsh8883iQ9Vsv3bd1Qp1s1nSqjsn0DLOfWCABB3d");
            //sets the post request as the resulting string
            //sets a request header so the page receving the request
            //will know what to do with it

            String completedresult = "not completed";

            while ( completedresult.indexOf("ot compl") != -1 ) {
                Thread.sleep(4000);
                HttpResponse httpResponse = httpclient.execute(httget);

                responseIn = httpResponse.getEntity().getContent();
                StatusLine statusLine = httpResponse.getStatusLine();
                if( statusLine.getStatusCode() != 200 ){
                    result = "not found";
                    return result;
                }
                if (responseIn != null)
                    result = convertInputStreamToString(responseIn);

                completedresult = result;
                Log.i(TAG, "convertinputstream: " + result);
                Log.i(TAG, "status: " + statusLine.getStatusCode() + "   resonse: " + httpResponse.toString());
                Log.i(TAG, httpResponse.toString());
            }
            responseIn.close();
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
