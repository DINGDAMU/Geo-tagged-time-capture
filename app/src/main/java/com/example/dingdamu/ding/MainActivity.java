package com.example.dingdamu.ding;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    NavigationView navigationView;
     String email_check;
    String name_check;
    String profile_url_check;
    final int gallery_const=2;
     ImageView profile;
    Bitmap bitmap;
    final String url="https://php-dingdamu.rhcloud.com/update.php";

    final String url_profile="https://php-dingdamu.rhcloud.com/profile/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);













        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

       navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SharedPreferences sharedPreferences= getSharedPreferences("profile",
                Activity.MODE_PRIVATE);
        email_check =sharedPreferences.getString("email", "");
        name_check=sharedPreferences.getString("username", "");
        profile_url_check=sharedPreferences.getString("profile_url","");


        View headerView = navigationView.getHeaderView(0);
        TextView name_txt = (TextView) headerView.findViewById(R.id.id_username);
        TextView email_txt = (TextView) headerView.findViewById(R.id.email);
        profile=(ImageView)headerView.findViewById(R.id.imageView);
        name_txt.setText(name_check);
        email_txt.setText(email_check);
        if(profile_url_check!=null&&profile_url_check.trim().length() != 0) {
           Picasso.with(MainActivity.this).load(profile_url_check).placeholder(R.mipmap.placeholder).resize(100, 100).into(profile);

        }
        profile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, gallery_const);
            }
        });


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            SharedPreferences mySharedPreferences= getSharedPreferences("profile", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = mySharedPreferences.edit();
            editor.putString("email", "");
            editor.putString("password","");
            editor.apply();
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, Login_Activity.class);
            startActivity(intent);
            MainActivity.this.finish();
                  }

        return super.onOptionsItemSelected(item);
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, Import.class);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, GetyourPosition_Activity.class);
            startActivity(intent);

        } else if (id == R.id.nav_slideshow) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, Gallery_Activity.class);
            startActivity(intent);
        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, Compass_Activity.class);
            startActivity(intent);

        } else if (id == R.id.nav_share) {

            Intent intent = new Intent();
            intent.setClass(MainActivity.this, Upload_Activity.class);
            startActivity(intent);
        } else if (id == R.id.nav_send) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, Filter_Activity.class);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK&& data != null) {
            Uri selectedImage = data.getData();
            try {
                if (selectedImage != null) {
                    Picasso.with(MainActivity.this).load(selectedImage.toString()).placeholder(R.mipmap.placeholder).resize(100, 100).into(profile);
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    update_profile(bitmap);

                }
            }catch (IOException e){
                e.printStackTrace();
        }
        }

    }
    public void update_profile(Bitmap bm){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 0, stream);
        InputStream isBm=new ByteArrayInputStream(stream.toByteArray());
        String filename=email_check+".jpg";


        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(6*1000);
        client.setConnectTimeout(6*1000);
        client.setResponseTimeout(6*1000);
        client.setMaxRetriesAndTimeout(1,100);
        RequestParams params = new RequestParams();
        params.put("attach", isBm,filename);
        params.put("profile_url",url_profile+filename);
        params.put("email",email_check);
        client.post(url, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                String alarm = "Check your connection with the server!";
                Toast.makeText(MainActivity.this, alarm, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                if(statusCode==200){
                    String update="You are successful to update your profile!";
                    Toast.makeText(MainActivity.this,update,Toast.LENGTH_SHORT).show();
                }else{
                    String alarm="Please retry to upload your profile!";
                    Toast.makeText(MainActivity.this,alarm,Toast.LENGTH_SHORT).show();

                }

            }
        });

    }



}
