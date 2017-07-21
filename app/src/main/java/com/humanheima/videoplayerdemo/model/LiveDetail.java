package com.humanheima.videoplayerdemo.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by chenchao on 16/10/19.
 * cc@cchao.org
 * 直播节目
 */
public class LiveDetail implements Serializable {

    /**
     * time : 1476868126
     * liveName : FM100.1
     * livePoster : http://imgnews.gmw.cn/attachement/jpg/site2/20160922/6273045881886259231.jpg
     * liveURL : http://hls.live.kksmg.com/live/live24h3/playlist.m3u8?se=kankan&ct=2&_cp=1&_fk=E44F46F2D4AD6C96DC3705D345D973A8551AEF1651ED4275F87E11D1CC37FB31
     * shareInfo : {"shareImage":"http://imgnews.gmw.cn/attachement/jpg/site2/20160922/6273045881886259231.jpg","shareTitle":"FM100.1分享","shareText":"欢迎收听FM100.1广播电台","shareURL":"http://pudong.xun-ao.com/api/liveSourceShare.php?id=2"}
     * list : []
     */

    private int time;
    private String liveName;
    private String livePoster;
    private String liveURL;
    /**
     * shareImage : http://imgnews.gmw.cn/attachement/jpg/site2/20160922/6273045881886259231.jpg
     * shareTitle : FM100.1分享
     * shareText : 欢迎收听FM100.1广播电台
     * shareURL : http://pudong.xun-ao.com/api/liveSourceShare.php?id=2
     */

    private ShareInfoBean shareInfo;
    @SerializedName("list")
    private List<LiveProgram> livePrograms;

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getLiveName() {
        return liveName;
    }

    public void setLiveName(String liveName) {
        this.liveName = liveName;
    }

    public String getLivePoster() {
        return livePoster;
    }

    public void setLivePoster(String livePoster) {
        this.livePoster = livePoster;
    }

    public String getLiveURL() {
        return liveURL;
    }

    public void setLiveURL(String liveURL) {
        this.liveURL = liveURL;
    }

    public ShareInfoBean getShareInfo() {
        return shareInfo;
    }

    public void setShareInfo(ShareInfoBean shareInfo) {
        this.shareInfo = shareInfo;
    }

    public List<LiveProgram> getLivePrograms() {
        return livePrograms;
    }

    public void setLivePrograms(List<LiveProgram> livePrograms) {
        this.livePrograms = livePrograms;
    }

    public static class ShareInfoBean implements Serializable {
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

    public static class LiveProgram implements Serializable {

        private int playID;

        private String playName;

        private long startTime;

        private String isPlaying;

        private long time;

        public int getPlayID() {
            return playID;
        }

        public void setPlayID(int playID) {
            this.playID = playID;
        }

        public String getPlayName() {
            return playName;
        }

        public void setPlayName(String playName) {
            this.playName = playName;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public String getIsPlaying() {
            return isPlaying;
        }

        public void setIsPlaying(String isPlaying) {
            this.isPlaying = isPlaying;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }
    }
}
