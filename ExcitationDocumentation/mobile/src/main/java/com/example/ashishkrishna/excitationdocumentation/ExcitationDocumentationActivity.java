package com.example.ashishkrishna.excitationdocumentation;

import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.database.Cursor;
import android.content.Intent;
import android.app.ActionBar;
import android.view.View;
import android.widget.Button;

import android.os.Environment;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import com.twitter.sdk.android.core.TwitterSession;


import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import io.fabric.sdk.android.Fabric;
import java.io.File;
import android.provider.MediaStore;
import java.io.IOException;
import java.util.Date;
import java.io.OutputStream;
import android.widget.ImageView;
import java.text.SimpleDateFormat;
import android.net.Uri;
import retrofit.http.GET;
import java.io.*;
import java.net.*;
import android.util.Log;
public class ExcitationDocumentationActivity extends MainActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "0yFFZJGDgXD03Pj9ErqADuB34";
    private static final String TWITTER_SECRET = "K1xWSXoiShMo9wY1qOn5WPXw1lh9FNs5ewksEkv0eDfxJ8ZC9a";

    public static final int CAMERA_REQUEST_CODE = 2222;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    public static String mCurrPhotoPath;
    String imgDecodableString;
    Button cam;
    Button twitter1;
    private static final int SELECT_PICTURE = 1;
    private static int RESULT_LOAD_IMG = 1;
    Intent imgcap1;
    Intent twtr;
    File photoone = null;
    Thread a;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        TwitterSession session = Twitter.getSessionManager().getActiveSession();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new TweetComposer());
        setContentView(R.layout.activity_excitation_documentation);
        cam = (Button) findViewById(R.id.button);
        twitter1 = (Button) findViewById(R.id.button2);
        cam.setOnClickListener(new View.OnClickListener() {


            public void onClick(View vw1) {


                Intent cam1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


                if (cam1.resolveActivity(getPackageManager()) != null) {

                    try {
                        photoone = createImageFile();

                    }
                    catch(Exception h) {

                    }

                    if (photoone != null) {

                        MainActivity.filename = Uri.fromFile(photoone);
                        cam1.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoone));
                        startActivityForResult(cam1, 1);
                    }


                }


            }

        });

        twitter1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View vw2) {
                setTwitterbtn();
            }
        });
    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_excitation_documentation, menu);
        return true;
    }

    public void setTwitterbtn() {




                        Intent twitget = new Intent(this, TweetgetActivity.class);
                        startActivity(twitget);
                       // try {
                         //   Thread.currentThread().wait(3000);
                       // }
                       // catch (InterruptedException f) {

                        // }
                       // android.os.Process.killProcess(android.os.Process.myPid());










    }











    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // To Handle Camera Result
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            Log.w("" + REQUEST_IMAGE_CAPTURE, "" + requestCode);
            String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
            galleryAddPic();
            ImageView imgView = (ImageView) findViewById(R.id.imgView);
            imgView.setImageURI(Uri.parse(MainActivity.mCurrPhotoPath));

            }




        }



   // String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        MainActivity.mCurrPhotoPath =  image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

        Uri contentUri = MainActivity.filename;
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

           @Override
            public boolean onOptionsItemSelected(MenuItem item) {
                // Handle action bar item clicks here. The action bar will
                // automatically handle clicks on the Home/Up button, so long
                // as you specify a parent activity in AndroidManifest.xml.
                int id = item.getItemId();

                //noinspection SimplifiableIfStatement
                if (id == R.id.action_settings) {
                    return true;
                }

                return super.onOptionsItemSelected(item);
            }





    }
