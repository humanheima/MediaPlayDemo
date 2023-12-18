package com.humanheima.videoplayerdemo.ui.base;

import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBarWrapper;
import com.humanheima.videoplayerdemo.R;
import com.humanheima.videoplayerdemo.receiver.NetChangeReceiver;
import com.humanheima.videoplayerdemo.ui.widget.HorizontalProgressView;
import com.humanheima.videoplayerdemo.ui.widget.dialog.PromptDialog;
import com.humanheima.videoplayerdemo.ui.widget.video.VideoController;
import com.humanheima.videoplayerdemo.ui.widget.video.VideoView;
import com.humanheima.videoplayerdemo.util.Debug;
import com.humanheima.videoplayerdemo.util.ImageUtil;
import com.humanheima.videoplayerdemo.util.NetWorkUtil;
import com.humanheima.videoplayerdemo.util.SpUtil;
import com.humanheima.videoplayerdemo.util.ToastUtil;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by chenchao on 16/10/17.
 * cc@cchao.org
 */
public abstract class BaseVideoActivity extends BaseActivity implements IMediaPlayer.OnInfoListener,
        IMediaPlayer.OnErrorListener, IMediaPlayer.OnPreparedListener, IMediaPlayer.OnCompletionListener,
        IMediaPlayer.OnErrorCompletionListener {

    private final String TAG = "BaseVideoActivity";

    VideoView videoView;
    public VideoController videoController;
    public RelativeLayout rlVideoLoading;
    public RelativeLayout rlImgVideo;
    public ImageView imgVideo;
    public ImageView imgLiveAction;
    public View viewPrepared;

    VerticalSeekBarWrapper verticalSeekBarWrapper;
    VerticalSeekBar verticalSeekBar;

    //音量
    RelativeLayout rlVolumeController;
    HorizontalProgressView horizontalProgressView;

    //监听网络变化
    private NetChangeReceiver receiver;

    private WindowManager windowManager;

    //手势监听音量
    private GestureDetector gestureDetector;
    private AudioManager audioManager;
    //最大声音
    private int maxVolume;
    //当前声音
    private int nowVolume = -1;

    protected boolean isFullScreen = false;

    private int currentDuration = 0;

    //2g/3g网络提醒
    private PromptDialog promptDialog;

    //能否在非wifi情况播放
    private boolean canPlayNoWifi;

    //视频url
    private String videoUrl;

    private MyPopWindow videoCropPop;
    private ImageView imgCrop;
    private Bitmap cropBitmap;

    //是否分享返回resume
    private boolean isShareResume = false;

    //是否已经开始了播放
    private boolean isStartPlaying = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    rlVolumeController.setVisibility(View.GONE);
                    break;
                case 1:
                    //rlBrightController.setVisibility(View.GONE);
                    break;
            }

            super.handleMessage(msg);
        }
    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (videoController != null && videoController.isLock()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            int width = windowManager.getDefaultDisplay().getWidth();
            int height = windowManager.getDefaultDisplay().getHeight();
            if (width > height) {
                setOpenFlingClose(false);
                isFullScreen = true;
                rlImgVideo.setLayoutParams(
                        new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, screenWidth));
                videoView.setmRootViewHeight(screenWidth);
                videoView.setVideoLayout(VideoView.VIDEO_LAYOUT_STRETCH);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else {

                isFullScreen = false;
                rlVolumeController.setVisibility(View.GONE);
                verticalSeekBarWrapper.setVisibility(View.GONE);
                setVideoSize();
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
            changeView(isFullScreen);
            videoController.setFullOrSmallScreen();
        }
    }

    @Override
    protected void initData() {
        windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        gestureDetector = new GestureDetector(this, new VideoPlayGestureListener());
        canPlayNoWifi = SpUtil.getInstance().getAllowMoblie();
        verticalSeekBar.setMax(100);

        setVideoSize();

        videoView.setOnPreparedListener(this);
        videoView.setOnErrorListener(this);
        videoView.setOnInfoListener(this);
        videoView.setOnCompletionListener(this);
        videoView.setOnErrorCompletionListener(this);

        videoController.setPlayerController(this, videoView, isLive());

        videoController.setShareAndCropListener(new VideoController.ShareAndCropListener() {
            @Override
            public void openShare(View view) {
                //openSharePop(view);
            }

            @Override
            public void openCrop(View view) {
                openCropPop(view);
            }
        });
        videoController.setShowAndHideControllerListener(new VideoController.ShowAndHideControllerListener() {
            @Override
            public void hide() {
                verticalSeekBarWrapper.setVisibility(View.GONE);
            }

            @Override
            public void showVolume() {
                if (verticalSeekBarWrapper.getVisibility() == View.VISIBLE) {
                    verticalSeekBarWrapper.setVisibility(View.GONE);
                } else {
                    nowVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    verticalSeekBar.setProgress(nowVolume * 100 / maxVolume);
                    verticalSeekBarWrapper.setVisibility(View.VISIBLE);
                }
            }
        });
        verticalSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //变更声音
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume * progress / 100, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //注册网络变化广播监听器
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetChangeReceiver();
        receiver.setActivity(this);
        this.registerReceiver(receiver, filter);
    }

   /* @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        gestureDetector = new GestureDetector(this, new VideoPlayGestureListener());
        canPlayNoWifi = SpUtil.getInstance().getAllowMoblie();
        verticalSeekBar.setMax(100);

        setVideoSize();

        videoView.setOnPreparedListener(this);
        videoView.setOnErrorListener(this);
        videoView.setOnInfoListener(this);
        videoView.setOnCompletionListener(this);
        videoView.setOnErrorCompletionListener(this);
        videoController.setPlayerController(this, videoView, isLive());
        videoController.setShareAndCropListener(new VideoController.ShareAndCropListener() {
            @Override
            public void openShare(View view) {
                //openSharePop(view);
            }

            @Override
            public void openCrop(View view) {
                openCropPop(view);
            }
        });
        videoController.setShowAndHideControllerListener(new VideoController.ShowAndHideControllerListener() {
            @Override
            public void hide() {
                verticalSeekBarWrapper.setVisibility(View.GONE);
            }

            @Override
            public void showVolume() {
                if (verticalSeekBarWrapper.getVisibility() == View.VISIBLE) {
                    verticalSeekBarWrapper.setVisibility(View.GONE);
                } else {
                    nowVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    verticalSeekBar.setProgress(nowVolume * 100 / maxVolume);
                    verticalSeekBarWrapper.setVisibility(View.VISIBLE);
                }
            }
        });
        verticalSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //变更声音
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume * progress / 100, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //注册网络变化广播监听器
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetChangeReceiver();
        receiver.setActivity(this);
        this.registerReceiver(receiver, filter);
    }*/

    private void setVideoSize() {
        RelativeLayout.MarginLayoutParams imgVideoParams = (RelativeLayout.MarginLayoutParams) rlImgVideo.getLayoutParams();
        imgVideoParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        imgVideoParams.height = screenWidth * 9 / 16;
        imgVideoParams.topMargin = videoMargin();
        rlImgVideo.setLayoutParams(imgVideoParams);
    }

    public boolean isFullScreen() {
        return isFullScreen;
    }

    protected void setVideoTitle(String title) {
        videoController.setTitle(title);
    }

    @Override
    public void onBackPressed() {
        //锁屏
        if (videoController.isLock()) {
            return;
        }
        if (isFullScreen()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            Debug.d("BaseVideoActivity", "heheheheh");
            super.onBackPressed();
        }
    }

    @Override
    public void onPrepared(IMediaPlayer mp) {
        Debug.d(TAG, "onPrepared");
        //若当前显示流量提示框,暂停
        if (videoController.getShowPlayPrompt()) {
            videoController.setVideoPlayOrPause(false);
        }
        //设置横竖屏可以切换
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }

    @Override
    public boolean onInfo(IMediaPlayer mp, int what, int extra) {
        Debug.d(TAG, "onInfo:" + what + "-->" + extra + "-->"
                + MediaPlayer.MEDIA_INFO_BUFFERING_START + "-->" + MediaPlayer.MEDIA_INFO_BUFFERING_END);
        switch (what) {
            case 10001:
                //TODO 针对流量切回wifi不能播放
                if (!isPlay() && NetWorkUtil.isWifi(this) && !isShareResume) {
                    videoController.setVideoPlayOrPause(true);
                }
                break;
            case 10002:
                if (isShareResume) {
                    videoController.setVideoPlayOrPause(false, currentDuration);
                    videoController.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isShareResume = false;
                        }
                    }, 200);
                }
                break;
            case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                if (rlVideoLoading.getVisibility() == View.VISIBLE) {
                    rlVideoLoading.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            rlVideoLoading.setVisibility(View.GONE);
                            viewPrepared.setVisibility(View.GONE);
                        }
                    }, 200);
                }
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                rlVideoLoading.setVisibility(View.VISIBLE);
                //TODO 针对流量切回wifi不能播放
                if (!isPlay() && NetWorkUtil.isWifi(this) && !isShareResume) {
                    videoController.setVideoPlayOrPause(true);
                }
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                rlVideoLoading.setVisibility(View.GONE);
                viewPrepared.setVisibility(View.GONE);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onError(IMediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onCompletion(IMediaPlayer mp) {
        Debug.d(TAG, "onCompletion");
        onCompletionAndError(mp);
    }

    /**
     * 无法播放导致的视频播放结束
     *
     * @param mp
     */
    @Override
    public void onErrorCompletion(IMediaPlayer mp) {
        onCompletionAndError(mp);
    }

    /**
     * 视频正常播放结束和视频无法播放导致的视频播放结束统一处理
     *
     * @param mp
     */
    private void onCompletionAndError(IMediaPlayer mp) {
        videoController.reset();
        videoUrl = null;
        currentDuration = 0;
        imgVideo.setVisibility(View.VISIBLE);
        imgLiveAction.setVisibility(View.VISIBLE);
        rlVideoLoading.setVisibility(View.GONE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * 播放视频
     *
     * @param url 视频地址
     * @param isStopVideo 播放前是否销毁当前视频
     */
    protected void startVideoPlay(String url, boolean isStopVideo) {
        Debug.d(TAG, "startVideoPlay(url)");
        if (isStopVideo) {
            stopVideoPlay();
        }
        videoUrl = url;
        if (!NetWorkUtil.isConnected()) {
            ToastUtil.Infotoast(this, getResources().getString(R.string.not_network));
        }
        //非Wi-Fi
        if (!NetWorkUtil.isWifi(this)) {
            if (!canPlayNoWifi) {
                ToastUtil.Infotoast(this, getResources().getString(R.string.video_play_not_wifi_prompt));
            } else {
                showPrompt();
            }
        } else {
            startVideoPlay();
        }
    }

    protected void startVideoPlay(String url) {
        startVideoPlay(url, true);
    }

    private void startVideoPlay() {
        Debug.d(TAG, "startVideoPlay");
        if (TextUtils.isEmpty(videoUrl)) {
            return;
        }
        stopVideoPlay();
        viewPrepared.setVisibility(View.VISIBLE);
        imgLiveAction.setVisibility(View.GONE);
        imgVideo.setVisibility(View.GONE);
        rlVideoLoading.setVisibility(View.VISIBLE);
        videoView.setVideoPath(videoUrl);
        videoView.requestFocus();
        videoController.setVideoPlayOrPause(true);
        isStartPlaying = true;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    protected void stopVideoPlay() {
        boolean b = videoView == null ? true : false;
        Debug.d(TAG, "stopVideoPlay videoView == null" + b);
        if (videoView == null) {
            return;
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        videoController.setVideoPlayOrPause(false);
        videoView.stopPlayback();
        imgVideo.setVisibility(View.VISIBLE);
        imgLiveAction.setVisibility(View.VISIBLE);
        rlVideoLoading.setVisibility(View.GONE);
        isStartPlaying = false;
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    /**
     * 截图
     */
    private void openCropPop(View view) {
        if (getCropBitmap() == null) {
            cropBitmap = videoView.getBitmap();
        } else {
            cropBitmap = getCropBitmap();
        }
        if (videoCropPop == null) {
            View rootView = getLayoutInflater().inflate(R.layout.pop_video_crop, null);

            videoCropPop = new MyPopWindow(rootView, LinearLayout.LayoutParams.MATCH_PARENT
                    , LinearLayout.LayoutParams.MATCH_PARENT, true);
            videoCropPop.setBackgroundDrawable(new BitmapDrawable());
            videoCropPop.setFocusable(true);
            videoCropPop.setTouchable(true);
            videoCropPop.setOutsideTouchable(true);

            imgCrop = (ImageView) rootView.findViewById(R.id.dialog_video_crop_img);
            rootView.findViewById(R.id.dialog_video_crop_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    videoCropPop.dismiss();
                }
            });
            rootView.findViewById(R.id.dialog_video_crop_save).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtil.Infotoast(BaseVideoActivity.this,
                            getResources().getString(R.string.video_crop_save_loading));
                    Observable.create(new Observable.OnSubscribe<Bitmap>() {
                                @Override
                                public void call(Subscriber<? super Bitmap> subscriber) {
                                    subscriber.onNext(cropBitmap);
                                    subscriber.onCompleted();
                                }
                            }).subscribeOn(AndroidSchedulers.mainThread())
                            .observeOn(Schedulers.io())
                            .map(new Func1<Bitmap, String>() {
                                @Override
                                public String call(Bitmap bitmap) {
                                    return ImageUtil.saveImageToExternal(BaseVideoActivity.this, bitmap);
                                }
                            })
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<String>() {
                                @Override
                                public void call(String path) {
                                    if (TextUtils.isEmpty(path)) {
                                        ToastUtil.Infotoast(BaseVideoActivity.this,
                                                getResources().getString(R.string.video_crop_save_failure));
                                    } else {
                                        ToastUtil.Infotoast(BaseVideoActivity.this,
                                                getResources().getString(R.string.video_crop_save_success)
                                                        .concat(path));
                                    }
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    ToastUtil.Infotoast(BaseVideoActivity.this,
                                            getResources().getString(R.string.video_crop_save_failure));
                                }
                            });
                }
            });
        }
        imgCrop.setImageBitmap(cropBitmap);
        if (videoView.isPlaying()) {
            videoController.setVideoPlayOrPause();
        }
        videoCropPop.showAtLocation(view, Gravity.BOTTOM, 0, 0);
    }

    @Override
    public void netChanged() {
        super.netChanged();
        if (videoView != null) {
            if (!NetWorkUtil.isConnected()) {
                ToastUtil.Infotoast(this, getResources().getString(R.string.not_network));
                videoController.setVideoPlayOrPause(false);
            } else if (!NetWorkUtil.isWifi(this)) {
                videoController.setVideoPlayOrPause(false);
                videoController.setShowPlayPrompt(true);
            }
        }
    }

    /**
     * 横竖屏切换时隐藏显示view
     *
     * @param isFullScreen
     */
    protected abstract void changeView(boolean isFullScreen);

    /**
     * 是否是直播
     *
     * @return
     */
    protected abstract boolean isLive();

    /**
     * 视频距顶部距离
     *
     * @return
     */
    protected abstract int videoMargin();

    /**
     * 视频是否正在播放
     *
     * @return
     */
    protected boolean isPlay() {
        if (videoView != null && videoView.isPlaying()) {
            return true;
        }
        return false;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public Bitmap getCropBitmap() {
        return null;
    }

    public boolean getIsStartPlaying() {
        return isStartPlaying;
    }

    private void showPrompt() {
        if (promptDialog == null) {
            promptDialog = PromptDialog.show(this, getResources().getString(R.string.video_play_not_wifi_title)
                    , getResources().getString(R.string.video_play_not_wifi_content)
                    , getResources().getString(R.string.video_play_not_wifi_continue));
            promptDialog.setCancelListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hidePrompt();
                }
            });
            promptDialog.setOkListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    videoController.setShowPlayPrompt(false);
                    if (isLive()) {
                        startVideoPlay();
                    } else {
                        if (currentDuration == 0) {
                            startVideoPlay();
                        } else {
                            Debug.d(TAG, "点播播放视频" + currentDuration);
                            rlVideoLoading.setVisibility(View.VISIBLE);
                            videoController.setVideoPlayOrPause(true, currentDuration);
                        }
                    }
                    hidePrompt();
                }
            });
        } else {
            promptDialog.show();
        }
    }

    private void hidePrompt() {
        if (promptDialog != null && promptDialog.isShowing()) {
            promptDialog.dismiss();
        }
    }

    /**
     * 直播、点播横屏播放的时候监听手势变化 控制音量、亮度和（点播）播放的快进、快退
     */
    class VideoPlayGestureListener extends GestureDetector.SimpleOnGestureListener {

        private float fx;
        private float fy;
        private float halfScreenWidth;

        @Override
        public boolean onDown(MotionEvent e) {
            fx = e.getX();
            fy = e.getY();
            halfScreenWidth = screenWidth / 2;
            return super.onDown(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float mOldX = 0, mOldY = 0;
            if (e1 != null) {
                mOldX = e1.getX();
                mOldY = e1.getY();
            }
            int y = (int) e2.getRawY();
            int x = (int) e2.getRawX();

            Display disp = getWindowManager().getDefaultDisplay();
            int windowWidth = disp.getWidth();
            int windowHeight = disp.getHeight();

            if (Math.abs((y - fy)) > Math.abs((x - fx)) + 100 && fx > halfScreenWidth + 40) {
                onVolumeSlide((mOldY - y) / windowHeight);
            } else if (Math.abs((y - fy)) > Math.abs((x - fx)) + 100 && fx < halfScreenWidth - 40) {
                //调节亮度
                int myBright = (int) mOldY - y;
                if (myBright >= 255) {
                    myBright = 255;
                }
                if (myBright <= -255) {
                    myBright = -255;
                }
                //onBrightSlide(myBright);
            }

            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            float mOldX = e1.getX();
            float mOldY = e1.getY();
            int y = (int) e2.getRawY();
            int x = (int) e2.getRawX();

            Display disp = getWindowManager().getDefaultDisplay();
            int windowWidth = disp.getWidth();
            int windowHeight = disp.getHeight();

            if (Math.abs((y - fy)) < Math.abs((x - fx)) + 60) {
                //onPlayerSeek(x - mOldX);
            }

            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    /**
     * 滑动改变声音大小
     *
     * @param percent
     */
    protected void onVolumeSlide(float percent) {
        if (nowVolume == -1) {
            nowVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (nowVolume < 0) {
                nowVolume = 0;
            }

            handler.removeMessages(0);
            rlVolumeController.setVisibility(View.VISIBLE);
        }

        int index = (int) (percent * maxVolume) + nowVolume;
        if (index > maxVolume) {
            index = maxVolume;
        } else if (index < 0) {
            index = 0;
        }

        // 变更声音
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

        verticalSeekBar.setProgress(index * 100 / maxVolume);
        horizontalProgressView.setProgress(index * 100 / maxVolume);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isFullScreen) {
            gestureDetector.onTouchEvent(ev);
            //处理手势结束
            switch (ev.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                    endGesture();
                    break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 隐藏音量控制
     * 0 代表控制音量的显示和隐藏 1代表亮度的显示和隐藏
     */
    protected void endGesture() {
        nowVolume = -1;
        handler.removeMessages(0);
        handler.removeMessages(1);
        handler.sendEmptyMessageDelayed(0, 1000);
        handler.sendEmptyMessageDelayed(1, 1000);
    }

    @Override
    public void finish() {
        if (videoView != null) {
            stopVideoPlay();
            videoView.stopPlayback();
            videoView = null;
        }
        System.gc();
        super.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView != null) {
            if (videoView.getCurrentPosition() > 0) {
                currentDuration = videoView.getCurrentPosition();
            }
            videoController.setVideoPlayOrPause(false);
            Debug.d(TAG, "播放进度:" + currentDuration);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoView != null) {
            if (isLive() && !TextUtils.isEmpty(videoUrl)) {
                if (videoController.getShowPlayPrompt()) {
                    showPrompt();
                } else {
                    rlVideoLoading.setVisibility(View.VISIBLE);
                    startVideoPlay(videoUrl);
                }
            } else if (currentDuration > 0) {
                if (videoController.getShowPlayPrompt()) {
                    Debug.d(TAG, "点播1111");
                    showPrompt();
                } else {
                    Debug.d(TAG, "点播2222");
                    rlVideoLoading.setVisibility(View.VISIBLE);
                    videoController.setVideoPlayOrPause(true, currentDuration);
                }
            }
        }
        if (videoController != null && videoController.isLock()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            this.unregisterReceiver(receiver);
        }
        if (videoController != null) {
            videoController.destoryTimer();
        }
    }
}
