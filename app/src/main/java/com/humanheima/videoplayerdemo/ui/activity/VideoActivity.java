package com.humanheima.videoplayerdemo.ui.activity;

import com.humanheima.videoplayerdemo.R;
import com.humanheima.videoplayerdemo.ui.base.BaseVideoActivity;
import com.humanheima.videoplayerdemo.util.MyUrl;

/**
 * 视频
 */
public class VideoActivity extends BaseVideoActivity {

    @Override
    protected int bindLayout() {
        return R.layout.activity_video;
    }

    @Override
    protected void initData() {
        super.initData();
        startVideoPlay(MyUrl.urlList.get(0));
        /*Observable.timer(2, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<Long, Observable<?>>() {
                    @Override
                    public Observable<?> call(Long aLong) {
                        startVideoPlay(MyUrl.urlList.get(0));
                        return null;
                    }
                }).subscribe();*/
    }

    @Override
    protected void bindEvent() {

    }

    @Override
    protected void changeView(boolean isFullScreen) {

    }

    @Override
    protected boolean isLive() {
        return false;
    }

    @Override
    protected int videoMargin() {
        return 0;
    }
}
