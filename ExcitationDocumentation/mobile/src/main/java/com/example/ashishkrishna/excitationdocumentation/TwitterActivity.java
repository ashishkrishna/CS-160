package com.example.ashishkrishna.excitationdocumentation;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.content.Intent;
import android.app.ActionBar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.Environment;
import android.content.Context;


import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import io.fabric.sdk.android.Fabric;
import com.twitter.sdk.android.Twitter;
import android.net.Uri;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.tweetui.TweetUi;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.TwitterAuthToken;
import android.util.Log;
/**
 * Created by ashishkrishna on 7/5/15.
 */
public class TwitterActivity extends MainActivity {
    private static final String TWITTER_KEY = "tqljtrFZr2GWf2escnUEAhgtN";
    private static final String TWITTER_SECRET = "zdiIg01sS7EYuiXU5I0lbAAQPpNv3UkC5DjKWEwlg9WhOTqFEj";

    public static final int CAMERA_REQUEST_CODE = 2222;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String mCurrPhotoPath;
    String imgDecodableString;
    Button cam;
    Button twitter1;
    TwitterLoginButton loginButton;
    private static final int SELECT_PICTURE = 1;
    private static int RESULT_LOAD_IMG = 1;
    Intent imgcap1;
    Thread a;

    Twitter mTwitter;

    @Override
    public void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.twitter_login);
        //String twitterkey1 = getIntent().getStringExtra(TWITTER_KEY);
        //String twittersec1 = getIntent().getStringExtra(TWITTER_SECRET);

        loginButton = (TwitterLoginButton)
                findViewById(R.id.login_button);
        TwitterAuthConfig twitterAuthConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);


        Fabric.with(this, new Twitter(twitterAuthConfig));
        Fabric.with(this, new TwitterCore(twitterAuthConfig), new TweetUi());
        Fabric.with(this, new TweetComposer());


       loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                ;
                // Do something with result, which provides a TwitterSession for making API calls
                //TwitterSession session = Twitter.getSessionManager().getActiveSession();
                //TwitterAuthToken authToken = session.getAuthToken();
                //String token = authToken.token;
                //String secret = authToken.secret;
                //TextView t1 = (TextView) findViewById(R.id.textView);
                //t1.setText("CHANGED");


            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
                //TextView t1 = (TextView) findViewById(R.id.textView);
                //t1.setText("ERROR");
            }
        });





    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the login button.
        loginButton.onActivityResult(requestCode, resultCode,
                data);
        this.finish();

 }

}








