package com.humanheima.videoplayerdemo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.brotherd.musiclibrary.MusicPlayer;
import com.humanheima.videoplayerdemo.R;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlayMusicActivity extends AppCompatActivity {


    private final String TAG = getClass().getSimpleName();
    private String localPath;
    private String networkUrl;
    private MusicPlayer musicPlayer;

    public static void launch(Context context) {
        Intent starter = new Intent(context, PlayMusicActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);
        ButterKnife.bind(this);
        localPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/DreamItPossible.mp3";
        Log.e(TAG, localPath);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "DreamItPossible.mp3");
        localPath = file.getPath();
        Log.e(TAG, localPath);
       /* Log.e(TAG, file.getPath());
        String[] files = file.list();
        if (files != null) {
            for (String file1 : files) {
                Log.e(TAG, file1);
            }
        } else {
            Log.e(TAG, "files==null");
        }*/
        musicPlayer = MusicPlayer.getInstance();
    }

    @OnClick({R.id.btn_local, R.id.btn_network, R.id.btn_stop})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_local:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        musicPlayer.play("http://file.kuyinyun.com/group1/M00/90/B7/rBBGdFPXJNeAM-nhABeMElAM6bY151.mp3");
                    }
                }).start();
                break;
            case R.id.btn_network:
                break;
            case R.id.btn_stop:
                musicPlayer.stop();
                break;
            default:
                break;
        }
    }
}
