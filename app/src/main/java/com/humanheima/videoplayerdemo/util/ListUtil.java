package com.humanheima.videoplayerdemo.util;

import java.util.List;

/**
 * Created by chenchao on 16/9/28.
 * cc@cchao.org
 */
public class ListUtil {

    public static boolean isEmpty(List<?> data) {
        if (data == null || data.size() == 0) {
            return true;
        }
        return false;
    }
}
