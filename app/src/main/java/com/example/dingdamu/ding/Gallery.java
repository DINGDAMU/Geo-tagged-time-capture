package com.example.dingdamu.ding;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by dingdamu on 12/05/16.
 */
public class Gallery extends AppCompatActivity {

    Button mNext,mBack;
    ArrayList<String> sqluri,sqlcoordinate,sqladdress,sqltime;
    PostORM p = new PostORM();
    ArrayList<ArrayList<String>> holder;
    ListView feedList;
    PostAdapter adapter;
    ImageView showImage;
    TextView showTime;


    double latitude, longitude;
    Geocoder geocoder;
    List<Address> addresses;
    String resultLatLong;
    LocationService service;
    int index=0;
    TextView showIndex;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_gallery);
        mNext = (Button)findViewById(R.id.next_pic);
        mBack = (Button)findViewById(R.id.pre_pic);
        showImage= (ImageView) findViewById(R.id.listImage);
        showTime=(TextView)findViewById(R.id.listTime);
        showIndex=(TextView)findViewById(R.id.index);




        service = new LocationService(Gallery.this);
        Location gpsLocation = service.getLocation(LocationManager.GPS_PROVIDER);
        if (gpsLocation != null) {
            latitude = gpsLocation.getLatitude();
            longitude = gpsLocation.getLongitude();
            resultLatLong = "Latitude: " + gpsLocation.getLatitude()+" \nLongitude: " + gpsLocation.getLongitude();
            geocoder = new Geocoder(this, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(addresses.isEmpty()||!isNetworkAvailable()) {
                Toast.makeText(Gallery.this, "Location not found!", Toast.LENGTH_SHORT).show();
                mBack.setVisibility(View.INVISIBLE);
                mNext.setVisibility(View.INVISIBLE);
            }
            else{

                String address = addresses.get(0).getAddressLine(0)+"\n"+addresses.get(0).getAddressLine(1)+", "
                        +addresses.get(0).getAddressLine(2);
                Toast.makeText(Gallery.this,address,Toast.LENGTH_SHORT).show();
                sqluri=p.getNeededUrifromDB(Gallery.this,address);


                //sqlcoordinate = p.getNeededCoordinatesfromDB(Gallery.this,address);
                //sqladdress = p.getNeededAddressfromDB(Gallery.this,address);
                sqltime = p.getNeededTimefromDB(Gallery.this,address);
                //Toast.makeText(Gallery.this,"Finish reading the database",Toast.LENGTH_SHORT).show();

                if(sqluri.isEmpty()) {
                    Toast.makeText(Gallery.this,"No matching pictures!",Toast.LENGTH_SHORT).show();


                }else{
                    Picasso.with(this).load(sqluri.get(index)).placeholder(R.drawable.placeholder).resize(1000,1000).into(showImage);
                    String index_new="Image Number:"+(index+1);
                    showIndex.setText(index_new);
                    showTime.setText(sqltime.get(index));
                    mBack.setVisibility(View.VISIBLE);
                    mNext.setVisibility(View.VISIBLE);





                }


            }


        }
        else
        {
            Toast.makeText(Gallery.this,"Could not get location !",Toast.LENGTH_SHORT).show();
            mBack.setVisibility(View.INVISIBLE);
            mNext.setVisibility(View.INVISIBLE);
        }





        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(index == sqluri.size()-1){
                    Toast.makeText(Gallery.this,"This is the last image, jump to the first",Toast.LENGTH_SHORT).show();
                    index = 0;
                }
                else{
                    index++;
                }
                Picasso.with(Gallery.this).load(sqluri.get(index)).placeholder(R.drawable.placeholder).resize(1000,1000).into(showImage);
                showIndex.setText("Image Number:"+(index+1));
                showTime.setText(sqltime.get(index));

            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(index == 0){
                    Toast.makeText(Gallery.this,"This is the first image,jump to the last image",Toast.LENGTH_SHORT).show();
                    index = sqluri.size()-1;
                }
                else{
                    index--;
                }
                Picasso.with(Gallery.this).load(sqluri.get(index)).placeholder(R.drawable.placeholder).resize(1000,1000).into(showImage);
                showIndex.setText("Image Number:"+(index+1));
                showTime.setText(sqltime.get(index));


            }
        });
    }







    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }


}
