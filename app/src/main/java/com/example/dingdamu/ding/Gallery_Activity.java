package com.example.dingdamu.ding;

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
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by dingdamu on 12/05/16.
 */
public class Gallery_Activity extends AppCompatActivity {

    Button mNext,mBack;
    ArrayList<String> sqluri,sqltime;
    PostORM p = new PostORM();
    ImageView showImage;
    TextView showTime;
    String address;


    double latitude, longitude;
    Geocoder geocoder;
    List<Address> addresses;
    String resultLatLong;
    LocationService service;
    int index=0;
    TextView showIndex;
    Button mGet;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_gallery);
        mNext = (Button)findViewById(R.id.next_pic);
        mBack = (Button)findViewById(R.id.pre_pic);
        mGet=(Button)findViewById(R.id.btn);
        showImage= (ImageView) findViewById(R.id.listImage);
        showTime=(TextView)findViewById(R.id.listTime);
        showIndex=(TextView)findViewById(R.id.index);
        mBack.setVisibility(View.INVISIBLE);
        mNext.setVisibility(View.INVISIBLE);


        mGet.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                new PositionTask().execute();
                mGet.setVisibility(View.INVISIBLE);


            }
        });















        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(index == sqluri.size()-1){
                    Toast.makeText(Gallery_Activity.this,"This is the last image, jump to the first",Toast.LENGTH_SHORT).show();
                    index = 0;
                }
                else{
                    index++;
                }
                Picasso.with(Gallery_Activity.this).load(sqluri.get(index)).placeholder(R.drawable.placeholder).resize(1000,1000).into(showImage);
                showIndex.setText("Image Number:"+(index+1));
                showTime.setText(sqltime.get(index));

            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(index == 0){
                    Toast.makeText(Gallery_Activity.this,"This is the first image,jump to the last image",Toast.LENGTH_SHORT).show();
                    index = sqluri.size()-1;
                }
                else{
                    index--;
                }
                Picasso.with(Gallery_Activity.this).load(sqluri.get(index)).placeholder(R.drawable.placeholder).resize(1000,1000).into(showImage);
                String ImageNum="Image Number:"+(index+1);
                showIndex.setText(ImageNum);
                showTime.setText(sqltime.get(index));



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
            pDialog = new ProgressDialog(Gallery_Activity.this);
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
            service = new LocationService(Gallery_Activity.this);


            Location gpsLocation = service.getLocation(LocationManager.GPS_PROVIDER);


            if (gpsLocation != null) {


                latitude = gpsLocation.getLatitude();
                longitude = gpsLocation.getLongitude();
                resultLatLong = "Latitude: " + gpsLocation.getLatitude() +
                        " Longitude: " + gpsLocation.getLongitude();
                geocoder = new Geocoder(Gallery_Activity.this, Locale.getDefault());

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
            Gallery_Activity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(pDialog!=null){
                        pDialog.dismiss();
                        pDialog=null;
                    }
                }
            });
            super.onPostExecute(addresses);

            if (addresses == null||!isNetworkAvailable()) {
                Toast.makeText(Gallery_Activity.this, "Could not get location !Please retry in "+Utils.clicktime/1000+" seconds!", Toast.LENGTH_SHORT).show();
                service.removeUpdates();
                service.unregisterlistener();
                mBack.setVisibility(View.INVISIBLE);
                mNext.setVisibility(View.INVISIBLE);
            } else {

                address = addresses.get(0).getAddressLine(0)+","+addresses.get(0).getAddressLine(1)+", "
                        +addresses.get(0).getAddressLine(2);

                Toast.makeText(Gallery_Activity.this,address,Toast.LENGTH_SHORT).show();
                sqluri=p.getNeededUrifromDB(Gallery_Activity.this,address);


                sqltime = p.getNeededTimefromDB(Gallery_Activity.this,address);

                if(sqluri.isEmpty()) {
                    Toast.makeText(Gallery_Activity.this,"No matching pictures!",Toast.LENGTH_SHORT).show();
                    service.removeUpdates();
                    service.unregisterlistener();


                }else{
                    Picasso.with(Gallery_Activity.this).load(sqluri.get(index)).placeholder(R.drawable.placeholder).resize(1000,1000).into(showImage);
                    String index_new="Image Number:"+(index+1);
                    showIndex.setText(index_new);
                    showTime.setText(sqltime.get(index));
                    mBack.setVisibility(View.VISIBLE);
                    mNext.setVisibility(View.VISIBLE);
                    service.removeUpdates();
                    service.unregisterlistener();







                }






            }


        }
    }



}
