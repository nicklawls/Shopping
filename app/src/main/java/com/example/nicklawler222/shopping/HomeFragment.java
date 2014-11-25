package com.example.nicklawler222.shopping;

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
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
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
                } else {
                    photoUri = data.getData();
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
                Log.d("bitmap", selectedImageUri.getScheme());
                selectedImagePath = getPath(selectedImageUri);
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
                Log.d("path_gallery",selectedImagePath); //THIS IS THE PATH TO THE IMAGE FROM GALLERY
                //ERIC LOOK OVER HERE^//ERIC LOOK OVER HERE^//ERIC LOOK OVER HERE^//ERIC LOOK OVER HERE^//ERIC LOOK OVER HERE^
            }
            else if (resultCode == 0) {
                Toast.makeText(getActivity(), "qq", Toast.LENGTH_SHORT).show();
            }

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
    public String getPath(Uri uri) {
        String selectedImagePath;
        //1:MEDIA GALLERY --- query from MediaStore.Images.Media.DATA
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if(cursor != null){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            selectedImagePath = cursor.getString(column_index);
        }else{
            selectedImagePath = null;
        }

        if(selectedImagePath == null){
            //2:OI FILE Manager --- call method: uri.getPath()
            selectedImagePath = uri.getPath();
        }
        return selectedImagePath;
    }
}
//    private class FetchSQL extends AsyncTask<Void,Void,Bundle> {
//
//        protected Bundle doInBackground(Void... params) {
//            try {
//                Class.forName("org.postgresql.Driver");
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            }
//            ArrayList productnumbers = new ArrayList();
//            ArrayList productnames = new ArrayList();
//            Bundle product = new Bundle();
//            String url;
//            url = "jdbc:postgresql://shopandgodb.cv80ayxyiqrh.us-west-2.rds.amazonaws.com:5432/sagdb?user=shopandgo&password=goandshop";
//            Connection conn;
//            try {
//                DriverManager.setLoginTimeout(5);
//                conn = DriverManager.getConnection(url);
//                Statement st = conn.createStatement();
//                String username = DataHolder.getInstance().getData();
//                String sql;
//                sql = "SELECT DISTINCT b.product_no,p.name  FROM browsings b,products p WHERE username = '" + username + "' AND b.product_no = p.product_no";
//                ResultSet rs = st.executeQuery(sql);
//
//                while (rs.next()) {
//                    productnumbers.add(rs.getString("product_no"));
//                    productnames.add(rs.getString("name"));
//                }
//                rs.close();
//                st.close();
//                conn.close();
//                product.putStringArrayList("product_no",productnumbers);
//                product.putStringArrayList("productname",productnames);
//                return product;
//            } catch (SQLException e) {
//                e.printStackTrace();
//                return null;
//            }
//        }
//
//        protected void onPostExecute(Bundle product) {
//            FragmentManager manager = getFragmentManager();
//            FragmentTransaction ft = manager.beginTransaction();
//            ft.replace(R.id.frame_container,ProductListFragment.newInstance(product.getStringArrayList("product_no"),product.getStringArrayList("productname")));
//            ft.commit();
//        }
//    }
//}