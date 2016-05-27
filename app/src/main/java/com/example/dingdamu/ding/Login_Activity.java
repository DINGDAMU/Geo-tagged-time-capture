package com.example.dingdamu.ding;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by dingdamu on 26/05/16.
 */
public class Login_Activity extends AppCompatActivity{
    private EditText LoginEmail;
    private EditText LoginPassword;
    public static String name;
    public static String email;
    final String url="http://10.196.161.55/login_insert.php";
    Button register;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);

        // Get Reference to variables
        LoginEmail = (EditText) findViewById(R.id.email);
        LoginPassword = (EditText) findViewById(R.id.password);
        register=(Button) findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent();
                intent.setClass(Login_Activity.this, Register_Activity.class);
                startActivity(intent);


                Login_Activity.this.finish();
            }
        });


    }
    public void checkLogin(View arg0) {

        if(!isNetworkAvailable()){
            Toast.makeText(Login_Activity.this,"Please connect the Internet!",Toast.LENGTH_SHORT).show();
        }else {
            Login();
        }
    }

    public void Login(){
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(6*1000);
        RequestParams params = new RequestParams();
        params.put("email",LoginEmail.getText().toString());
        params.put("password",LoginPassword.getText().toString() );

        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int  statusCode, Header[] headers, JSONObject response) {
                try{
                    JSONArray array = response.getJSONArray("login");
                    if(array.length()==0){
                        Toast.makeText(Login_Activity.this, "Login fail!", Toast.LENGTH_LONG).show();

                    }else {
                        JSONObject jp = array.getJSONObject(0);
                         name = jp.getString("username");
                         email = jp.getString("email");







                        Toast.makeText(Login_Activity.this, "Login success!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent();
                        intent.setClass(Login_Activity.this, MainActivity.class);
                        startActivity(intent);
                        Login_Activity.this.finish();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }



            }
        });

    }
    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo.isConnected()) {
            return true;
        }
        return false;
    }
}
