package com.example.dingdamu.ding;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Downloader;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by dingdamu on 23/05/16.
 */
public class Network_painting_Activity extends AppCompatActivity {
    ImageView cameraImage;
    TextView locationText, addressText;
    Uri imageUri;
    Geocoder geocoder;
    List<Address> addresses;
    double latitude, longitude;
    String resultLatLong, resultAddr;
    Button mUpload, mCancel, mRetry;
    PostORM p = new PostORM();
    LocationService service;
    Output op = new Output();
    int IMAGE_CONST = 1;
    File mFile;
    int  serverResponseCode;


    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_network_painting);
        service = new LocationService(Network_painting_Activity.this);
        cameraImage = (ImageView) findViewById(R.id.image);
        locationText = (TextView) findViewById(R.id.locationText);
        addressText = (TextView) findViewById(R.id.addressText);
        mUpload = (Button) findViewById(R.id.uploadPost);
        mCancel = (Button) findViewById(R.id.cancelPost);
        addresses = new ArrayList<>();
        mRetry = (Button) findViewById(R.id.retryButton);
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        final String updated = "Updated : " + today.monthDay + "-" + (today.month + 1) + "-" + today.year + "   " + today.format("%k:%M:%S");

        imageUri = getIntent().getData();
        mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AsyncLogin().execute("ding1212121",imageUri.toString(),resultLatLong,resultAddr,updated);


                Toast.makeText(Network_painting_Activity.this, "Uploaded new Post", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Network_painting_Activity.this, Upload_Activity.class);
                startActivity(i);
                Network_painting_Activity.this.finish();

            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Network_painting_Activity.this, "Upload Discarded", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Network_painting_Activity.this, Import.class);
                startActivity(i);
            }
        });

        mRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Position pos = new Position();
                pos.getPosition(locationText, addressText, Network_painting_Activity.this);


            }
        });

        Location gpsLocation = service.getLocation(LocationManager.GPS_PROVIDER);
        if (gpsLocation != null) {
            latitude = gpsLocation.getLatitude();
            longitude = gpsLocation.getLongitude();
            resultLatLong = "Latitude: " + gpsLocation.getLatitude() +
                    " Longitude: " + gpsLocation.getLongitude();
            geocoder = new Geocoder(this, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (addresses.isEmpty() || !isNetworkAvailable()) {
                mUpload.setVisibility(View.GONE);
                mRetry.setVisibility(View.VISIBLE);
            } else {

                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getAddressLine(1);
                String state = addresses.get(0).getAddressLine(2);
                resultAddr = address + "," + city + ", " + state;
                locationText.setText(resultLatLong);
                addressText.setText(resultAddr);
            }
        } else {
            Toast.makeText(Network_painting_Activity.this, "Could not get location !", Toast.LENGTH_SHORT).show();
            mUpload.setVisibility(View.GONE);
            mRetry.setVisibility(View.VISIBLE);
        }
        Picasso.with(this).load(imageUri.toString()).placeholder(R.drawable.placeholder).resize(1000, 1000).into(cameraImage);
        service.removeUpdates();
        service.unregisterlistener();

    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    private class AsyncLogin extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(Network_painting_Activity.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tUploading...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }
        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL("http://192.168.1.52/mysql.php");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return "exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);


                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("username", params[0])
                        .appendQueryParameter("url",params[1])
                        .appendQueryParameter("coordinates", params[2])
                        .appendQueryParameter("address",params[3])
                        .appendQueryParameter("time",params[4]);
                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server


                    // Pass data to onPostExecute method
                    return "true";

                }else{

                    return("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }


        }

        @Override
        protected void onPostExecute(String result) {

            //this method will be running on UI thread

            pdLoading.dismiss();

            if(result.equalsIgnoreCase("true"))
            {
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */

                Toast.makeText(Network_painting_Activity.this,"success!",Toast.LENGTH_SHORT).show();

            }else if (result.equalsIgnoreCase("false")){

                // If username and password does not match display a error message
                Toast.makeText(Network_painting_Activity.this, "Invalid email or password", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(Network_painting_Activity.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

            }
        }

    }

}