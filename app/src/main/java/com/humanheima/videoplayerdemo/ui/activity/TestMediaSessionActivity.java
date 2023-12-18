package com.humanheima.videoplayerdemo.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadata;
import android.media.browse.MediaBrowser;
import android.media.browse.MediaBrowser.ConnectionCallback;
import android.media.browse.MediaBrowser.MediaItem;
import android.media.session.MediaController;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.humanheima.videoplayerdemo.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by p_dmweidu on 2023/12/18
 * Desc:
 */
public class TestMediaSessionActivity extends AppCompatActivity {


    private static final String TAG = "TestMediaSessionActivit";

    private RecyclerView recyclerView;
    private List<MediaItem> list;
    private TestMediaSessionAdapter demoAdapter;
    private LinearLayoutManager layoutManager;

    private MediaBrowser mediaBrowser;

    private MediaController mMediaController;

    private Button btnStart;
    private Button btnPause;
    private TextView tvTitle;
    private ConnectionCallback connectionCallback;


    public static void start(Context context) {
        Intent starter = new Intent(context, TestMediaSessionActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_media_session);

        tvTitle = findViewById(R.id.tv_title);
        btnStart = findViewById(R.id.btn_start);
        btnPause = findViewById(R.id.btn_pause);

        btnStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                handlerPlayEvent();
            }
        });

        createConnectionCallback();

        mediaBrowser = new MediaBrowser(this, new ComponentName(this, MusicService.class),
                connectionCallback, null);

        list = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        demoAdapter = new TestMediaSessionAdapter(this, list);

        recyclerView = findViewById(R.id.rv_recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(demoAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mediaBrowser.connect();
    }


    private void createConnectionCallback() {
        connectionCallback = new ConnectionCallback() {
            @Override
            public void onConnected() {
                super.onConnected();

                if (mediaBrowser.isConnected()) {
                    String mediaId = mediaBrowser.getRoot();
                    mediaBrowser.unsubscribe(mediaId);

                    mediaBrowser.subscribe(mediaId, new MediaBrowser.SubscriptionCallback() {
                        @Override
                        public void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaItem> children) {
                            super.onChildrenLoaded(parentId, children);
                            list.addAll(children);
                            //demoAdapter.notifyDataSetChanged();
                            //children 即为Service发送回来的媒体数据集合
                            for (MediaItem item : children) {
                                Log.e(TAG, item.getDescription().getTitle().toString());
                                list.add(item);
                            }
                            //在onChildrenLoaded可以执行刷新列表UI的操作
                            demoAdapter.notifyDataSetChanged();
                        }
                    });
                }

                mMediaController = new MediaController(TestMediaSessionActivity.this,
                        mediaBrowser.getSessionToken());
                mMediaController.registerCallback(new MediaController.Callback() {
                    @Override
                    public void onPlaybackStateChanged(PlaybackState state) {
                        super.onPlaybackStateChanged(state);
                        //在这里可以更新UI
                        Log.e(TAG, "onPlaybackStateChanged: " + state.getState());
                        switch (state.getState()) {

                        }
                    }

                    @Override
                    public void onMetadataChanged(@Nullable MediaMetadata metadata) {
                        super.onMetadataChanged(metadata);
                        Log.i(TAG, "onMetadataChanged: ");
                    }
                });
            }
        };
    }

    /**
     * 处理播放按钮事件
     */
    private void handlerPlayEvent() {
        switch (mMediaController.getPlaybackState().getState()) {
            case PlaybackState.STATE_PLAYING:
                mMediaController.getTransportControls().pause();
                break;
            case PlaybackState.STATE_PAUSED:
                mMediaController.getTransportControls().play();
                break;
            default:
                mMediaController.getTransportControls().playFromSearch("", null);
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mediaBrowser.disconnect();
    }
}
