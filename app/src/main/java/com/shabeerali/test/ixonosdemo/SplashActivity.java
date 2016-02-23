package com.shabeerali.test.ixonosdemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Start the Main activity after 3 seconds
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent intent = getIntent();
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                if(intent.getData() != null ) {
                    i.setData(intent.getData());
                }
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
