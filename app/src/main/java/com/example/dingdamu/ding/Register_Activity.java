package com.example.dingdamu.ding;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cz.msebera.android.httpclient.Header;

/**
 * Created by dingdamu on 27/05/16.
 */

public class Register_Activity extends AppCompatActivity {
    private EditText RegisterEmail;
    private EditText RegisterPassword;
    private EditText RegisterUsername;
     Button  RegisterUrl;
    Button register;
    final int gallery_const=2;
    Uri selectedImage;
    Bitmap bitmap;

    final String url="https://php-dingdamu.rhcloud.com/register.php";
    final String url_profile="https://php-dingdamu.rhcloud.com/profile/";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_register);


        RegisterEmail=(EditText)findViewById(R.id.email);
        RegisterPassword=(EditText)findViewById(R.id.password);
        RegisterUsername=(EditText)findViewById(R.id.username);
        RegisterUrl=(Button)findViewById(R.id.profile_url);
        register=(Button) findViewById(R.id.register);
        RegisterUrl.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, gallery_const);
            }
        });

        register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(RegisterEmail.getText().length()<8||RegisterPassword.getText().length()<8||RegisterUsername.getText().length()<8){
                    String alarm="Email/Username/Password should be at least 8 characters!";
                    Toast.makeText(Register_Activity.this,alarm,Toast.LENGTH_SHORT).show();
                }
                else{
                    register(bitmap);

                }
            }
        });

    }
    public void register(Bitmap bm){
        if (bm == null) {
            Toast.makeText(Register_Activity.this, "Please upload one photo as your profile", Toast.LENGTH_SHORT).show();
        }else{
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 60, stream);
            InputStream isBm = new ByteArrayInputStream(stream.toByteArray());
            String filename = RegisterEmail.getText().toString() + ".jpg";


            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(6 * 1000);
            RequestParams params = new RequestParams();
            params.put("email", RegisterEmail.getText().toString());
            params.put("password", RegisterPassword.getText().toString());
            params.put("username", RegisterUsername.getText().toString());
            params.put("attach", isBm, filename);
            params.put("profile_url", url_profile + filename);
            client.post(url, params, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    String alarm = "Check your connection with the server!";
                    Toast.makeText(Register_Activity.this, alarm, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    if (responseString.equalsIgnoreCase("Duplicated!")) {
                        String alarm = "This email has been registered before!";
                        Toast.makeText(Register_Activity.this, alarm, Toast.LENGTH_SHORT).show();
                    } else if (responseString.equalsIgnoreCase("1 record added")) {
                        Toast.makeText(Register_Activity.this, "Register success!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.setClass(Register_Activity.this, Login_Activity.class);
                        startActivity(intent);
                        Register_Activity.this.finish();

                    } else {
                        Toast.makeText(Register_Activity.this, responseString, Toast.LENGTH_LONG).show();
                    }

                }
            });
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK&& data != null){
           selectedImage = data.getData();
          try {
              if (selectedImage != null) {
                  bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
              }
          }catch(IOException e){
              e.printStackTrace();
          }

        }

    }
}
