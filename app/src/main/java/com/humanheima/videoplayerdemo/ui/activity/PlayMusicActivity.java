package com.humanheima.videoplayerdemo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import android.widget.ProgressBar;
import com.brotherd.musiclibrary.MusicPlayer;
import com.brotherd.musiclibrary.MusicPlayer.IPlayCallback;
import com.humanheima.videoplayerdemo.R;

import com.humanheima.videoplayerdemo.ui.widget.RingProgressView;
import java.io.File;

import butterknife.ButterKnife;

/**
 * Created by p_dmweidu on 2022/12/29
 * Desc: 测试简单的音乐播放功能。
 */
public class PlayMusicActivity extends AppCompatActivity {


    private final String TAG = getClass().getSimpleName();
    private String localPath;
    private String networkUrl;
    private MusicPlayer musicPlayer;
    private RingProgressView ringProgressView;

    private ProgressBar progressLoading;

    public static void launch(Context context) {
        Intent starter = new Intent(context, PlayMusicActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);
        progressLoading = findViewById(R.id.progress_loading);
        ButterKnife.bind(this);
        localPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath()
                + "/DreamItPossible.mp3";
        Log.e(TAG, localPath);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                "DreamItPossible.mp3");
        localPath = file.getPath();
        Log.e(TAG, localPath);
        ringProgressView = findViewById(R.id.ring_progress_view);
        musicPlayer = MusicPlayer.getInstance(this);
        musicPlayer.setPlayCallback(new IPlayCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onBufferingStart() {
                if (progressLoading != null) {
                    progressLoading.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onBufferingEnd() {
                if (progressLoading != null) {
                    progressLoading.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPaused() {

            }

            @Override
            public void onStopped() {

            }

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError() {

            }

            @Override
            public void onProgress(int currentPosition, int duration) {
                ringProgressView.setProgress(currentPosition, duration);
            }
        });
    }

    public void playLocal(View view) {
        final String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath()
                + "/DreamItPossible.mp3";
        new Thread(new Runnable() {
            @Override
            public void run() {
                musicPlayer.playLocal(path);
            }
        }).start();
    }

    public void playNet(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                musicPlayer.playNet("http://mp3.9ku.com/hot/2014/07-16/642431.mp3");
            }
        }).start();
    }

    public void pause(View view) {
        musicPlayer.pause();
    }

    public void resume(View view) {
        musicPlayer.resume();
    }

    public void stopPlay(View view) {
        musicPlayer.stop();
    }
}
