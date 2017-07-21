package com.humanheima.videoplayerdemo.ui.widget.video;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.humanheima.videoplayerdemo.R;
import com.humanheima.videoplayerdemo.ui.base.BaseVideoActivity;
import com.humanheima.videoplayerdemo.ui.widget.dialog.PromptDialog;
import com.humanheima.videoplayerdemo.util.Debug;
import com.humanheima.videoplayerdemo.util.NetWorkUtil;
import com.humanheima.videoplayerdemo.util.SpUtil;
import com.humanheima.videoplayerdemo.util.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by chenchao on 16/10/18.
 * cc@cchao.org
 */
public class VideoController extends RelativeLayout implements Handler.Callback {

    private final String TAG = getClass().getName();

    //seekBar最大进度
    private final int SEEK_MAX = 1000;
    //控制条自动隐藏时间
    private final int DEFAULT_TIME = 5 * 1000;
    //隐藏控制条
    private final int FADE_OUT = 1;

    @BindView(R.id.rl_parent)
    RelativeLayout rlParent;
    @BindView(R.id.text_video_title)
    TextView textTitle;
    @BindView(R.id.ll_top_bar)
    LinearLayout llTopBar;
    @BindView(R.id.img_play_pause)
    ImageView imgPlayPause;
    @BindView(R.id.img_full_small)
    ImageView imgFullSmall;
    @BindView(R.id.img_volume)
    ImageView imgVolume;
    @BindView(R.id.text_totalTime)
    TextView textTotalTime;
    @BindView(R.id.text_currentTime)
    TextView textCurrentTime;
    @BindView(R.id.seekbar)
    SeekBar seekbar;
    @BindView(R.id.rl_bottom_bar)
    RelativeLayout rlBottomBar;
    @BindView(R.id.img_lock)
    ImageView imgLock;
    @BindView(R.id.img_crop)
    ImageView imgCrop;
    @BindView(R.id.img_video_share)
    ImageView imgShare;
    @BindView(R.id.ll_share_crop)
    LinearLayout llShareCrop;
    @BindView(R.id.video_controller_progress)
    ProgressBar progressBar;

    private View rootView;

    private VideoView videoView;

    //时分秒
    private SimpleDateFormat hmsDateFormat;
    //分秒
    private SimpleDateFormat msDateFormat;

    private BaseVideoActivity baseVideoActivity;

    //是否是直播
    private boolean isLive = false;

    //是否锁屏
    private boolean isLock = false;

    private Handler handler;

    //定时器修改播放进度
    private Timer timer;
    private MyTimerTask timerTask;
    //缓存进度0-100
    private int buffer = 0;

    //重要:是否显示流量提示,用于wifi切流量时的特殊处理,wifi切流量点确定只提示一次
    private boolean showPlayPrompt = false;
    //流量提示框
    private PromptDialog promptDialog;

    private ShowAndHideControllerListener showAndHideControllerListener;
    private ShareAndCropListener shareAndCropListener;

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case FADE_OUT:
                showHideController(false);
                showHideProgressBar(true);
                break;
            default:
                break;
        }
        return false;
    }

    public VideoController(Context context) {
        super(context);
        initView();
    }

    public VideoController(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public VideoController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void setShowAndHideControllerListener(ShowAndHideControllerListener listener) {
        this.showAndHideControllerListener = listener;
        imgVolume.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showAndHideControllerListener.showVolume();
            }
        });
    }

    private void initView() {
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.video_controller, null);
        ButterKnife.bind(this, rootView);
        addView(rootView);

        handler = new Handler(this);
        hmsDateFormat = new SimpleDateFormat("HH:mm:ss");
        msDateFormat = new SimpleDateFormat("mm:ss");

        progressBar.setMax(SEEK_MAX);
        seekbar.setMax(SEEK_MAX);
    }

    public void setPlayerController(BaseVideoActivity baseVideoActivity, VideoView videoView) {
        setPlayerController(baseVideoActivity, videoView, false);
    }

    public void setPlayerController(BaseVideoActivity baseVideoActivity, VideoView videoView, boolean isLive) {
        this.baseVideoActivity = baseVideoActivity;
        this.videoView = videoView;
        setLive(isLive);
    }

    public void setTitle(String title) {
        textTitle.setText(title);
    }

    public void setShowPlayPrompt(boolean showPlayPrompt) {
        this.showPlayPrompt = showPlayPrompt;
    }

    public boolean getShowPlayPrompt() {
        return showPlayPrompt;
    }

    /**
     * 进度重置
     */
    public void reset() {
        buffer = 0;
        textCurrentTime.setText("00:00");
        textTotalTime.setText("/00:00");
        progressBar.setProgress(0);
        progressBar.setSecondaryProgress(0);
        seekbar.setProgress(0);
        seekbar.setSecondaryProgress(0);
    }

    /**
     * 设置是否直播
     *
     * @param live
     */
    public void setLive(boolean live) {
        isLive = live;
        if (isLive) {
            //隐藏进度条等
            seekbar.setVisibility(INVISIBLE);
            textTotalTime.setVisibility(INVISIBLE);
            textCurrentTime.setVisibility(INVISIBLE);
            progressBar.setVisibility(GONE);
        } else {
            seekbar.setVisibility(VISIBLE);
            textCurrentTime.setVisibility(VISIBLE);
            textTotalTime.setVisibility(VISIBLE);
            seekbar.setOnSeekBarChangeListener(new MySeekBarChangeListener());
            //监测视频播放进度
            timer = new Timer();
            timerTask = new MyTimerTask();
            timer.schedule(timerTask, 0, 1000);
            progressBar.setVisibility(VISIBLE);
        }
    }

    @OnClick(R.id.img_play_pause)
    public void setVideoPlayOrPause() {
        if (videoView == null) {
            return;
        }
        setVideoPlayOrPause(!videoView.isPlaying());
    }

    /**
     * 设置视频暂停or播放
     *
     * @param isSetPlay
     */
    public void setVideoPlayOrPause(boolean isSetPlay) {
        setVideoPlayOrPause(isSetPlay, -1);
    }

    public void setVideoPlayOrPause(boolean isSetPlay, int seekTo) {
        if (isSetPlay) {
            if (!NetWorkUtil.isConnected()) {
                ToastUtil.Infotoast(baseVideoActivity, getResources().getString(R.string.not_network));
                return;
            } else if (!NetWorkUtil.isWifi(baseVideoActivity)) {
                //流量不能播放
                if (!SpUtil.getInstance().getAllowMoblie()) {
                    ToastUtil.Infotoast(baseVideoActivity, getResources().getString(R.string.video_play_not_wifi_prompt));
                    return;
                } else if (showPlayPrompt) {
                    //流量播放提示
                    showPrompt(seekTo);
                    return;
                } else {
                    Debug.d(TAG, "点播流量播放");
                    if (seekTo != -1) {
                        videoView.seekTo(seekTo);
                    } else {
                        videoView.start();
                    }
                    imgPlayPause.setImageResource(R.drawable.ic_video_pause);
                }
            } else {
                Debug.d(TAG, "点播wifi播放");
                if (seekTo != -1) {
                    videoView.seekTo(seekTo);
                } else {
                    videoView.start();
                }
                imgPlayPause.setImageResource(R.drawable.ic_video_pause);
            }
        } else {
            if (seekTo != -1) {
                videoView.seekTo(seekTo);
            }
            videoView.pause();
            imgPlayPause.setImageResource(R.drawable.ic_video_play);
        }
    }

    private void showPrompt() {
        showPrompt(-1);
    }

    private void showPrompt(final int seekTo) {
        if (promptDialog == null) {
            promptDialog = PromptDialog.show(baseVideoActivity, getResources().getString(R.string.video_play_not_wifi_title)
                    , getResources().getString(R.string.video_play_not_wifi_content)
                    , getResources().getString(R.string.video_play_not_wifi_continue));
            promptDialog.setCancelListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    hidePrompt();
                }
            });
            promptDialog.setOkListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (seekTo != -1) {
                        videoView.seekTo(seekTo);
                    } else {
                        videoView.start();
                    }
                    imgPlayPause.setImageResource(R.drawable.ic_video_pause);
                    showPlayPrompt = false;
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
     * 显示隐藏控制条
     */
    @OnClick(R.id.rl_parent)
    public void showOrHideController() {
        handler.removeMessages(FADE_OUT);
        if (isLock) {
            if (imgLock.getVisibility() == VISIBLE) {
                imgLock.setVisibility(INVISIBLE);
                showHideProgressBar(true);
            } else {
                imgLock.setVisibility(VISIBLE);
                showHideProgressBar(true);
                handler.sendEmptyMessageDelayed(FADE_OUT, DEFAULT_TIME);
            }
        } else {
            if (rlBottomBar.getVisibility() == VISIBLE) {
                showHideController(false);
                showHideProgressBar(true);
            } else {
                showHideController(true);
                showHideProgressBar(false);
                handler.sendEmptyMessageDelayed(FADE_OUT, DEFAULT_TIME);
            }
        }
    }

    /**
     * 顶部返回按钮
     */
    @OnClick(R.id.img_video_back)
    public void onBack() {
        baseVideoActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * 全屏和取消全屏
     */
    @OnClick(R.id.img_full_small)
    public void setFullOrSmall() {
        Debug.d(TAG, "放大缩小:" + baseVideoActivity.isFullScreen());
        if (baseVideoActivity.isFullScreen()) {
            baseVideoActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            baseVideoActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    /**
     * 设置锁屏与取消锁屏
     */
    @OnClick(R.id.img_lock)
    public void setLock() {
        if (isLock) {
            imgLock.setImageResource(R.drawable.ic_video_lock_no);
            llTopBar.setVisibility(VISIBLE);
            rlBottomBar.setVisibility(VISIBLE);
            llShareCrop.setVisibility(VISIBLE);
            showHideProgressBar(false);
        } else {
            imgLock.setImageResource(R.drawable.ic_video_lock_yes);
            llTopBar.setVisibility(INVISIBLE);
            rlBottomBar.setVisibility(INVISIBLE);
            llShareCrop.setVisibility(INVISIBLE);
            showHideProgressBar(true);
        }
        isLock = !isLock;
        baseVideoActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

    /**
     * 显示隐藏控制条
     *
     * @param isShow
     */
    private void showHideController(boolean isShow) {
        int show = isShow ? VISIBLE : INVISIBLE;
        rlBottomBar.setVisibility(show);
        if (!baseVideoActivity.isFullScreen()) {
            return;
        }
        llTopBar.setVisibility(show);
        imgLock.setVisibility(show);
        llShareCrop.setVisibility(show);
        if (showAndHideControllerListener != null && !isShow) {
            showAndHideControllerListener.hide();
        }
    }

    /**
     * 显示隐藏底部进度条
     *
     * @param isShow
     */
    private void showHideProgressBar(boolean isShow) {
        if (!isShow) {
            progressBar.setVisibility(GONE);
        } else if (!isLive) {
            progressBar.setVisibility(VISIBLE);
        }
    }

    /**
     * 全屏与小屏view显示设置
     */
    public void setFullOrSmallScreen() {
        if (baseVideoActivity.isFullScreen()) {
            llTopBar.setVisibility(VISIBLE);
            imgLock.setVisibility(VISIBLE);
            llShareCrop.setVisibility(VISIBLE);
            rlBottomBar.setVisibility(VISIBLE);
            imgVolume.setVisibility(VISIBLE);
            imgFullSmall.setImageResource(R.drawable.ic_video_full_cancel);
            handler.sendEmptyMessageDelayed(FADE_OUT, DEFAULT_TIME);
            showHideProgressBar(false);
        } else {
            imgLock.setVisibility(INVISIBLE);
            isLock = false;
            llTopBar.setVisibility(INVISIBLE);
            imgLock.setVisibility(INVISIBLE);
            llShareCrop.setVisibility(INVISIBLE);
            rlBottomBar.setVisibility(VISIBLE);
            imgVolume.setVisibility(GONE);
            imgFullSmall.setImageResource(R.drawable.ic_video_full);
            handler.sendEmptyMessageDelayed(FADE_OUT, DEFAULT_TIME);
            showHideProgressBar(false);
        }
    }

    public void setShareAndCropListener(ShareAndCropListener listener) {
        this.shareAndCropListener = listener;
        imgShare.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                shareAndCropListener.openShare(v);
            }
        });
        imgCrop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                shareAndCropListener.openCrop(v);
            }
        });
    }

    public boolean isLock() {
        return isLock;
    }


    /**
     * 关闭定时器线程
     */
    public void destoryTimer() {
        if (timerTask != null) {
            timerTask.cancel();
        }
        timer = null;
        timerTask = null;
    }

    /**
     * 定时器任务用来更新播放进度条的进度
     */
    private class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (videoView != null) {
                        int current = videoView.getCurrentPosition();
                        int duration = videoView.getDuration();
                        if (duration >= 3600 * 1000) {
                            textCurrentTime.setText(hmsDateFormat.format(current - TimeZone.getDefault().getRawOffset()));
                            textTotalTime.setText("/".concat(hmsDateFormat.format(duration - TimeZone.getDefault().getRawOffset())));
                        } else {
                            textCurrentTime.setText(msDateFormat.format(current - TimeZone.getDefault().getRawOffset()));
                            textTotalTime.setText("/".concat(msDateFormat.format(duration - TimeZone.getDefault().getRawOffset())));
                        }
                        if (current <= duration && duration > 0) {
                            seekbar.setProgress(current * SEEK_MAX / duration);
                            progressBar.setProgress(current * SEEK_MAX / duration);
                        } else if (current > duration && current > 0) {
                            seekbar.setProgress(SEEK_MAX);
                            progressBar.setProgress(SEEK_MAX);
                        }
                        int progressInt = seekbar.getProgress() / 10;
                        //设置缓存进度
                        if (buffer <= videoView.getBufferPercentage() && buffer < 100 && videoView.getBufferPercentage() > progressInt) {
                            buffer = videoView.getBufferPercentage();
                        } else if (buffer < 100) {
                            buffer = seekbar.getProgress() / 10 + 1;
                            if (buffer > 100) {
                                buffer = 100;
                            }
                        }
                        seekbar.setSecondaryProgress(buffer * 10);
                        progressBar.setSecondaryProgress(buffer * 10);
                    }
                }
            });
        }
    }

    /**
     * 拖动进度条事件
     */
    private class MySeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress = seekBar.getProgress();
            int currentTime = (progress * videoView.getDuration()) / SEEK_MAX;
            videoView.seekTo(currentTime);
            if (!videoView.isPlaying()) {
                videoView.start();
            }
            imgPlayPause.setImageResource(R.drawable.ic_video_pause);
        }
    }

    public interface ShareAndCropListener {

        void openShare(View view);

        void openCrop(View view);
    }


    public interface ShowAndHideControllerListener {
        void hide();

        void showVolume();
    }
}
