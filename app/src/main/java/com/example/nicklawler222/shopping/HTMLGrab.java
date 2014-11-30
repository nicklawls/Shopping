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
            Thread.sleep(15000);

            HttpClient httpclient = new DefaultHttpClient();
            String json = "";
            JSONObject holder = new JSONObject();
                         //THIS IS FOR GRABBING THE METADATA FROM THE API TO PARSE LATER
            //url with the post data
            String url = DOWN_URL.trim() + DataHolder.getInstance().getTOKEN().trim();
            HttpPost httpost = new HttpPost(url);
            httpost.setHeader("Accept", "application/json");
            httpost.setHeader("Content-type", "application/json");
            httpost.setHeader("X-Mashape-Key", "texfF9WVUCmsh8883iQ9Vsv3bd1Qp1s1nSqjsn0DLOfWCABB3d");

            //convert parameters into JSON object
            //holder.put("X-Mashape-Key", "texfF9WVUCmsh8883iQ9Vsv3bd1Qp1s1nSqjsn0DLOfWCABB3d");
            json = holder.toString();
            //passes the results to a string builder/entity
            StringEntity se = new StringEntity(json);
            Log.i(TAG, "json:" + json + ")");

            //sets the post request as the resulting string
            httpost.setEntity(se);
            //sets a request header so the page receving the request
            //will know what to do with it

            HttpResponse httpResponse = httpclient.execute(httpost);

            responseIn = httpResponse.getEntity().getContent();
            StatusLine statusLine = httpResponse.getStatusLine();

            if (responseIn != null)
                result = convertInputStreamToString(responseIn);
            else
                result = "Didnt work";
            Log.i(TAG, "status: " + statusLine.getStatusCode() + "   resonse: " + httpResponse.toString());
            Log.i(TAG, httpResponse.toString());
            Log.i(TAG, "url: " + url );
            Log.i(TAG, "jsonresponse: " + result + ")");
            responseIn.close();
            se.consumeContent();
            return onInput(responseIn);

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

    protected String onInput(InputStream in) throws Exception {
        StringBuilder sb = new StringBuilder();
        Scanner scanner = new Scanner(in);
        while (scanner.hasNext()) {
            sb.append(scanner.next());
        }

        JSONObject root = new JSONObject(sb.toString());
        String id = root.getJSONObject("data").getString("token");

        Log.i(TAG, "jsonreturn:" + id + ")");
        return id;
    }

}
