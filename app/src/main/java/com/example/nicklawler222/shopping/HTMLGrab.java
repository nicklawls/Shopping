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

            Thread.sleep(6000);
            Log.i(TAG, "after sleep");

//            URL url;
//            url = new URL ( DOWN_URL.trim() + DataHolder.getInstance().getTOKEN().trim() );
//            Log.i(TAG, "toconnect " + url.toString());
//
//            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
//            Log.i(TAG, "afterconnection " + urlConn.toString());
//
//            urlConn.setRequestMethod("POST");
//            urlConn.setDoInput (true);
//            urlConn.setDoOutput(true);
//            urlConn.setUseCaches(false);
//            urlConn.setRequestProperty("Content-Type", "application/json");
//            urlConn.setRequestProperty("Accept","application/json");
//            urlConn.setRequestProperty("X-Mashape-Key", "texfF9WVUCmsh8883iQ9Vsv3bd1Qp1s1nSqjsn0DLOfWCABB3d");
//            urlConn.connect();
////Create JSONObject here
//            JSONObject jsonParam = new JSONObject();
//            jsonParam.put("X-Mashape-Key", "texfF9WVUCmsh8883iQ9Vsv3bd1Qp1s1nSqjsn0DLOfWCABB3d");
//            Log.i(TAG, "urlconn: " + urlConn.getContent().toString());
//
//            DataOutputStream printout = new DataOutputStream(urlConn.getOutputStream ());
//
//            printout.writeBytes(jsonParam.toString());
//            printout.flush ();
//            printout.close ();
//
//            DataInputStream input = new DataInputStream(urlConn.getInputStream());
//
//            BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(input));
//            String line = "";
//            StringBuilder stringBuilder = new StringBuilder();
//            while ((line = responseStreamReader.readLine()) != null)
//            {
//                stringBuilder.append(line).append("\n");
//            }
//            responseStreamReader.close();
//
//            String response = stringBuilder.toString();
//            result = response;
//            input.close();
//            urlConn.disconnect();
//            Log.i(TAG, "response: " + response);
//            return response;


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
            HttpResponse httpResponse = httpclient.execute(httget);

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
