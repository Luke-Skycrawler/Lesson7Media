package com.domker.study.androidstudy;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.domker.study.androidstudy.player.VideoPlayerIJK;
import com.domker.study.androidstudy.player.VideoPlayerListener;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * 使用开源IjkPlayer播放视频
 */
public class IJKPlayerActivity extends AppCompatActivity {
    private VideoPlayerIJK ijkPlayer;
    private MediaPlayer player;
    private SurfaceHolder holder;
    private SeekBar bar;
    private TextView duration;
    private Long length=null,progress=null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        duration=findViewById(R.id.duration);
//        bar=findViewById(R.id.seek_bar);
        setContentView(R.layout.layout_ijkplayer);
        setTitle("ijkPlayer");

        ijkPlayer = findViewById(R.id.ijkPlayer);

        //加载native库
        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        } catch (Exception e) {
            this.finish();
        }
        ijkPlayer.setListener(new VideoPlayerListener());
        ijkPlayer.setVideoResource(R.raw.bytedance);
//        length=ijkPlayer.getDuration();
//        bar.setProgress(0);
        update_status();
//        ijkPlayer.setVideoPath(getVideoPath());

        findViewById(R.id.buttonPlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ijkPlayer.start();
                update_status();
            }
        });

        findViewById(R.id.buttonPause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ijkPlayer.pause();
            }
        });

        findViewById(R.id.buttonSeek).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ijkPlayer.seekTo(20 * 1000);
            }
        });
    }

    private void update_status(){
        Handler mHandler=new Handler();
        progress=ijkPlayer.getCurrentPosition()/1000;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                update_status();
            }
        },1000);
        if(!ijkPlayer.isPlaying())return;
        if(length==null||length==0)length=ijkPlayer.getDuration()/1000;
        if(duration==null)duration=findViewById(R.id.duration);
        if(bar==null)bar=findViewById(R.id.seek_bar);
//        if(duration!=null&&bar!=null){
            duration.setText(progress/60+":"+progress%60);
            Log.i("time",progress.toString());
            bar.setProgress((int)(100*progress/length));
    }
    private String getVideoPath() {
//        return "m/vwww.bilibili.coideo/BV1HQ4y1K7fy/";
//        return "http://qthttp.apple.com.edgesuite.net/1010qwoeiuryfg/sl.m3u8";
        return "android.resource://" + this.getPackageName() + "/" + R.raw.bytedance;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (ijkPlayer.isPlaying()) {
            ijkPlayer.stop();
        }

        IjkMediaPlayer.native_profileEnd();
    }
}
