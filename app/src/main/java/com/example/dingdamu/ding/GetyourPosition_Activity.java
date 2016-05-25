package com.example.dingdamu.ding;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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
    LocationService service;
    Button get;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_getyourposition);
        locationText = (TextView) findViewById(R.id.locationText1);
        addressText = (TextView) findViewById(R.id.addressText1);
        get = (Button) findViewById(R.id.btn);

        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utils.isFastDoubleClick()){
                    return;
                }
                else if (!isNetworkAvailable(GetyourPosition_Activity.this)) {
                    String alarm = "Please connect the internet!";
                    Toast.makeText(GetyourPosition_Activity.this, alarm, Toast.LENGTH_SHORT).show();
                } else {
            new PositionTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        });


    }

    public class PositionTask extends AsyncTask<String, String, List<Address>> {

        private ProgressDialog pDialog;
        Geocoder geocoder;
        String resultLatLong, resultAddr;
        double latitude, longitude;
        List<Address> addresses;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(GetyourPosition_Activity.this);
            pDialog.setMessage("Getting your location ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    cancel(true);
                }
            });
            pDialog.show();


        }

        @Override
        protected List<Address> doInBackground(String... params) {
            Looper.prepare();
            service = new LocationService(GetyourPosition_Activity.this);


            Location gpsLocation = service.getLocation(LocationManager.GPS_PROVIDER);


            if (gpsLocation != null) {


                latitude = gpsLocation.getLatitude();
                longitude = gpsLocation.getLongitude();
                resultLatLong = "Latitude: " + gpsLocation.getLatitude() +
                        " Longitude: " + gpsLocation.getLongitude();
                geocoder = new Geocoder(GetyourPosition_Activity.this, Locale.getDefault());

                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    return addresses;
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
            if(isCancelled()) {
                return null;
            }
            Looper.myLooper().quit();


            return null;

        }

        @Override
        protected void onPostExecute(List<Address> addresses) {
            GetyourPosition_Activity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(pDialog!=null){
                        pDialog.dismiss();
                        pDialog=null;
                    }
                }
            });
            super.onPostExecute(addresses);

            if (addresses == null) {
                Toast.makeText(GetyourPosition_Activity.this, "Could not get location !Please retry in "+Utils.clicktime/1000+" seconds!", Toast.LENGTH_SHORT).show();
                get.setVisibility(View.INVISIBLE);
                service.removeUpdates();
                service.unregisterlistener();
            } else {

                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getAddressLine(1);
                String state = addresses.get(0).getAddressLine(2);
                resultAddr = address + "," + city + ", " + state;

                get.setVisibility(View.INVISIBLE);
                locationText.setText(resultLatLong);
                addressText.setText(resultAddr);
                service.removeUpdates();
                service.unregisterlistener();




            }


        }
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo.isConnected()) {
            return true;
        }
        return false;
    }




}
