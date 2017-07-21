package com.humanheima.videoplayerdemo.model;

/**
 * Created by roly on 2016/10/13.
 * 直播间详情
 */

public class LiveDetailsBean {

    /**
     * liveTitle : 直播间地址测试1
     * startTime : 1478754105
     * endTime : 1478790105
     * time : 1476333152
     * introduction :
     * liveURL : http://hls.live.kksmg.com/live/live24h3/playlist.m3u8?se=kankan&ct=2&_cp=1&_fk=E44F46F2D4AD6C96DC3705D345D973A8551AEF1651ED4275F87E11D1CC37FB31
     * livePoster : http://imgnews.gmw.cn/attachement/jpg/site2/20160922/6273045881886259231.jpg
     * shareInfo : {"shareImage":"http://imgnews.gmw.cn/attachement/jpg/site2/20160922/6273045881886259231.jpg","shareTitle":"直播间地址测试1","shareText":"直播间地址测试1","shareURL":""}
     */

    private String liveTitle;
    private long startTime;
    private long endTime;
    private long time;
    private String introduction;
    private String liveURL;
    private String livePoster;
    /**
     * shareImage : http://imgnews.gmw.cn/attachement/jpg/site2/20160922/6273045881886259231.jpg
     * shareTitle : 直播间地址测试1
     * shareText : 直播间地址测试1
     * shareURL :
     */

    private ShareInfoBean shareInfo;

    public String getLiveTitle() {
        return liveTitle;
    }

    public void setLiveTitle(String liveTitle) {
        this.liveTitle = liveTitle;
    }

    public long getStartTime() {
        return startTime * 1000;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime * 1000;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getTime() {
        return time * 1000;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getLiveURL() {
        return liveURL;
    }

    public void setLiveURL(String liveURL) {
        this.liveURL = liveURL;
    }

    public String getLivePoster() {
        return livePoster;
    }

    public void setLivePoster(String livePoster) {
        this.livePoster = livePoster;
    }

    public ShareInfoBean getShareInfo() {
        return shareInfo;
    }

    public void setShareInfo(ShareInfoBean shareInfo) {
        this.shareInfo = shareInfo;
    }

    public static class ShareInfoBean {
        private String shareImage;
        private String shareTitle;
        private String shareText;
        private String shareURL;

        public String getShareImage() {
            return shareImage;
        }

        public void setShareImage(String shareImage) {
            this.shareImage = shareImage;
        }

        public String getShareTitle() {
            return shareTitle;
        }

        public void setShareTitle(String shareTitle) {
            this.shareTitle = shareTitle;
        }

        public String getShareText() {
            return shareText;
        }

        public void setShareText(String shareText) {
            this.shareText = shareText;
        }

        public String getShareURL() {
            return shareURL;
        }

        public void setShareURL(String shareURL) {
            this.shareURL = shareURL;
        }
    }
}
