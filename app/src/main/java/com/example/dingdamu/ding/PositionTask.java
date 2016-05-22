package com.example.dingdamu.ding;

import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.zip.Inflater;

/**
 * Created by dingdamu on 22/05/16.
 */
public class PositionTask extends AsyncTask<String,String,String> {

    private ProgressDialog pDialog;

    Context context;
    double latitude, longitude;
    Geocoder geocoder;
    List<Address> addresses;
    String resultLatLong, resultAddr;
    TextView locationText, addressText;


        public PositionTask(Context context){
            this.context=context;
        }








    @Override
        protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Getting your location ...");
        //pDialog.setIndeterminate(false);
        //pDialog.setCancelable(true);
       // pDialog.show();


    }

    @Override
    protected String doInBackground(String... params) {
        LocationService service = new LocationService(context);


        Location gpsLocation = service.getLocation(LocationManager.GPS_PROVIDER);

        if (gpsLocation != null) {


            latitude = gpsLocation.getLatitude();
            longitude = gpsLocation.getLongitude();
            resultLatLong = "Latitude: " + gpsLocation.getLatitude() +
                    " Longitude: " + gpsLocation.getLongitude();
            geocoder = new Geocoder(context, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {

        if(!isNetworkAvailable(context)||addresses.isEmpty())
        {
            Toast.makeText(context,"Could not get location !",Toast.LENGTH_SHORT).show();

        }
        else {
            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            resultAddr = address + "\n" + city + ", " + state;
            locationText.setText(resultLatLong);
            addressText.setText(resultAddr);
        }
        pDialog.dismiss();

    }


    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }
}
