package com.humanheima.videoplayerdemo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import androidx.appcompat.app.AppCompatActivity;
import com.humanheima.videoplayerdemo.R;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_test_media_session).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                TestMediaSessionActivity.start(MainActivity.this);
            }
        });

        findViewById(R.id.btn_test_listen_call).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                TestListenCallActivity.launch(MainActivity.this);
            }
        });
    }

    public void startVideoActivity() {
        startActivity(new Intent(MainActivity.this, VideoActivity.class));
    }

    public void startLiveActivity() {
        startActivity(new Intent(MainActivity.this, LiveActivity.class));
    }

    public void startMusicActivity() {
        PlayMusicActivity.launch(this);
    }
}
