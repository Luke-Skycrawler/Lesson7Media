package com.domker.study.androidstudy;

import android.app.Activity;
//import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.graphics.PathUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;

public class CameraView extends Activity {
    private Camera mCamera=null;
    private CameraPreview mPreview;
    private MediaRecorder mMediaRecorder;
//    private ImageView preview;
    private Button trigger;
    private Camera.PictureCallback mPicture;
//    SurfaceHolder mHolder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        preview=findViewById(R.id.image_preview);
//        mSurfaceView= findViewById(R.id.surfaceview);
        setContentView(R.layout.activiy_diycamera);

        mPicture = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                FileOutputStream fos = null;
                String filePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                        + File.separator + "1.jpg";
                File file = new File(filePath);
                try {
                    fos = new FileOutputStream(file);
                    fos.write(data);
                    fos.flush();
                    fos.close();
                    ImageView preview=findViewById(R.id.image_preview);
                    Bitmap bitmap= BitmapFactory.decodeFile(filePath);
                    preview.bringToFront();
                    preview.setVisibility(View.VISIBLE);
                    preview.setImageBitmap(bitmap);
                    mCamera.startPreview();
//                    Bitmap rotateBitmap= PathUtils.rotateImage(bitmap,filePath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        trigger=findViewById(R.id.camera_trigger);
        trigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    mCamera.takePicture(null,null,mPicture);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mCamera=Camera.open();
        if (mCamera == null) initCamera();
        mPreview=new CameraPreview(this,mCamera);
        ConstraintLayout prev=findViewById(R.id.Camera_Layout);
        prev.addView(mPreview);
        mCamera.startPreview();
    }

    private void initCamera() {
        try{
            mCamera = Camera.open();
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPictureFormat(ImageFormat.JPEG);
//            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
//            parameters.set("orientation", "portrait");
//            parameters.set("rotation", 90);
            mCamera.setParameters(parameters);
            mCamera.setDisplayOrientation(90);
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        mCamera.stopPreview();
//        if(mMediaRecorder!=null){
//            mMediaRecorder.reset();
//            mMediaRecorder.release();
//            mMediaRecorder=null;
//            mCamera.lock();
//        }
//        if(mCamera!=null){
//            mCamera.release();
//            mCamera=null;
//        }
    }


}
