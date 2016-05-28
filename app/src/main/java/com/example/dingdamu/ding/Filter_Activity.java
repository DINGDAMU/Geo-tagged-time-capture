package com.example.dingdamu.ding;

import android.app.Activity;

/**
 * Created by dingdamu on 21/05/16.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class Filter_Activity extends Activity {

    LocationService service;
    TextView time_text;
    TextView name_text;
    TextView index_text;
    TextView coordinates_text;
    TextView address_text;
    ImageView cameraImage;
    int index=0;
    Button mNext,mBack;
    JSONArray array;
    Geocoder geocoder;
    String resultLatLong, resultAddr;
    double latitude, longitude;
    List<Address> addresses;
    Button get;
    final String url="https://php-dingdamu.rhcloud.com/sql_select.php";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_filter);
        time_text = (TextView) findViewById(R.id.listTime);
        name_text = (TextView) findViewById(R.id.listUploader);
        index_text = (TextView) findViewById(R.id.index);
        coordinates_text=(TextView)findViewById(R.id.listCoordinates);
        address_text=(TextView)findViewById(R.id.listAddress);
        cameraImage=(ImageView)findViewById(R.id.listImage);


        mNext = (Button)findViewById(R.id.next_pic);
        mBack = (Button)findViewById(R.id.pre_pic);

        mBack.setVisibility(View.INVISIBLE);
        mNext.setVisibility(View.INVISIBLE);

        // Get Reference to variables


        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               // Picasso.with(Filter_Activity.this).load(sqluri.get(index)).placeholder(R.drawable.placeholder).resize(1000,1000).into(showImage);
                try{
                    JSONObject jp=array.getJSONObject(index);
                    if(index == array.length()-1){
                        Toast.makeText(Filter_Activity.this,"This is the last image, jump to the first",Toast.LENGTH_SHORT).show();
                        index = 0;
                    }
                    else{
                        index++;
                    }

                    String name = jp.getString("username");
                String time = jp.getString("time");
                String coordinates = jp.getString("coordinates");
                    coordinates_text.setText(coordinates);
                time_text.setText(time);
                String uploader = "Uploaded by:" + name;
                name_text.setText(uploader);
                String index_new = "Image Number:" + (index + 1);
                index_text.setText(index_new);
                    String url=jp.getString("url");
                    Picasso.with(Filter_Activity.this).load(url).placeholder(R.drawable.placeholder).resize(1000,1000).into(cameraImage);

                }catch(JSONException e){
                e.printStackTrace();

            }
        }});

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              //  Picasso.with(Gallery_Activity.this).load(sqluri.get(index)).placeholder(R.drawable.placeholder).resize(1000,1000).into(showImage);
                try{
                    JSONObject jp=array.getJSONObject(index);
                    if(index == 0){
                        Toast.makeText(Filter_Activity.this,"This is the first image,jump to the last image",Toast.LENGTH_SHORT).show();
                        index = array.length()-1;
                    }
                    else{
                        index--;
                    }

                    String name = jp.getString("username");
                    String time = jp.getString("time");
                    String coordinates = jp.getString("coordinates");
                    coordinates_text.setText(coordinates);
                    time_text.setText(time);
                    String uploader = "Uploaded by:" + name;
                    name_text.setText(uploader);
                    String index_new = "Image Number:" + (index + 1);
                    index_text.setText(index_new);
                    String url=jp.getString("url");
                    Picasso.with(Filter_Activity.this).load(url).placeholder(R.drawable.placeholder).resize(1000,1000).into(cameraImage);

                }catch(JSONException e){
                    e.printStackTrace();




                }
        }});

        get = (Button) findViewById(R.id.btn);

        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utils.isFastDoubleClick()){
                    return;
                }
                else if(!isNetworkAvailable()){
                    Toast.makeText(Filter_Activity.this,"Please connect the Internet!",Toast.LENGTH_SHORT).show();
                }else{
                    new PositionTask().execute();

                }
            }
        });
    }



    public void filter(View arg0) {

        if (resultAddr!=null&&isNetworkAvailable()) {
            address_text.setText(resultAddr);
            requestjsonarray();
        } else {
            String alarm="Could not get your location!";
            Toast.makeText(Filter_Activity.this, alarm, Toast.LENGTH_SHORT).show();
        }

    }

    public void requestjsonarray() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("address", resultAddr);
        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
                    public void onSuccess(int  statusCode, Header[] headers,JSONObject response) {
                try {
                     array = response.getJSONArray("information");
                    if (array.length() == 0) {
                        Toast.makeText(Filter_Activity.this, "No matching pictures!", Toast.LENGTH_SHORT).show();
                    } else {
                        JSONObject jp = array.getJSONObject(index);

                        String name = jp.getString("username");
                        String time = jp.getString("time");
                        String coordinates = jp.getString("coordinates");
                        coordinates_text.setText(coordinates);
                        time_text.setText(time);
                        String uploader = "Uploaded by:" + name;
                        name_text.setText(uploader);
                        String index_new = "Image Number:" + (index + 1);
                        index_text.setText(index_new);
                        String url=jp.getString("url");
                        Picasso.with(Filter_Activity.this).load(url).placeholder(R.drawable.placeholder).resize(1000,1000).into(cameraImage);
                        mBack.setVisibility(View.VISIBLE);
                        mNext.setVisibility(View.VISIBLE);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
                });
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


    public class PositionTask extends AsyncTask<String, String, List<Address>> {

        private ProgressDialog pDialog;



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Filter_Activity.this);
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
            service = new LocationService(Filter_Activity.this);


            Location gpsLocation = service.getLocation(LocationManager.GPS_PROVIDER);


            if (gpsLocation != null) {


                latitude = gpsLocation.getLatitude();
                longitude = gpsLocation.getLongitude();
                resultLatLong = "Latitude: " + gpsLocation.getLatitude() +
                        " Longitude: " + gpsLocation.getLongitude();
                geocoder = new Geocoder(Filter_Activity.this, Locale.getDefault());

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
            Filter_Activity.this.runOnUiThread(new Runnable() {
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
                Toast.makeText(Filter_Activity.this, "Could not get location !Please retry in "+Utils.clicktime/1000+" seconds!", Toast.LENGTH_SHORT).show();
                service.removeUpdates();
                service.unregisterlistener();
            } else {

                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getAddressLine(1);
                String state = addresses.get(0).getAddressLine(2);
                resultAddr = address + "," + city + ", " + state;

                service.removeUpdates();
                service.unregisterlistener();




            }


        }
    }
}
