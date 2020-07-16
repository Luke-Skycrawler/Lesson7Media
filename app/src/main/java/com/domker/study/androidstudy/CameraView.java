package com.domker.study.androidstudy;

import android.app.Activity;
//import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.CamcorderProfile;
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
import java.text.SimpleDateFormat;
import java.util.Date;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;

public class CameraView extends Activity {
    private Camera mCamera=null;
    private CameraPreview mPreview;
    private MediaRecorder mMediaRecorder;
//    private ImageView preview;
    private Button trigger,videoshoot;
    private Camera.PictureCallback mPicture;
    private boolean isRecording=false;
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
                    mCamera.startPreview();
                    Bitmap rotateBitmap= rotateBitmap(bitmap,90);
                    preview.setImageBitmap(rotateBitmap);
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
        videoshoot=findViewById(R.id.videoshoot);
        videoshoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    // stop recording and release camera
                    mMediaRecorder.stop();  // stop the recording
                    mMediaRecorder.release(); // release the MediaRecorder object
                    mCamera.lock();         // take camera access back from MediaRecorder

                    // inform the user that recording has stopped
                    videoshoot.setText("record");
                    isRecording = false;
                } else {
                    // initialize video camera
                    if (prepareVideoRecorder()) {
                        // Camera is available and unlocked, MediaRecorder is prepared,
                        // now you can start recording
                        mMediaRecorder.start();

                        // inform the user that recording has started
                        videoshoot.setText("Stop");
                        isRecording = true;
                    } else {
                        // prepare didn't work, release the camera
                        mMediaRecorder.release();
                        // inform user
                    }
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
//        prepareVideoRecorder();
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
    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        if (degrees == 0 || null == bitmap) {
            return bitmap;
        }
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
        Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap.recycle();
        return bmp;
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
    private boolean prepareVideoRecorder(){

        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        // Step 4: Set output file
        String mp4path=getOutputMediaPath();
        mMediaRecorder.setOutputFile(mp4path);

        // Step 5: Set the preview output
        mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());
        mMediaRecorder.setOrientationHint(90);

        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (Exception e) {
            mMediaRecorder.release();
            return false;
        }
        return true;
    }
    private  String getOutputMediaPath(){
        File mediaStorageDir=getExternalFilesDir(Environment.DIRECTORY_DCIM);
        String timestamp=new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile=new File(mediaStorageDir,"IMG_"+timestamp+".mp4");
        if(!mediaFile.exists())mediaFile.getParentFile().mkdirs();
        return mediaFile.getAbsolutePath();
    }

}
