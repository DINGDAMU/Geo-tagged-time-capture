package com.example.dingdamu.ding;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by dingdamu on 17/05/16.
 */
public class Compass_Activity extends AppCompatActivity {
    private Compass compass;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        Compass.mText=(TextView) findViewById(R.id.compass_information);

        compass = new Compass(this);
        compass.arrowView = (ImageView) findViewById(R.id.main_image_hands);



    }

    @Override
    protected void onStart() {
        super.onStart();
        compass.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        compass.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        compass.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        compass.stop();
    }

}
