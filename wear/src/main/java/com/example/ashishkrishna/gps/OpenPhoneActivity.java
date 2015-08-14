package com.example.ashishkrishna.gps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by qinbian on 7/30/15.
 */
public class OpenPhoneActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_openphone);

        final ImageButton phoneButton = (ImageButton) findViewById(R.id.open_phone);
        phoneButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent openCmtReceivedIntent = new Intent(OpenPhoneActivity.this, CmtReceivedActivity.class);
                OpenPhoneActivity.this.startActivity(openCmtReceivedIntent);

            }
        });

    }
}
