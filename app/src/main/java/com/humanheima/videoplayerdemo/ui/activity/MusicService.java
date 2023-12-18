package com.humanheima.videoplayerdemo.ui.activity;

import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.media.browse.MediaBrowser;
import android.media.browse.MediaBrowser.MediaItem;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.service.media.MediaBrowserService;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicService extends MediaBrowserService {

    private static final String TAG = "MusicService";


    private MediaSession mSession;
    private PlaybackState mPlaybackState;

    private MediaPlayer mMediaPlayer;
    private String musicUrl = "https://aod.cos.tx.xmcdn.com/storages/c409-audiofreehighqps/8F/76/CMCoOSEEHN8GAACRUACQNuTJ.mp3";


    @Override
    public void onCreate() {
        super.onCreate();
        mPlaybackState = new PlaybackState.Builder()
                .setState(PlaybackState.STATE_NONE, 0, 1.0f)
                .build();

        mSession = new MediaSession(this, "MusicService");
        mSession.setCallback(new MediaSession.Callback() {
            @Override
            public void onPlay() {
                super.onPlay();
                //开始播放
                if (mPlaybackState.getState() == PlaybackState.STATE_PAUSED) {
                    mMediaPlayer.start();
                    mPlaybackState = new PlaybackState.Builder()
                            .setState(PlaybackState.STATE_PLAYING, 0, 1.0f)
                            .build();
                    mSession.setPlaybackState(mPlaybackState);
                }

            }

            @Override
            public void onPause() {
                super.onPause();
                //暂停播放
                Log.i(TAG, "onPause: ");
                if (mPlaybackState.getState() == PlaybackState.STATE_PLAYING) {
                    mMediaPlayer.pause();
                    mPlaybackState = new PlaybackState.Builder()
                            .setState(PlaybackState.STATE_PAUSED, 0, 1.0f)
                            .build();
                    mSession.setPlaybackState(mPlaybackState);
                }
            }

            @Override
            public void onPlayFromUri(Uri uri, Bundle extras) {
                super.onPlayFromUri(uri, extras);
                Log.e(TAG, "onPlayFromUri");
                try {
                    switch (mPlaybackState.getState()) {
                        case PlaybackState.STATE_PLAYING:
                        case PlaybackState.STATE_PAUSED:
                        case PlaybackState.STATE_NONE:
                            mMediaPlayer.reset();
                            mMediaPlayer.setDataSource(MusicService.this, uri);
                            mMediaPlayer.prepare();//准备同步
                            mPlaybackState = new PlaybackState.Builder()
                                    .setState(PlaybackState.STATE_CONNECTING, 0, 1.0f)
                                    .build();
                            mSession.setPlaybackState(mPlaybackState);
                            //我们可以保存当前播放音乐的信息，以便客户端刷新UI
                            mSession.setMetadata(new MediaMetadata.Builder()
                                    .putString(MediaMetadata.METADATA_KEY_TITLE, extras.getString("title"))
                                    .build()
                            );
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                //下一首
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                //上一首
            }

            @Override
            public void onStop() {
                super.onStop();
                //停止播放
            }

            @Override
            public void onSeekTo(long pos) {
                super.onSeekTo(pos);
                //拖动进度条
            }
        });
        mSession.setFlags(MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mSession.setPlaybackState(mPlaybackState);

        //设置token后会触发MediaBrowserCompat.ConnectionCallback的回调方法
        //表示MediaBrowser与MediaBrowserService连接成功
        setSessionToken(mSession.getSessionToken());

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(android.media.MediaPlayer mp) {
                //开始播放
                mMediaPlayer.start();
                //设置播放状态
                mSession.setPlaybackState(new PlaybackState.Builder()
                        .setState(PlaybackState.STATE_PLAYING, 0, 1.0f)
                        .build());
            }
        });

        //设置播放完成监听
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(android.media.MediaPlayer mp) {
                //设置播放状态
                mSession.setPlaybackState(new PlaybackState.Builder()
                        .setState(PlaybackState.STATE_NONE, 0, 1.0f)
                        .build());
                mMediaPlayer.reset();
            }
        });

    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        return new BrowserRoot("root", null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaItem>> result) {
        Log.e(TAG, "onLoadChildren--------");
        //将信息从当前线程中移除，允许后续调用sendResult方法
        result.detach();

        //我们模拟获取数据的过程，真实情况应该是异步从网络或本地读取数据
        MediaMetadata metadata = new MediaMetadata.Builder()
                .putString(MediaMetadata.METADATA_KEY_MEDIA_ID, musicUrl)
                .putString(MediaMetadata.METADATA_KEY_TITLE, "圣诞歌")
                .build();
        ArrayList<MediaBrowser.MediaItem> mediaItems = new ArrayList<>();
        mediaItems.add(createMediaItem(metadata));

        //向Browser发送数据
        result.sendResult(mediaItems);


    }

    private MediaBrowser.MediaItem createMediaItem(MediaMetadata metadata) {
        return new MediaBrowser.MediaItem(
                metadata.getDescription(),
                MediaBrowser.MediaItem.FLAG_PLAYABLE
        );
    }

}
