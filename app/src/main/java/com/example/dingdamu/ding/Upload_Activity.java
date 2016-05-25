package com.example.dingdamu.ding;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by dingdamu on 23/05/16.
 */
public class Upload_Activity extends AppCompatActivity {
    Uri imageUri;
    int IMAGE_CONST = 1;
    ArrayList<String> sqluri,sqlcoordinate,sqladdress,sqltime;
    PostORM p = new PostORM();
    ArrayList<ArrayList<String>> holder;
    ListView feedList;
    PostAdapter adapter;
    FloatingActionButton add,add2;
    Output op=new Output();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_import);
        sqluri = new ArrayList<>();
        sqladdress = new ArrayList<>();
        sqlcoordinate = new ArrayList<>();
        sqltime = new ArrayList<>();
        feedList = (ListView)findViewById(R.id.feedList);
        holder = new ArrayList<ArrayList<String>>();
        //拍照
        add = (FloatingActionButton)findViewById(R.id.fab1);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File mFile=op.getOutputUri(IMAGE_CONST,Upload_Activity.this);
                imageUri = Uri.fromFile(mFile);
                if (imageUri == null) {
                    Toast.makeText(Upload_Activity.this, R.string.storage_access_error, Toast.LENGTH_SHORT).show();
                } else {
                    pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(pictureIntent, IMAGE_CONST);
                }
            }
        });

        add2 = (FloatingActionButton)findViewById(R.id.fab2);
        add2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Upload_Activity.this,Network_compass_camera_Activity.class);
                startActivity(intent);
                Upload_Activity.this.finish();

            }
        });

    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
        {   //Intent.ACTION_MEDIA_SCANNER_SCAN_FILE：扫描指定文件


            Intent galleryAddIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            galleryAddIntent.setData(imageUri);
            sendBroadcast(galleryAddIntent);

            Intent sendIntent = new Intent(Upload_Activity.this,Network_painting_Activity.class);
            sendIntent.setData(imageUri);
            startActivity(sendIntent);
            Upload_Activity.this.finish();
        }
        else if(resultCode != RESULT_CANCELED)
        {
            Toast.makeText(this, "There was an error", Toast.LENGTH_SHORT).show();
        }
    }
}
