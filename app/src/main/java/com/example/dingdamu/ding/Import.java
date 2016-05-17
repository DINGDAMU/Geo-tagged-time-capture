package com.example.dingdamu.ding;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by dingdamu on 10/05/16.
 */
public class Import extends AppCompatActivity {
    Uri imageUri;
    int IMAGE_CONST = 1;
    ArrayList<String> sqluri,sqlcoordinate,sqladdress,sqltime,sqlcompass;
    PostORM p = new PostORM();
    ArrayList<ArrayList<String>> holder;
    ListView feedList;
    PostAdapter adapter;
    FloatingActionButton add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_import);
        sqluri = new ArrayList<>();
        sqladdress = new ArrayList<>();
        sqlcoordinate = new ArrayList<>();
        sqltime = new ArrayList<>();
        sqlcompass=new ArrayList<>();
        feedList = (ListView)findViewById(R.id.feedList);
        holder = new ArrayList<ArrayList<String>>();
        //拍照
        add = (FloatingActionButton)findViewById(R.id.fab1);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                imageUri = getOutputUri(IMAGE_CONST);
                if (imageUri == null) {
                    Toast.makeText(Import.this, R.string.storage_access_error, Toast.LENGTH_SHORT).show();
                } else {
                    pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(pictureIntent, IMAGE_CONST);
                }
            }
        });
        new SQLTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_goback) {
            Intent intent = new Intent();
            intent.setClass(Import.this, MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    //populates the list asynchronously
    public class SQLTask extends AsyncTask<String,String,ArrayList<ArrayList<String>>>
    {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Import.this);
            pDialog.setMessage("Loading your feed ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }


        @Override
        protected ArrayList<ArrayList<String>> doInBackground(String... params) {
                sqluri = p.getUrifromDB(Import.this);
            sqlcoordinate = p.getCoordinatesfromDB(Import.this);
            sqladdress = p.getAddressfromDB(Import.this);
            sqltime = p.getTimefromDB(Import.this);
            sqlcompass=p.getCompassfromDB(Import.this);
            holder.add(sqluri);
            holder.add(sqlcoordinate);
            holder.add(sqladdress);
            holder.add(sqltime);
            holder.add(sqlcompass);
            return holder;
        }

        //首先我们可能重写getView()，通过LayoutInflater的inflate方法映射一个自己定义的Layout布局xml加载或从xxxView中创建。
        //这些大家可能滚瓜烂熟了但是仍然很多Android开发者对于BaseAdapter中notifyDataSetChanged()方法不是很理解
        // notifyDataSetChanged方法通过一个外部的方法控制如果适配器的内容改变时需要强制调用getView来刷新每个Item的内容。
        @Override
        protected void onPostExecute(ArrayList<ArrayList<String>> arrayLists) {
            ArrayList<String> uris = sqluri;
            ArrayList<String> coordinates = sqlcoordinate;
            ArrayList<String> addresses = sqladdress;
            ArrayList<String> times = sqltime;
            ArrayList<String> compasses=sqlcompass;
            adapter = new PostAdapter(Import.this,R.layout.list_item,uris,coordinates,addresses,times,compasses);
            feedList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            pDialog.dismiss();

        }
    }

    //returns Uri as well as creates a directory for storing images locally on the device
    private Uri getOutputUri(int mediaType) {
        if (hasExternalStorage()) {
            // get external storage directory
            String appName = Import.this.getString(R.string.app_name);
            //保存图片
            File extStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),appName);

            // create subdirectory
            if(!extStorageDir.exists())
            {
                if(!extStorageDir.mkdirs())
                {
                    Toast.makeText(Import.this, "Failed to create directory", Toast.LENGTH_SHORT).show();
                }
            }
            //设置文件名
            File mFile;
            Date mCurrentDate = new Date();
            String mTimestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ITALY).format(mCurrentDate);
            String path = extStorageDir.getPath() + File.separator;
            if(mediaType == IMAGE_CONST) {
                mFile = new File(path + "FEEDIMG_" + mTimestamp + ".jpg");
            }
            else
            {
                return null;
            }
            // return the file's URI
           // mediaScanIntent.setData(contentUri);
           // this.sendBroadcast(mediaScanIntent);
            //添加照片进图册
            return Uri.fromFile(mFile);
        } else {
            return null;
        }
    }

    private boolean hasExternalStorage() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
        {
            Intent galleryAddIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            galleryAddIntent.setData(imageUri);
            sendBroadcast(galleryAddIntent);

            Intent sendIntent = new Intent(Import.this,Painting_Activity.class);
            sendIntent.setData(imageUri);
            startActivity(sendIntent);
        }
        else if(resultCode != RESULT_CANCELED)
        {
            Toast.makeText(this, "There was an error", Toast.LENGTH_SHORT).show();
        }
    }
}
