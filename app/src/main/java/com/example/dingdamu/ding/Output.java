package com.example.dingdamu.ding;

import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;
import android.content.Context;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by dingdamu on 23/05/16.
 */
public class Output {
    //returns Uri as well as creates a directory for storing images locally on the device
    public File getOutputUri(int mediaType,Context context) {
        if (hasExternalStorage()) {
            // get external storage directory
            String appName = context.getString(R.string.app_name);
            //保存图片
            File extStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),appName);

            // create subdirectory
            if(!extStorageDir.exists())
            {
                if(!extStorageDir.mkdirs())
                {
                    Toast.makeText(context, "Failed to create directory", Toast.LENGTH_SHORT).show();
                }
            }
            //设置文件名
            File mFile;
            Date mCurrentDate = new Date();
            String mTimestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ITALY).format(mCurrentDate);
            String path = extStorageDir.getPath() + File.separator;
            if(mediaType == 1) {
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
            return mFile;
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
}
