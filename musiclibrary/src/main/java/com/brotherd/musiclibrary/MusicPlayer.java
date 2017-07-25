package com.brotherd.musiclibrary;

import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

/**
 * Created by dumingwei on 2017/7/21.
 */
public class MusicPlayer implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private final String TAG = getClass().getSimpleName();

    public MediaPlayer mediaPlayer;

    private MusicPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnCompletionListener(this);
    }

    public static MusicPlayer getInstance() {

        return LazyLoader.INSTANCE;
    }

    private static class LazyLoader {

        private static final MusicPlayer INSTANCE = new MusicPlayer();
    }

    public void play(String url) {
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
        } catch (IOException e) {
            Log.e(TAG, "play error:" + e.getMessage());
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "onPrepared");
        mp.start();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d(TAG, "onError");
        mp.reset();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(TAG, "onCompletion");
    }


}
