package com.chen.library.api;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.chen.library.application.BaseApplication;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.CompositeException;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/3/6.
 */

public class ApiClient {
    private static MallApiService serviceMall;
    public static final String SERVER_BASE_MALL = "https://api.jzyinke.com/yinke-api";
    //    public static final String SERVER_BASE_MALL = "http://192.168.8.235:6110/yinke-api";
    public static final String SERVER_MALL_API = SERVER_BASE_MALL + "/api/";
    //    public static final String SERVER = "http://192.168.1.128:8082/yinke-api";
//    public static final String SERVER = "http://whale.imwork.net/yinke-api";
    public static final String IMAGE_DOWNLOAD = SERVER_BASE_MALL;

    private static Retrofit.Builder builderMall = new Retrofit.Builder().baseUrl(SERVER_MALL_API).addCallAdapterFactory(RxJavaCallAdapterFactory
            .create()).addConverterFactory(GsonConverterFactory.create());

    public static MallApiService getMallApi() {
        if (serviceMall == null) {
            synchronized (MallApiService.class) {
                if (serviceMall == null) {
                    serviceMall = ServiceGenerator.createService(MallApiService.class, builderMall);
                }
            }
        }
        return serviceMall;
    }


    @NonNull
    public static String getShopCommodityDetailUrl(String id) {
        return ApiClient.SERVER_MALL_API + "shop/v1/getShopDetail.yk?id=" + id;
    }

    public static class ServiceGenerator {
        public static <S> S createService(Class<S> serviceClass, Retrofit.Builder retrofitBuilder) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(10, TimeUnit.SECONDS);
            builder.readTimeout(0, TimeUnit.SECONDS);//0为无限时间
            builder.writeTimeout(0, TimeUnit.SECONDS);//0为无限时间
            builder.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    okhttp3.Request original = chain.request();
                    String authToken = null;
//                    authToken = "6AD77CF727B647A288039C46217F954C";
                    okhttp3.Request.Builder requestBuilder = original.newBuilder().header("Accept", "application/json").method(original.method(),
                            original.body());
                    if (!TextUtils.isEmpty(authToken)) {
                        requestBuilder.header("token", authToken);
                    }
                    original = requestBuilder.build();
                    Response proceed = chain.proceed(original);
                    return proceed;
                }
            });
            final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT);
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(interceptor);
            OkHttpClient okClient = builder.build();
            Retrofit retrofit = retrofitBuilder.client(okClient).build();
            return retrofit.create(serviceClass);
        }

//        public static <S> S createServiceTest(Class<S> serviceClass) {
//            OkHttpClient.Builder builder = new OkHttpClient.Builder();
//            final String authToken = null;
////            final String authToken = "b5bfac1e-600c-4237-8dfe-93f249889038";
//            if (!isEmpty(authToken)) {
//                builder.addInterceptor(new Interceptor() {
//                    @Override
//                    public Response intercept(Chain chain) throws IOException {
//                        okhttp3.Request original = chain.request();
//
//                        // Request customization: add request headers
//                        okhttp3.Request.Builder requestBuilder = original.newBuilder().header("Authorization", "Bearer " + authToken).method
//                                (original.method(), original.body());
//
//                        okhttp3.Request request = requestBuilder.build();
//                        return chain.proceed(request);
//                    }
//                });
//            }
//            builder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
//            OkHttpClient okClient = builder.build();
//            Retrofit retrofit = ServiceGenerator.builder.client(okClient).build();
//            return retrofit.create(serviceClass);
//        }
    }


//    @Test
//    public void test() throws Exception {
//        System.setProperty("http.proxyHost", "localhost");
//        System.setProperty("http.proxyPort", "8888");
//        final Observable<Result<List<BarMember>>> barAdmin = ServiceGenerator.createServiceTest(ApiService.class).getBarAdmin
//                ("06b62e51-310b-11e6-b0d6-305a3a599e75");
//        flatResult(barAdmin).subscribe(new SubscriberNetWork<List<BarMember>>() {
//            @Override
//            public void onErrorM(Throwable e) {
//                System.out.println(e);
//            }
//
//
//            @Override
//            public void onCompleted() {
//                System.out.println("onCompleted");
//            }
//
//            @Override
//            public void onNext(List<BarMember> barMembers) {
//                System.out.println(barMembers);
//            }
//        });
//    }

//    @Test
//    public void test() throws Exception {
//        Observable.create(new Observable.OnSubscribe<Object>() {
//            @Override
//            public void call(Subscriber<? super Object> subscriber) {
////                System.out.println("1");
//                subscriber.onNext(1);
////                subscriber.onCompleted();
//            }
//        }).concatMap(new Func1<Object, Observable<?>>() {
//            @Override
//            public Observable<?> call(Object o) {
//                return Observable.create(new Observable.OnSubscribe<Object>() {
//                    @Override
//                    public void call(Subscriber<? super Object> subscriber) {
////                        System.out.println("2");
//                        subscriber.onNext(2);
////                        subscriber.onCompleted();
//                    }
//                });
//            }
//        }).concatMap(new Func1<Object, Observable<?>>() {
//            @Override
//            public Observable<?> call(Object o) {
//                return Observable.create(new Observable.OnSubscribe<Object>() {
//                    @Override
//                    public void call(Subscriber<? super Object> subscriber) {
////                        System.out.println("3");
//                        subscriber.onNext(3);
////                        subscriber.onCompleted();
//                    }
//                });
//            }
//        }).subscribe(new SubscriberNetWorkWithString<Object>() {
//            @Override
//            public void onErrorM(String message, Throwable e) {
//                System.out.println("onErrorM");
//            }
//
//            @Override
//            public void onCompleted() {
//                System.out.println("onCompleted");
//            }
//
//            @Override
//            public void onNext(Object s) {
//                System.out.println(s);
//            }
//        });
//    }
//    @Test
//    public void test() throws Exception {
//        Observable.just("你好1").startWith(Observable.create(new Observable.OnSubscribe<String>() {
//            @Override
//            public void call(Subscriber<? super String> subscriber) {
//                System.out.println(2);
//                subscriber.onCompleted();
//            }
//        })).startWith(Observable.create(new Observable.OnSubscribe<Object>() {
//            @Override
//            public void call(Subscriber<? super Object> subscriber) {
//                System.out.println(1);
//                subscriber.onCompleted();
//            }
//        })).subscribe(new SubscriberNetWorkWithString<String>() {
//            @Override
//            public void onErrorM(String message, Throwable e) {
//                System.out.println("onErrorM");
//            }
//
//            @Override
//            public void onCompleted() {
//                System.out.println("onCompleted");
//            }
//
//            @Override
//            public void onNext(String s) {
//                System.out.println(s);
//            }
//        });
//    }

//    public static abstract class SubscriberNetWork<T> extends Subscriber<T> {
//        @Override
//        public final void onError(Throwable e) {
//            if (e == null) {
//                new NullPointerException().printStackTrace();
//                e = new RxRunTimeException("服务器故障,请稍后重试");
//            }
//            if (!(e instanceof RxRunTimeException)) {
//                e.printStackTrace();
//                if (e instanceof HttpException) {
//                    if (((HttpException) e).code() == 401) {
//                        BaseApplication.getInstance().reLoginByException();
//                    } else {
//                        Result result = new Result();
//                        result.code = ((HttpException) e).code();
//                        result.message = "网络请求失败,请检查网络";
//                        e = new ApiException(result);
//                    }
//                } else e = new RxRunTimeException("服务器故障,请稍后重试");
//            }
//            onErrorM(e);
//        }
//
//        public abstract void onErrorM(Throwable e);
//    }

    public static abstract class SubscriberNetWorkWithString<T> extends Subscriber<T> {

        @Override
        public final void onError(Throwable e) {
            String message = "服务器故障,请稍后重试";
            if (e == null) {
                new NullPointerException().printStackTrace();
                e = new RxRunTimeException(message);
            }
            if (e instanceof CompositeException) {
                for (Throwable throwable : ((CompositeException) e).getExceptions()) {
                    onError(throwable);
                }
            } else {
                if (!(e instanceof RxRunTimeException)) {
                    e.printStackTrace();
                    if (e instanceof HttpException) {
                        message = "网络请求失败,请检查网络";
                        if (((HttpException) e).code() == 401) {
                            BaseApplication.getInstance().reLoginByException();
                        } else {
                            Result result = new Result();
                            result.code = ((HttpException) e).code();
                            result.message = message;
                            e = new ApiException(result);
                        }
                    }
                } else if (e instanceof ApiException && ((ApiException) e).getResult().code == 4098) {
//                message = e.getMessage();
//                IntentManager.startPayVipAcitvity(YBWApplication.getInstance(), 1, Intent.FLAG_ACTIVITY_NEW_TASK);
                } else message = e.getMessage();
                onErrorM(message, e);
            }
        }

        public abstract void onErrorM(String message, Throwable e);
    }

    public static boolean isEmpty(@Nullable CharSequence str) {
        return str == null || str.length() == 0;
    }

    public static class RxRunTimeException extends RuntimeException {
        public RxRunTimeException(String message) {
            super(message);
        }
    }

    public static class ApiException extends RxRunTimeException {
        private final Result result;

        @Override
        public String getMessage() {
            return TextUtils.isEmpty(result.message) ? "服务器故障,请稍后重试" : result.message;
        }


        public <T> ApiException(Result<T> result) {
            super("网络请求失败,请检查网络");
            this.result = result;
        }

        public Result getResult() {
            return result;
        }
    }


    public static class Result<T> {
        public int code;
        public String message;
        public T data;

        static Result error() {
            return error("网络请求失败,请检查网络");
        }

        static Result error(String message) {
            Result result = new Result();
            result.message = message;
            return result;
        }

    }

    public static <T> Observable<T> flatResult(final Observable<Result<T>> result) {
        return result.concatMap(new Func1<Result<T>, Observable<T>>() {
            @Override
            public Observable<T> call(final Result<T> result) {
                return Observable.create(new Observable.OnSubscribe<T>() {
                    @Override
                    public void call(Subscriber<? super T> subscriber) {
                        Result<T> r = result;
                        if (r == null) r = Result.error();
                        switch (r.code) {
                            case 0:
                                subscriber.onNext(r.data);
                                break;
                            default:
                                subscriber.onError(new ApiException(r));
                                break;
                        }
                        subscriber.onCompleted();
                    }
                }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
    }

    public static <T> Observable.Transformer<T, T> applySchedulers() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> observable) {
                return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

}
