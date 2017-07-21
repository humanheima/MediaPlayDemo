package com.humanheima.videoplayerdemo.ui.widget.video;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by chenchao on 16/10/31.
 * cc@cchao.org
 */
public class MediaController extends FrameLayout {

    public MediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MediaController(Context context) {
        super(context);
    }

    public interface MediaPlayerControl {
        void start();

        void pause();

        int getDuration();

        int getCurrentPosition();

        void seekTo(long pos);

        boolean isPlaying();

        int getBufferPercentage();

        boolean canPause();

        boolean canSeekBackward();

        boolean canSeekForward();
    }
}
