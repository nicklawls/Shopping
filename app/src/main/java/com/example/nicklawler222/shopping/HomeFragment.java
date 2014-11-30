package com.example.nicklawler222.shopping;

import android.content.ContentUris;
import android.content.Intent;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HomeFragment extends Fragment {
    private static final String TAG = "CallCamera";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQ = 0;
    private String selectedImagePath;
    private String filemanagerstring;

    private Uri mImageUri;
    private String mImgurUrl;
    private MyImgurUploadTask mImgurUploadTask;
    private int mImgurUploadStatus;

    Uri fileUri = null;
    ImageView photoImage = null;

    public HomeFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        photoImage = (ImageView) rootView.findViewById(R.id.photo_image);


        //View rootView = inflater.inflate(R.layout.fragment_browse_history, container, false);

        //new FetchSQL().execute();
        Button callCameraButton = (Button) rootView.findViewById(R.id.button_callcamera);
        callCameraButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = getOutputPhotoFile();
                fileUri = Uri.fromFile(getOutputPhotoFile());
                i.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(i, CAPTURE_IMAGE_ACTIVITY_REQ); //capture image activity req is 0, this differentiates ACTION_IMAGE_CAPTURE from ACTION_GET_CONTENT
            }
        });
        Button fileFromPhone = (Button) rootView.findViewById(R.id.button_filefromphone);
        fileFromPhone.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i,
                        "Select Picture"), 1); //didn't feel like using a cool name thingy for ACTION_GET_CONTENT (not that i wrote the one for the other thingy)
            }
        });


        return rootView;
    }
    private File getOutputPhotoFile() {
        File directory = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "blah");
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                Log.e(TAG, "Failed to create storage directory.");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
        return new File(directory.getPath() + File.separator + "IMG_"
                + timeStamp + ".jpg");
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQ) { //FROM CAMERA
            if (resultCode == -1) {
                Uri photoUri = null;
                if (data == null) {
                    // A known bug here! The image should have saved in fileUri
                    Toast.makeText(getActivity(), "Image saved successfully",
                            Toast.LENGTH_LONG).show();
                    photoUri = fileUri;
                    mImageUri = fileUri;
                } else {
                    photoUri = data.getData();
                    mImageUri = data.getData();
                    Toast.makeText(getActivity(), "Image saved successfully in: " + data.getData(),
                            Toast.LENGTH_LONG).show();
                }
                Log.d("path_gallery",photoUri.getPath()); //THIS IS THE PATH TO THE IMAGE FROM CAMERA
                //ERIC LOOK OVER HERE^//ERIC LOOK OVER HERE^//ERIC LOOK OVER HERE^//ERIC LOOK OVER HERE^//ERIC LOOK OVER HERE^
                showPhoto(photoUri);

            } else if (resultCode == 0) {
                Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Callout for image capture failed!",
                        Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == 1) { //FROM GALLERY
            if (resultCode == -1) {
                Toast.makeText(getActivity(), "supposed to work", Toast.LENGTH_SHORT).show();
                Uri selectedImageUri = data.getData();
                mImageUri = data.getData();
                selectedImagePath = getPath(getActivity().getApplicationContext(),selectedImageUri);
                Log.d("path_gallery",selectedImagePath); //THIS IS THE PATH TO THE IMAGE FROM GALLERY
                //ERIC LOOK OVER HERE^//ERIC LOOK OVER HERE^//ERIC LOOK OVER HERE^//ERIC LOOK OVER HERE^//ERIC LOOK OVER HERE^
                if (selectedImagePath != null) {
                    // Selected image is local image
                    Bitmap bitmap = new BitmapDrawable(this.getResources(),
                            selectedImagePath).getBitmap();
                    int nh = (int) ( bitmap.getHeight() * (512.0 / bitmap.getWidth()) );
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
                    BitmapDrawable drawable = new BitmapDrawable(this.getResources(), scaled);
                    photoImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    photoImage.setImageDrawable(drawable);
                }

            }
            else if (resultCode == 0) {
                Toast.makeText(getActivity(), "qq", Toast.LENGTH_SHORT).show();
            }

        }
        if (mImageUri != null ) {
            selectedImagePath = mImageUri.getPath();
                new MyImgurUploadTask(mImageUri).execute();
        }
    }
    private void showPhoto(Uri photoUri) {
        File imageFile = new File(photoUri.getPath());
        if (imageFile.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            int nh = (int) ( bitmap.getHeight() * (512.0 / bitmap.getWidth()) );
            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
            BitmapDrawable drawable = new BitmapDrawable(this.getResources(), scaled);
            photoImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
            photoImage.setImageDrawable(drawable);
        }
    }
    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @author paulburke
     */
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /*
    Working
    WHEN A IMAGE IS SELECTED THE IMGURUPLOAD TASK STARTS, IT UPLOADS TO IMGUR
        THE URL IS RETURNED FROM THE TASK, WHICH IS PARSED AND SENT TO HTMLPost
    HTML POST gets called and sumbits a JSON node with the data to CamFIND
        ......This is working, the token is returned and parsed and is valid
        ........The token is sent to HTMLGrab

    ISN't working
    HTMLGrab is returning error 404: not found code
    HTMLGrab should make a post that resembles this https://www.mashape.com/imagesearcher/camfind
     */

    private class MyImgurUploadTask extends ImgurUploadTask {
        public MyImgurUploadTask(Uri imageUri) {
            super(imageUri, getActivity());
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mImgurUploadTask != null) {
                boolean cancelled = mImgurUploadTask.cancel(false);
                if (!cancelled)
                    this.cancel(true);
            }
            mImgurUploadTask = this;
            mImgurUrl = null;
        }
        @Override
        protected void onPostExecute(String imageId) {
            super.onPostExecute(imageId);
            mImgurUploadTask = null;

            if (imageId != null) {
                mImgurUrl = "http://imgur.com/" + imageId;
                DataHolder.getInstance().setURL("http://i.imgur.com/" + imageId + ".jpg");
            } else {
                mImgurUrl = "Failed upload.";
            }
            ((TextView) getView().findViewById(R.id.returnedURL)).setText(mImgurUrl);

            new MyHTMLPost().execute();
            if (isVisible()) ;
        }
    }

    private class MyHTMLPost extends HTMLPost {
        public MyHTMLPost() {
            super( 1 ,getActivity());
        }

        @Override
        protected void onPostExecute(String imageId) {
            super.onPostExecute(imageId);

//            ((TextView) getView().findViewById(R.id.returnedURL)).setText(imageId);
            String token = imageId.substring( imageId.indexOf("oken\":")+7, imageId.indexOf("\",\"ur") );
            ((TextView) getView().findViewById(R.id.returnedURL)).setText(token);
            DataHolder.getInstance().setTOKEN(token);

            new MyHTMLGrab().execute();
            if (isVisible()) ;
        }
    }

    private class MyHTMLGrab extends HTMLGrab {
        public MyHTMLGrab() {
            super( getActivity());
        }

        @Override
        protected void onPostExecute(String imageId) {
            super.onPostExecute(imageId);

//            ((TextView) getView().findViewById(R.id.returnedURL)).setText(imageId);
//            String token = imageId.substring( imageId.indexOf("oken\":")+7, imageId.indexOf("\",\"ur") );
            ((TextView) getView().findViewById(R.id.returnedURL)).setText(imageId);

            if (isVisible()) ;
        }
    }





    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}