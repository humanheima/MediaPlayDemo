package com.humanheima.videoplayerdemo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.humanheima.videoplayerdemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_start_video_activity)
    Button btnStartVideoActivity;
    @BindView(R.id.btn_start_live_activity)
    Button btnStartLiveActivity;

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
}
