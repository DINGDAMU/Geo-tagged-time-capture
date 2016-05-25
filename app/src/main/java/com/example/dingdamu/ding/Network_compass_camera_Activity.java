package com.example.dingdamu.ding;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by dingdamu on 24/05/16.
 */
public class Network_compass_camera_Activity extends Activity{
    private View layout;
    private Camera camera;
    private Camera.Parameters parameters;
    private Compass compass;
    Process process = null;
    private SurfaceHolder holder;
    int IMAGE_CONST=1;
    Uri imageUri ;
    private ProgressDialog pDialog;
    Output op=new Output();











    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 显示界面
        setContentView(R.layout.act_compass_camera);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.flags=WindowManager.LayoutParams.FLAG_FULLSCREEN;

        Compass.mText=(TextView)findViewById(R.id.compass_information);
        compass = new Compass(this);



        compass.arrowView = (ImageView) findViewById(R.id.main_image_hands);


        layout = this.findViewById(R.id.buttonLayout);


        SurfaceView surfaceView=(SurfaceView) findViewById(R.id.surfaceView);
        holder=surfaceView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.setFixedSize(176, 144);	//设置Surface分辨率
        holder.setKeepScreenOn(true);// 屏幕常亮
        holder.addCallback(new SurfaceCallback());//为SurfaceView的句柄添加一个回调函数


        try{
            process = Runtime.getRuntime().exec("su");
        }catch(IOException e){
            e.printStackTrace();
        }




        Button add = (Button)findViewById(R.id.takepicture);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new InsertTask().execute();



            }
        });


    }







    /**
     * 图片被点击触发的时间
     *
     * @param
     */



    private final class SurfaceCallback implements SurfaceHolder.Callback {

        //拍照状态变化时调用该方法
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            parameters = camera.getParameters(); // 获取各项参数
            parameters.setPictureFormat(PixelFormat.JPEG); // 设置图片格式
            parameters.setPreviewSize(width, height); // 设置预览大小
            parameters.setPreviewFrameRate(5);    //设置每秒显示4帧
            parameters.setPictureSize(width, height); // 设置保存的图片尺寸
            parameters.setJpegQuality(80); // 设置照片质量
        }

        // 开始拍照时调用该方法
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera = Camera.open(); // 打开摄像头
                camera.setPreviewDisplay(holder); // 设置用于显示拍照影像的SurfaceHolder对象
                camera.setDisplayOrientation(getPreviewDegree(Network_compass_camera_Activity.this));
                camera.startPreview(); // 开始预览
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        // 停止拍照时调用该方法
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (camera != null) {
                camera.release(); // 释放照相机
                camera = null;
            }
        }
    }


    /**
     * 点击手机屏幕是，显示两个按钮
     */
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                layout.setVisibility(ViewGroup.VISIBLE); // 设置视图可见
                break;
        }
        return true;
    }



    // 提供一个静态方法，用于根据手机方向获得相机预览画面旋转的角度
    public static int getPreviewDegree(Activity activity) {
        // 获得手机的方向
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degree = 0;
        // 根据手机的方向计算相机预览画面应该选择的角度
        switch (rotation) {
            case Surface.ROTATION_0:
                degree = 90;
                break;
            case Surface.ROTATION_90:
                degree = 0;
                break;
            case Surface.ROTATION_180:
                degree = 270;
                break;
            case Surface.ROTATION_270:
                degree = 180;
                break;
        }
        return degree;
    }




    @Override
    protected void onStart() {
        super.onStart();
        compass.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        compass.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        compass.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        compass.stop();
    }

    //returns Uri as well as creates a directory for storing images locally on the device
    private File getOutputUri(int mediaType) {
        if (hasExternalStorage()) {
            // get external storage directory
            String appName = Network_compass_camera_Activity.this.getString(R.string.app_name);
            //保存图片
            File extStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),appName);

            // create subdirectory
            if(!extStorageDir.exists())
            {
                if(!extStorageDir.mkdirs())
                {
                    Toast.makeText(Network_compass_camera_Activity.this, "Failed to create directory", Toast.LENGTH_SHORT).show();
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
    private void takephoto(Process process,File mFile){
        try {
            OutputStream outputStream = null;
            try {
                outputStream = process.getOutputStream();
                outputStream.write(("screencap -p " + mFile).getBytes("ASCII"));
                outputStream.flush();
            } catch (Exception e) {
            } finally {
                if (outputStream != null) {
                    outputStream.close();
                }
            }
            process.waitFor();
        } catch (Exception e) {
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }
    //populates the list asynchronously
    public class InsertTask extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Network_compass_camera_Activity.this);
            pDialog.setMessage("Loading.....");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }


        @Override
        protected String doInBackground(String... params) {
            File mFile=op.getOutputUri(IMAGE_CONST,Network_compass_camera_Activity.this);
            imageUri = Uri.fromFile(mFile);
            String result;
            if (imageUri == null) {
                Toast.makeText(Network_compass_camera_Activity.this, R.string.storage_access_error, Toast.LENGTH_SHORT).show();
                result = "false";
            } else {


                takephoto(process, mFile);
                Intent galleryAddIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                galleryAddIntent.setData(imageUri);
                sendBroadcast(galleryAddIntent);
                result = "true";


            }

            return result;
        }

        //首先我们可能重写getView()，通过LayoutInflater的inflate方法映射一个自己定义的Layout布局xml加载或从xxxView中创建。
        //这些大家可能滚瓜烂熟了但是仍然很多Android开发者对于BaseAdapter中notifyDataSetChanged()方法不是很理解
        // notifyDataSetChanged方法通过一个外部的方法控制如果适配器的内容改变时需要强制调用getView来刷新每个Item的内容。
        @Override
        protected void onPostExecute(String result) {

            if (result.equalsIgnoreCase("true")) {
                String success="success!";
                Toast.makeText(Network_compass_camera_Activity.this,success, Toast.LENGTH_SHORT).show();

                Intent sendIntent = new Intent(Network_compass_camera_Activity.this, Network_painting_Activity.class);
                sendIntent.setData(imageUri);
                startActivity(sendIntent);
                Network_compass_camera_Activity.this.finish();




            }
            else{
                String alarm="failed to take the photo";
                Toast.makeText(Network_compass_camera_Activity.this,alarm , Toast.LENGTH_SHORT).show();

            }
            pDialog.dismiss();

        }


    }

}
