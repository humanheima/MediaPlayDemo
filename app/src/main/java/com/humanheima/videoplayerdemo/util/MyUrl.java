package com.humanheima.videoplayerdemo.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/14.
 */
public class MyUrl {
    public static List<String> urlList;
    private static String testVideoUrl = "http://domhttp.kksmg.com/2017/05/09/h264_450k_mp4_7f122c71a65219094da3907cbb01e14f_ncm.mp4";

    static {
        urlList = new ArrayList<>();
        urlList.add(testVideoUrl);
        urlList.add("http://domhttp.kksmg.com/2016/11/14/h264_450k_mp4_4ac875866180b930c2926f21adb1bd20_ncm.mp4");
    }

}
