package com.humanheima.videoplayerdemo.network;


import com.humanheima.videoplayerdemo.network.api.Api;
import com.humanheima.videoplayerdemo.util.RetrofitUtil;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by chenchao on 16/9/27.
 * cc@cchao.org
 */
public class NetWork {

    private static Api api;

    public static Api getApi() {
        if (api == null) {
            api = RetrofitUtil.create(Api.class);
        }
        return api;
    }

    /**
     * 对网络接口返回的Response进行分割操作
     *
     * @param response
     * @param <T>
     * @return
     */
    public static <T> Observable<T> flatResponse(final HttpResult<T> response) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                if (response.isSuccess()) {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(response.getBody());
                    }
                } else {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(new APIException(response.getResultCode(), response.getResultMessage()));
                    }
                    return;
                }
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                }

            }
        });
    }

    /**
     * 自定义异常，当接口返回的{link Response#code}不为{link Constant#RESULT_SUCCESS}时，需要跑出此异常
     * eg：登陆时验证码错误；参数为传递等
     */
    public static class APIException extends Exception {

        public int code;

        public String message;

        public APIException(int code, String message) {
            this.code = code;
            this.message = message;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }
}
