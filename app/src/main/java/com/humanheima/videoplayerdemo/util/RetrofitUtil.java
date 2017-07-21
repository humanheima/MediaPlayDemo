package com.humanheima.videoplayerdemo.util;

import com.humanheima.videoplayerdemo.Constant;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by chenchao on 16/9/27.
 * cc@cchao.org
 */
public class RetrofitUtil {

    //缓存大小
    private static final long CACHE_SIZE = 1024 * 1024 * 50;

    private static final Retrofit RETROFIT;

    static {

        OkHttpClient.Builder okHttpBuild = new OkHttpClient.Builder();
        //TODO 需要服务端返回Header Cache-Control设置与客户端一致,现在没琴梨用
//        File cacheFile = new File(App.getInstance().getCacheDir(), "PudongNews");
//        Cache cache = new Cache(cacheFile, CACHE_SIZE);
//        okHttpBuild.cache(cache);
//
//        Interceptor cacheControlInterceptor = new Interceptor() {
//            @Override
//            public Response intercept(Chain chain) throws IOException {
//                Request request = chain.request();
//                if (!NetWorkUtil.isConnected()) {
//                    request = request.newBuilder()
//                            .cacheControl(CacheControl.FORCE_CACHE)
//                            .build();
//                }
//                Response orginalResponse = chain.proceed(request);
//                if (NetWorkUtil.isConnected()) {
//                    String cacheControl = request.cacheControl().toString();
//                    return orginalResponse.newBuilder()
//                            .header("Cache-Control", cacheControl)
//                            .build();
//                } else {
//                    return orginalResponse.newBuilder()
//                            .header("Cache-Control", CacheControl.FORCE_CACHE.toString())
//                            .build();
//                }
//            }
//        };
//        okHttpBuild.addNetworkInterceptor(cacheControlInterceptor);

            okHttpBuild.interceptors().add(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request();
                    Response response = chain.proceed(request);
                    Buffer buffer = new Buffer();
                    request.body().writeTo(buffer);
                    BufferedSource source = response.body().source();
                    source.request(Long.MAX_VALUE);
                    Debug.d("Retrofit", request.method() + "-->" + request.url());
                    Debug.d("Retrofit", buffer.readUtf8());
                    Debug.d("Retrofit", source.buffer().clone().readUtf8());
                    return response;
                }
            });

        OkHttpClient client = okHttpBuild.readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();

        RETROFIT = new Retrofit.Builder()
                .client(client)
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    private RetrofitUtil() {
        throw new AssertionError("No instances");
    }

    public static <T> T create(Class<T> service) {
        return RETROFIT.create(service);
    }
}
