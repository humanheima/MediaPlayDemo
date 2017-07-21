package com.humanheima.videoplayerdemo.network.ApiEntity;


import com.humanheima.videoplayerdemo.Constant;
import com.humanheima.videoplayerdemo.util.MD5Util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenchao on 16/10/19.
 * cc@cchao.org
 * 所有直播节目
 */
public class LiveAllEntity {

    private static final String INTERFACE_NAME = "live_list";

    public static Map<String, Object> getParam() {
        Map<String, Object> param = new HashMap<>();
        Map<String, Object> map = new HashMap<>();
        String time = String.valueOf(System.currentTimeMillis() / 1000);
        String md5 = MD5Util.md5(INTERFACE_NAME
                .concat(time)
                .concat(Constant.SECURITY));
        param.put("verify", md5);
        map.put("cmd", INTERFACE_NAME);
        map.put("ts", time);
        map.put("params", param);
        return map;
    }
}
