package com.example.dingdamu.ding;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by dingdamu on 11/05/16.
 */
public class GetyourPosition_Activity extends AppCompatActivity {
    TextView locationText, addressText;




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_getyourposition);
        locationText = (TextView)findViewById(R.id.locationText1);
        addressText = (TextView)findViewById(R.id.addressText1);
        Position pos=new Position();
        pos.getPosition(locationText,addressText,GetyourPosition_Activity.this);



    }

}
