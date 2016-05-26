package com.example.dingdamu.ding;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.Base64;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

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
    Bitmap bitmap;
    String updated;

   // public static final int CONNECTION_TIMEOUT=10000;
   // public static final int READ_TIMEOUT=15000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_network_painting);
        LocationService service = new LocationService(Network_painting_Activity.this);
        cameraImage = (ImageView) findViewById(R.id.image);
        locationText = (TextView) findViewById(R.id.locationText);
        addressText = (TextView) findViewById(R.id.addressText);
        mUpload = (Button) findViewById(R.id.uploadPost);
        mCancel = (Button) findViewById(R.id.cancelPost);
        addresses = new ArrayList<>();
        mRetry = (Button) findViewById(R.id.retryButton);
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
         updated = "Updated : " + today.monthDay + "-" + (today.month + 1) + "-" + today.year + "   " + today.format("%k:%M:%S");

        imageUri = getIntent().getData();
        try{
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);


        }catch(Exception e){
            e.printStackTrace();
        }
        mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //new AsyncLogin().execute("ding1212121",imageUri.toString(),resultLatLong,resultAddr,updated);
                sendImage(bitmap);
                Intent i = new Intent(Network_painting_Activity.this, Upload_Activity.class);
                startActivity(i);
                Network_painting_Activity.this.finish();

            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Network_painting_Activity.this, "Upload Discarded", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Network_painting_Activity.this, Upload_Activity.class);
                startActivity(i);
                Network_painting_Activity.this.finish();

            }
        });

        mRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utils.isFastDoubleClick()){
                    return;
                }else {
                    new LocationTask().execute();
                }

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


    public class LocationTask extends AsyncTask<String,String,List<Address> > {

        private ProgressDialog pDialog;
        LocationService service;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(pDialog!=null){
                pDialog=null;
            }
            pDialog = new ProgressDialog(Network_painting_Activity.this);
            pDialog.setMessage("Getting your location ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected List<Address>  doInBackground(String... params) {
            Looper.prepare();
             service=new LocationService(Network_painting_Activity.this);
            service = new LocationService(Network_painting_Activity.this);
            Location gpsLocation = service.getLocation(LocationManager.GPS_PROVIDER);
            if (gpsLocation != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRetry.setVisibility(View.GONE);
                        mUpload.setVisibility(View.VISIBLE);
//stuff that updates ui
                    }
                });

                latitude = gpsLocation.getLatitude();
                longitude = gpsLocation.getLongitude();
                resultLatLong = "Latitude: " + gpsLocation.getLatitude() +
                        " Longitude: " + gpsLocation.getLongitude();
                geocoder = new Geocoder(Network_painting_Activity.this, Locale.getDefault());

                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    return addresses; // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Looper.myLooper().quit();
            return null;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {
            pDialog.dismiss();
            super.onPostExecute(addresses);
            if(!isNetworkAvailable()||addresses==null)
            {
                Toast.makeText(Network_painting_Activity.this,"Could not get location !Please retry in "+Utils.clicktime/1000+" seconds!",Toast.LENGTH_SHORT).show();
                mUpload.setVisibility(View.GONE);
                mRetry.setVisibility(View.VISIBLE);
                service.removeUpdates();
                service.unregisterlistener();

            }
            else {
                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                resultAddr = address + "\n" + city + ", " + state;
                locationText.setText(resultLatLong);
                addressText.setText(resultAddr);
                service.removeUpdates();
                service.unregisterlistener();
            }
        }
    }
    private void sendImage(Bitmap bm)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 60, stream);
        InputStream isBm=new ByteArrayInputStream(stream.toByteArray());

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(6*1000);
        RequestParams params = new RequestParams();
        Date mCurrentDate = new Date();
        String mTimestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ITALY).format(mCurrentDate);
        String filename=mTimestamp+".jpg";


        params.put("attach", isBm,filename);

            params.put("username","DAMU DING");
            params.put("url","http://192.168.1.52/upload/"+filename);
            params.put("coordinates",resultLatLong);
            params.put("address",resultAddr);
            params.put("time",updated);
        client.post("http://192.168.1.52/upload.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int  statusCode, Header[] headers, byte[] bytes) {
                Toast.makeText(Network_painting_Activity.this, "Upload success!", Toast.LENGTH_LONG).show();


            }
            @Override
            public void onFailure(int  statusCode, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(Network_painting_Activity.this, "Upload Fail!", Toast.LENGTH_LONG).show();
            }
        });
    }




}