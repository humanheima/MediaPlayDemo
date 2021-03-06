package com.humanheima.videoplayerdemo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.humanheima.videoplayerdemo.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_start_video_activity)
    public void startVideoActivity() {
        startActivity(new Intent(MainActivity.this, VideoActivity.class));
    }

    @OnClick(R.id.btn_start_live_activity)
    public void startLiveActivity() {
        startActivity(new Intent(MainActivity.this, LiveActivity.class));
    }

    @OnClick(R.id.btn_start_music_activity)
    public void startMusicActivity() {
        PlayMusicActivity.launch(this);
    }
}
