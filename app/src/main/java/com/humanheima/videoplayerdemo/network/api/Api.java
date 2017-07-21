package com.humanheima.videoplayerdemo.network.api;


import com.humanheima.videoplayerdemo.model.LiveAll;
import com.humanheima.videoplayerdemo.model.LiveDetail;
import com.humanheima.videoplayerdemo.model.LiveDetailsBean;
import com.humanheima.videoplayerdemo.network.HttpResult;

import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by chenchao on 16/9/27.
 * cc@cchao.org
 */
public interface Api {

    /**
     * 直播间详情
     *
     * @param body
     * @return
     */
    @POST("index.php")
    Observable<HttpResult<LiveDetailsBean>> getLiveDetails(@Body Map body);

    /**
     * 所有直播节目
     *
     * @param body
     * @return
     */
    @POST("index.php")
    Observable<HttpResult<LiveAll>> getLiveAll(@Body Map body);

    /**
     * 获取直播节目详细信息
     *
     * @param body
     * @return
     */
    @POST("index.php")
    Observable<HttpResult<LiveDetail>> getLiveDetail(@Body Map body);

}
