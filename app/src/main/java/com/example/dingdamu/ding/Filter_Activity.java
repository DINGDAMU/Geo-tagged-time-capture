package com.example.dingdamu.ding;

import android.app.Activity;

/**
 * Created by dingdamu on 21/05/16.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

public class Filter_Activity extends Activity {
    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds

    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    PostORM p = new PostORM();
    Geocoder geocoder;
    List<Address> addresses;
    LocationService service;
    double latitude, longitude;
    TextView time_text;
    TextView name_text;
    TextView index_text;
    TextView coordinates_text;
    int index=0;
    Button mNext,mBack;
    JSONArray jsonArray;
    String address;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_filter);
        time_text = (TextView) findViewById(R.id.listTime);
        name_text = (TextView) findViewById(R.id.listUploader);
        index_text = (TextView) findViewById(R.id.index);
        coordinates_text=(TextView)findViewById(R.id.listCoordinates);
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
                    JSONObject jp=jsonArray.getJSONObject(index);
                    if(index == jsonArray.length()-1){
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
            }catch(JSONException e){
                e.printStackTrace();

            }
        }});

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              //  Picasso.with(Gallery_Activity.this).load(sqluri.get(index)).placeholder(R.drawable.placeholder).resize(1000,1000).into(showImage);
                try{
                    JSONObject jp=jsonArray.getJSONObject(index);
                    if(index == 0){
                        Toast.makeText(Filter_Activity.this,"This is the first image,jump to the last image",Toast.LENGTH_SHORT).show();
                        index = jsonArray.length()-1;
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
                    mBack.setVisibility(View.VISIBLE);
                    mNext.setVisibility(View.VISIBLE);
                }catch(JSONException e){
                    e.printStackTrace();




                }
        }});
    }



    public void filter(View arg0) {
        address=getyourposition();
        if(address!=null) {
            Toast.makeText(Filter_Activity.this, address, Toast.LENGTH_SHORT).show();


            new AsyncLogin().execute();
        }
        else{
            String alarm="Could not get your location!";
            Toast.makeText(Filter_Activity.this, alarm, Toast.LENGTH_SHORT).show();

        }
        service.removeUpdates();
        service.unregisterlistener();




    }



    private class AsyncLogin extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(Filter_Activity.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }
        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL("http://192.168.1.52:80/mysql_select.php?address="+address);

            } catch(MalformedURLException e){
                e.printStackTrace();
                return "Exception";
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("GET");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);


                // Append parameters to URL
                //Uri.Builder builder = new Uri.Builder();
                //String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                //writer.write(query);
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
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }



                    String true_result=result.toString();

                    // Pass data to onPostExecute method
                    return true_result;

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
        protected void onPostExecute(String true_result) {

            //this method will be running on UI thread

            pdLoading.dismiss();
            //Toast.makeText(Filter_Activity.this,true_result,Toast.LENGTH_SHORT).show();



            try {
                JSONObject json_data=new JSONObject(true_result);
                jsonArray=json_data.getJSONArray("information");
                if (jsonArray.length()==0) {
                    Toast.makeText(Filter_Activity.this, "No matching pictures!", Toast.LENGTH_SHORT).show();



                }else {
                    JSONObject jp=jsonArray.getJSONObject(index);

                    String name = jp.getString("username");
                    String time = jp.getString("time");
                    String coordinates = jp.getString("coordinates");
                    coordinates_text.setText(coordinates);
                    time_text.setText(time);
                    String uploader = "Uploaded by:" + name;
                    name_text.setText(uploader);
                    String index_new = "Image Number:" + (index + 1);
                    index_text.setText(index_new);
                    mBack.setVisibility(View.VISIBLE);
                    mNext.setVisibility(View.VISIBLE);
                    Filter_Activity.this.finish();
                }



            } catch (JSONException e) {
                e.printStackTrace();

            }


        }

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

    public String getyourposition() {
            service = new LocationService(Filter_Activity.this);
            Location gpsLocation = service.getLocation(LocationManager.GPS_PROVIDER);
            while (gpsLocation != null) {
                latitude = gpsLocation.getLatitude();
                longitude = gpsLocation.getLongitude();
                geocoder = new Geocoder(this, Locale.getDefault());

                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (addresses.isEmpty() || !isNetworkAvailable()) {
                    Toast.makeText(Filter_Activity.this, "Location not found!", Toast.LENGTH_SHORT).show();

                } else {

                    address = addresses.get(0).getAddressLine(0) + "\n" + addresses.get(0).getAddressLine(1) + ", "
                            + addresses.get(0).getAddressLine(2);



                }

            }
        return address;

    }
}
