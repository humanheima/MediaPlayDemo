package com.humanheima.videoplayerdemo.ui.activity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.humanheima.videoplayerdemo.R;
import com.humanheima.videoplayerdemo.model.LiveAll;
import com.humanheima.videoplayerdemo.model.LiveDetail;
import com.humanheima.videoplayerdemo.network.ApiEntity.LiveAllEntity;
import com.humanheima.videoplayerdemo.network.ApiEntity.LiveDetailEntity;
import com.humanheima.videoplayerdemo.network.HttpResult;
import com.humanheima.videoplayerdemo.network.NetWork;
import com.humanheima.videoplayerdemo.ui.base.BaseVideoActivity;
import com.humanheima.videoplayerdemo.util.Debug;
import com.humanheima.videoplayerdemo.util.ListUtil;
import com.humanheima.videoplayerdemo.util.NetWorkUtil;
import com.humanheima.videoplayerdemo.util.ScreenUtil;
import com.humanheima.videoplayerdemo.util.ToastUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.Danmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.android.BaseCacheStuffer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.model.android.SpannedCacheStuffer;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class LiveActivity extends BaseVideoActivity {

    private final String TAG = "LiveActivity";
    ImageView imgAudio;
    ProgressBar progressbar;
    ImageView imgVolumeBg;
    Button send;
    Button sendImgText;
    private List<LiveAll.LiveBean> liveBeans;
    private List<LiveDetail> liveDetails;
    private List<LiveDetail> liveDetailsTemp;
    private Subscriber subscriber;
    //关于弹幕的
    DanmakuView danmakuView;
    private boolean showDanmaku;
    private DanmakuContext danmakuContext;
    //弹幕解析器
    private BaseDanmakuParser parser = new BaseDanmakuParser() {
        @Override
        protected IDanmakus parse() {
            return new Danmakus();
        }
    };

    //设置弹幕能够使图文混编的效果
    private BaseCacheStuffer.Proxy mCacheStufferAdapter = new BaseCacheStuffer.Proxy() {
        @Override
        public void prepareDrawing(BaseDanmaku danmaku, boolean fromWorkerThread) {

        }

        @Override
        public void releaseResource(BaseDanmaku danmaku) {

        }
    };

    @Override
    protected int bindLayout() {
        return R.layout.activity_live;
    }

    @Override
    protected void initData() {
        super.initData();
        liveBeans = new ArrayList<>();
        liveDetails = new ArrayList<>();
        liveDetailsTemp = new ArrayList<>();
        danmakuView.enableDanmakuDrawingCache(true);

        danmakuView.setCallback(new DrawHandler.Callback() {
            @Override
            public void prepared() {
                showDanmaku = true;
                danmakuView.start();
                generateSomeDanmaku();
            }

            @Override
            public void updateTimer(DanmakuTimer timer) {

            }

            @Override
            public void danmakuShown(BaseDanmaku danmaku) {

            }

            @Override
            public void drawingFinished() {

            }
        });

        //DanmakuContext可以用于对弹幕的各种全局配置进行设定，如设置字体、设置最大显示行数等
        danmakuContext = DanmakuContext.create();
        //准备
        Map<Integer, Integer> pairs = new HashMap<>();
        pairs.put(Danmaku.TYPE_SCROLL_RL, 5);
        // 设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_LR, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_BOTTOM, true);
        danmakuContext.setMaximumLines(pairs).preventOverlapping(overlappingEnablePair)
                .setCacheStuffer(new SpannedCacheStuffer(), mCacheStufferAdapter);//设置能够显示图文混编的texto
        danmakuView.prepare(parser, danmakuContext);
        subscriber = new Subscriber<Long>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e("generateSomeDanmaku", e.getMessage());
            }

            @Override
            public void onNext(Long aLong) {
                String content = "第" + aLong + "条弹幕";
                addDanmaku(content, false);
            }
        };
        getData();
    }

    private void generateSomeDanmaku() {
        /*interval操作符是每隔一段时间就产生一个数字，这些数字从0开始，一次递增1直至无穷大*/
        Observable.interval(2, 300, TimeUnit.MILLISECONDS, Schedulers.io())
                .take(100)//最多输出100个
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    @Override
    protected void bindEvent() {

    }

    @Override
    protected void changeView(boolean isFullScreen) {

    }

    @Override
    protected boolean isLive() {
        return true;
    }

    @Override
    protected int videoMargin() {
        return 0;
    }

    private void getData() {
        if (!NetWorkUtil.isConnected()) {
            ToastUtil.Infotoast(this, getResources().getString(R.string.not_network));
            return;
        }
        liveDetailsTemp.clear();
        NetWork.getApi().getLiveAll(LiveAllEntity.getParam())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<HttpResult<LiveAll>, Observable<LiveAll>>() {
                    @Override
                    public Observable<LiveAll> call(HttpResult<LiveAll> objectHttpResult) {
                        return NetWork.flatResponse(objectHttpResult);
                    }
                })
                .flatMap(new Func1<LiveAll, Observable<LiveAll.LiveBean>>() {
                    @Override
                    public Observable<LiveAll.LiveBean> call(LiveAll liveAll) {
                        if (!ListUtil.isEmpty(liveAll.getLiveBeans())) {
                            liveBeans.clear();
                            liveBeans.addAll(liveAll.getLiveBeans());
                        }
                        return Observable.from(liveBeans);
                    }
                })
                .observeOn(Schedulers.io())
                .flatMap(new Func1<LiveAll.LiveBean, Observable<HttpResult<LiveDetail>>>() {
                    @Override
                    public Observable<HttpResult<LiveDetail>> call(LiveAll.LiveBean liveBean) {
                        return NetWork.getApi().getLiveDetail(LiveDetailEntity.getParams(liveBean.getLiveID()));
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<HttpResult<LiveDetail>, Observable<LiveDetail>>() {
                    @Override
                    public Observable<LiveDetail> call(HttpResult<LiveDetail> objectHttpResult) {
                        return NetWork.flatResponse(objectHttpResult);
                    }
                })
                .subscribe(new Subscriber<LiveDetail>() {
                    @Override
                    public void onCompleted() {
                        Debug.d(TAG, "onCompleted");
                        liveDetails.clear();
                        liveDetails.addAll(liveDetailsTemp);
                        showData();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Debug.d(TAG, e.getMessage());
                    }

                    @Override
                    public void onNext(LiveDetail liveDetail) {
                        Debug.d(TAG, "onNext");
                        liveDetailsTemp.add(liveDetail);
                    }
                });
    }

    private void showData() {
        LiveDetail liveDetail = liveDetails.get(0);
        startVideoPlay(liveDetail.getLiveURL());
    }

    /**
     * 添加一条弹幕
     *
     * @param content
     * @param withBorder 弹幕是否有边框
     */
    private void addDanmaku(String content, boolean withBorder) {
        BaseDanmaku danmaku = danmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        danmaku.text = content;
        danmaku.padding = 8;
        danmaku.textSize = ScreenUtil.spToPx(this, 20);
        danmaku.textColor = Color.WHITE;
        danmaku.setTime(danmakuView.getCurrentTime());
        if (withBorder) {
            danmaku.borderColor = Color.GREEN;
        }
        danmakuView.addDanmaku(danmaku);
    }

    /**
     * 发送一条带图片的弹幕
     *
     * @param islive
     */
    private void addDanmaKuShowTextAndImage(boolean islive) {
        BaseDanmaku danmaku = danmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
        Drawable drawable = getResources().getDrawable(R.mipmap.ic_launcher);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        SpannableStringBuilder spannable = createSpannable(drawable);
        danmaku.text = spannable;
        danmaku.padding = 5;
        danmaku.priority = 1; // 一定会显示, 一般用于本机发送的弹幕
        danmaku.isLive = islive;
        danmaku.setTime(danmakuView.getCurrentTime() + 1200);
        danmaku.textSize = ScreenUtil.spToPx(this, 20);
        danmaku.textColor = Color.RED;
        danmaku.textShadowColor = 0; // 重要：如果有图文混排，最好不要设置描边(设textShadowColor=0)，否则会进行两次复杂的绘制导致运行效率降低
        danmaku.underlineColor = Color.GREEN;
        danmakuView.addDanmaku(danmaku);

    }

    private SpannableStringBuilder createSpannable(Drawable drawable) {
        String text = "bitmap";
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
        ImageSpan span = new ImageSpan(drawable);
        spannableStringBuilder.setSpan(span, 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.append("图文混编");
        spannableStringBuilder.setSpan(new BackgroundColorSpan(Color.parseColor("#ff3344")), text.length(),
                spannableStringBuilder.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (danmakuView != null && danmakuView.isPrepared() && danmakuView.isPaused()) {
            danmakuView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (danmakuView != null && danmakuView.isPrepared()) {
            danmakuView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!subscriber.isUnsubscribed()) {
            subscriber.unsubscribe();
        }
        if (danmakuView != null) {
            danmakuView.release();
            danmakuView = null;
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send:
                String content = "我发送的弹幕";
                addDanmaku(content, true);
                break;
            case R.id.send_img_text:
                addDanmaKuShowTextAndImage(true);
                break;
        }
    }

}
