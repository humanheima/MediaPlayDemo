package com.brotherd.musiclibrary;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by dumingwei on 2017/7/21.
 */
public class MusicPlayer implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnInfoListener {

    private static final String TAG = "MusicPlayer";
    public MediaPlayer mediaPlayer;

    private boolean mPaused = false;

    private AudioManager mAudioManager;
    private AudioFocusRequest mFocusRequest;
    private AudioManager.OnAudioFocusChangeListener mAudioFocusChangeListener;
    private AudioAttributes mAttribute;

    private IPlayCallback playCallback;
    private int WHAT_UPDATE_PROGRESS = 1;
    //一秒更新一次
    private int UPDATE_INTERVAL = 1000;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == WHAT_UPDATE_PROGRESS) {
                Log.i(TAG, "handleMessage: 收到消息");
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    Log.i(TAG, "handleMessage: 收到消息，回调进度");
                    int current = mediaPlayer.getCurrentPosition();
                    int duration = mediaPlayer.getDuration();
                    if (playCallback != null) {
                        playCallback.onProgress(current, duration);
                    }
                    mHandler.removeMessages(WHAT_UPDATE_PROGRESS);
                    mHandler.sendEmptyMessageDelayed(WHAT_UPDATE_PROGRESS, UPDATE_INTERVAL);
                }
            }

        }
    };

    private MusicPlayer(Context context) {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnInfoListener(this);
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mAudioFocusChangeListener = new OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_GAIN:
                        //继续播放
                        if (mPaused) {
                            resume();
                        }
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS:
                        //停止播放
                        stop();
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        //暂停播放
                        pause();
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        //混音播放
                        break;
                    default:
                        break;
                }

            }
        };

        //android 版本 5.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mAttribute = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
        }
        //android 版本 8.0
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    //.setWillPauseWhenDucked(true)
                    //.setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener(mAudioFocusChangeListener)
                    .setAudioAttributes(mAttribute)
                    .build();
        }
    }

    public void setPlayCallback(IPlayCallback playCallback) {
        this.playCallback = playCallback;
    }

    public synchronized static MusicPlayer getInstance(Context context) {
        return new MusicPlayer(context);
    }

    public void playNet(String url) {
        int status = requestAudioFocus();
        Log.i(TAG, "playNet: status = " + status);
        if (status == AudioManager.AUDIOFOCUS_GAIN) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(url);
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                Log.e(TAG, "playNet error:" + e.getMessage());
            }
        }
    }

    public void playLocal(String localPath) {
        mediaPlayer.reset();
        try {
            FileInputStream fis = new FileInputStream(localPath);
            FileDescriptor FD = fis.getFD();
            mediaPlayer.setDataSource(FD);
            mediaPlayer.prepareAsync();
            fis.close();
        } catch (IOException e) {
            Log.e(TAG, "playLocal error:" + e.getMessage());
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mPaused = false;
        }
    }

    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mPaused = true;
            stopHandler();
        }
    }

    public void resume() {
        if (mediaPlayer != null && mPaused) {
            mediaPlayer.start();
            mPaused = false;
            startGetProgress();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "onPrepared");
        mp.start();
        startGetProgress();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d(TAG, "onError：what = " + what + " , extra: " + extra);
        mp.reset();
        mPaused = false;
        abandonAudioFocus();
        stopHandler();
        return true;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(TAG, "onCompletion");
        stopHandler();
        abandonAudioFocus();
    }

    private int requestAudioFocus() {
        int status = AudioManager.AUDIOFOCUS_REQUEST_FAILED;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            status = mAudioManager.requestAudioFocus(mFocusRequest);
        } else {
            status = mAudioManager.requestAudioFocus(mAudioFocusChangeListener, AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);
        }
        Log.i(TAG, "requestAudioFocus: status = " + status);
        return status;
    }

    private void abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mAudioManager.abandonAudioFocusRequest(mFocusRequest);
        } else {
            mAudioManager.abandonAudioFocus(mAudioFocusChangeListener);
        }
    }

    public void startGetProgress() {
        mHandler.removeMessages(WHAT_UPDATE_PROGRESS);
        mHandler.sendEmptyMessage(WHAT_UPDATE_PROGRESS);
    }

    public void stopHandler() {
        mHandler.removeMessages(WHAT_UPDATE_PROGRESS);
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        Log.i(TAG, "onInfo: onInfo");
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                Log.i(TAG, "onInfo: 开始缓冲");
                if (playCallback!=null){
                    playCallback.onBufferingStart();
                }
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                Log.i(TAG, "onInfo: 结束缓冲");
                if (playCallback!=null){
                    playCallback.onBufferingEnd();
                }
                break;
            default:
                break;
        }
        return true;
    }


    public interface IPlayCallback {

        void onStart();

        void onBufferingStart();
        void onBufferingEnd();

        void onPaused();

        void onStopped();

        void onCompleted();

        void onError();

        void onProgress(int currentPosition, int duration);


    }


}
