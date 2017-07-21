package com.humanheima.videoplayerdemo.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by chenchao on 16/10/19.
 * cc@cchao.org
 */
public class LiveAll {

    /**
     * liveID : 3
     * liveName : 浦东电视台
     * isDefault : 1
     */

    @SerializedName("list")
    private List<LiveBean> liveBeans;

    public List<LiveBean> getLiveBeans() {
        return liveBeans;
    }

    public void setLiveBeans(List<LiveBean> liveBeans) {
        this.liveBeans = liveBeans;
    }

    public static class LiveBean {
        private int liveID;
        private String liveName;
        private int isDefault;

        public int getLiveID() {
            return liveID;
        }

        public void setLiveID(int liveID) {
            this.liveID = liveID;
        }

        public String getLiveName() {
            return liveName;
        }

        public void setLiveName(String liveName) {
            this.liveName = liveName;
        }

        public int getIsDefault() {
            return isDefault;
        }

        public void setIsDefault(int isDefault) {
            this.isDefault = isDefault;
        }
    }
}
