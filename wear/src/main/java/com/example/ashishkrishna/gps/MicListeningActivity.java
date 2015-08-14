package com.example.ashishkrishna.gps;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by qinbian on 7/30/15.
 */
public class MicListeningActivity extends Activity {

    private TextView txtSpeechInput;
    private ImageButton confirmButton;
    private ImageButton commentAgainButton;
    private Handler mHandler;

    private final int REQ_CODE_SPEECH_INPUT = 100;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_miclistening);
        mHandler = new Handler();

        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        confirmButton = (ImageButton) findViewById(R.id.confirm_icon);
        commentAgainButton = (ImageButton) findViewById(R.id.comment_again_icon);

        promptSpeechInput();
        clickCommentAgain();
        clickConfirm();
    }

    private void clickConfirm(){
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                confirmButton.setImageResource(R.drawable.check_circle_clicked);

                mHandler.postDelayed(new Runnable(){
                    public void run(){
                        confirmButton.setImageResource(R.drawable.check_circle);
                    }
                }, 2000);

                Intent openCmtReceivedIntent = new Intent(MicListeningActivity.this, CmtReceivedActivity.class);
                MicListeningActivity.this.startActivity(openCmtReceivedIntent);
            }
        });

    }

    private void clickCommentAgain(){
        commentAgainButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                commentAgainButton.setImageResource(R.drawable.cross_circle_clicked);

                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        commentAgainButton.setImageResource(R.drawable.cross_circle);
                    }
                }, 2000);



                Intent openCommentActivity = new Intent(MicListeningActivity.this, CommentActivity.class);
                MicListeningActivity.this.startActivity(openCommentActivity);
            }

        });
    }

    /**
     * Showing google speech input dialog
     */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtSpeechInput.setText(result.get(0));
                }
                break;
            }
        }
    }




}
