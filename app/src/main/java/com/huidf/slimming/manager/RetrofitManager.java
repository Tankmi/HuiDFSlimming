package com.huidf.slimming.manager;

import com.huidf.slimming.context.ApplicationData;
import com.huidf.slimming.context.PreferenceEntity;
import com.huidf.slimming.context.UrlConstant;
import com.huidf.slimming.home.HomeService;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import huitx.libztframework.utils.LOGUtils;
import huitx.libztframework.utils.NetUtils;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 作者：ZhuTao
 * 创建时间：2019/3/21 : 12:03
 * 描述：
 */
public class RetrofitManager {
    /**
     * 请求接口实例对象
     */
    private static RetrofitManager mInstance;
    private static final long DEFAULT_TIMEOUT = 60L;
    private Retrofit retrofit = null;
    //请求头信息
    private final String HEADER_CONNECTION = "keep-alive";
    private String userAgent = "Mozilla/5.0 (Windows NT 6.2; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.87 Safari/537.36";

    public static RetrofitManager getInstance() {
        if (mInstance == null) {
            synchronized (RetrofitManager.class) {
                if (mInstance == null) {
                    mInstance = new RetrofitManager();
                }
            }
        }
        return mInstance;
    }

    public Retrofit getRetrofit() {
        if (retrofit == null) {
            synchronized (RetrofitManager.class) {
                if (retrofit == null) {
                    OkHttpClient mClient = new OkHttpClient.Builder()
                            //添加公告查询参数
                            .addInterceptor(new CommonQueryParamsInterceptor())
                            .addInterceptor(new MutiBaseUrlInterceptor())
                           // 添加离线缓存目录,存储在外部存储空间
                            .cache(new Cache(new File(ApplicationData.context.getExternalFilesDir(PreferenceEntity.KEY_CACHE_PATH), "HDFS_HttpCache"), 14 * 1024 * 100))
                            .addInterceptor(new CacheInterceptor())
                            .addNetworkInterceptor(new CacheInterceptor())//必须要有，否则会返回504
                            .addInterceptor(new HeaderInterceptor())
                            .addInterceptor(new LoggingInterceptor())//添加请求拦截(可以在此处打印请求信息和响应信息)
                            .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                            .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                            .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                            .build();
                    retrofit = new Retrofit.Builder()
                            .baseUrl(UrlConstant.API_BASE)//基础URL 建议以 / 结尾
                            .addConverterFactory(GsonConverterFactory.create())//设置 Json 转换器
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//RxJava 适配器
                            .client(mClient)
                            .build();
                }
            }
        }
        return retrofit;
    }

    public HomeService getRequestService() {
        return getRetrofit().create(HomeService.class);
    }
//    public ApiService getRequestService() {
//        return getRetrofit().create(ApiService.class);
//    }

    /**
     * 设置公共请求参数
     */
    public class CommonQueryParamsInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            HttpUrl url = request.url().newBuilder()
                    .addQueryParameter("paramsA", "a")
                    .addQueryParameter("paramsB", "b")
                    .build();
            return chain.proceed(request.newBuilder().url(url).build());
        }
    }
    /**
     * 添加请求头需要携带的参数
     */
    public class HeaderInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Request requestBuilder = request.newBuilder()
                    .addHeader("Connection", HEADER_CONNECTION)
                    .addHeader("token", "token-value")
                    //.addHeader("User-Agent",userAgent) //如果天气的接口报：invalid User-Agent header，把此处注释打开即可
                    .method(request.method(), request.body())
                    .build();
            return chain.proceed(requestBuilder);
        }
    }

    /**
     * 设置缓存的拦截器
     */
    public class CacheInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!NetUtils.isAPNType()) {
                request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
            }
            Response response = chain.proceed(request);
            if (NetUtils.isAPNType()) {
                String cacheControl = request.cacheControl().toString();
                LOGUtils.LOG("Tag", "有网");
                return response.newBuilder().header("Cache-Control", cacheControl)
                        .removeHeader("Pragma").build();
            } else {
                LOGUtils.LOG("Tag", "无网");
                return response.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=" + "60 * 60 * 24 * 7")
                        .removeHeader("Pragma").build();
            }
        }
    }

    /**
     * 打印请求信息
     * log打印:http://blog.csdn.net/csdn_lqr/article/details/61420753
     */
    public class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            //这个chain里面包含了request和response，所以你要什么都可以从这里拿
            Request request = chain.request();
            long t1 = System.nanoTime();//请求发起的时间,纳秒
            String method = request.method();
            JSONObject jsonObject = new JSONObject();
            if ("POST".equals(method) || "PUT".equals(method)) {
                if (request.body() instanceof FormBody) {
                    FormBody body = (FormBody) request.body();
                    if (body != null) {
                        for (int i = 0; i < body.size(); i++) {
                            try {
                                jsonObject.put(body.name(i), body.encodedValue(i));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    LOGUtils.LOG("request", String.format("发送请求 %s on %s  %nRequestParams:%s%nMethod:%s",
                            request.url(), chain.connection(), jsonObject.toString(), request.method()));
                } else {
                    Buffer buffer = new Buffer();
                    RequestBody requestBody = request.body();
                    if (requestBody != null) {
                        request.body().writeTo(buffer);
                        String body = buffer.readUtf8();
                        LOGUtils.LOG("request", String.format("发送请求 %s on %s  %nRequestParams:%s%nMethod:%s",
                                request.url(), chain.connection(), body, request.method()));
                    }
                }
            } else {
                LOGUtils.LOG("request", String.format("发送请求 %s on %s%nMethod:%s",
                        request.url(), chain.connection(), request.method()));
            }
            Response response = chain.proceed(request);
            long t2 = System.nanoTime();//收到响应的时间
            ResponseBody responseBody = response.peekBody(1024 * 1024);
            LOGUtils.LOG("request",
                    String.format("Retrofit接收响应: %s %n返回json:【%s】 %n耗时：%.1fms",
                            response.request().url(),
                            responseBody.string(),
                            (t2 - t1) / 1e6d
                    ));
            return response;
        }

    }

    /**
     * 打印log日志：该拦截器用于记录应用中的网络请求的信息
     */
    private HttpLoggingInterceptor getHttpLogingInterceptor() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                //包含所有的请求信息
                //如果收到响应是json才打印
                if ("{".equals(message) || "[".equals(message)) {
                    LOGUtils.LOG("收到响应: " + message);
                }
                LOGUtils.LOG("message=" + message);
            }
        });
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return httpLoggingInterceptor;
    }

    private String BASE_URL_OTHER = "http://wthrcdn.etouch.cn/";

    /**
     * 添加可以处理多个Baseurl的拦截器：http://blog.csdn.net/qq_36707431/article/details/77680252
     * Retrofit(OKHttp)多BaseUrl情况下url实时自动替换完美解决方法:https://www.2cto.com/kf/201708/663977.html

     //     http://wthrcdn.etouch.cn/weather_mini?city=北京
     //    @Headers({"url_name:other"})
     //    @GET("weather_mini")
     //    Observable<WeatherEntity> getMessage(@Query("city") String city);
     */
    private class MutiBaseUrlInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            //获取request
            Request request = chain.request();
            //从request中获取原有的HttpUrl实例oldHttpUrl
            HttpUrl oldHttpUrl = request.url();
            //获取request的创建者builder
            Request.Builder builder = request.newBuilder();
            //从request中获取headers，通过给定的键url_name
            List<String> headerValues = request.headers("url_name");
            if (headerValues != null && headerValues.size() > 0) {
                //如果有这个header，先将配置的header删除，因此header仅用作app和okhttp之间使用
                builder.removeHeader("url_name");
                //匹配获得新的BaseUrl
                String headerValue = headerValues.get(0);
                HttpUrl newBaseUrl = null;
                if ("other".equals(headerValue)) {
                    newBaseUrl = HttpUrl.parse(BASE_URL_OTHER);
//                } else if ("other".equals(headerValue)) {
//                    newBaseUrl = HttpUrl.parse(BASE_URL_PAY);
                } else {
                    newBaseUrl = oldHttpUrl;
                }
                //在oldHttpUrl的基础上重建新的HttpUrl，修改需要修改的url部分
                HttpUrl newFullUrl = oldHttpUrl
                        .newBuilder()
                        .scheme("http")//更换网络协议,根据实际情况更换成https或者http
                        .host(newBaseUrl.host())//更换主机名
                        .port(newBaseUrl.port())//更换端口
                        .removePathSegment(0)//移除第一个参数v1
                        .build();
                //重建这个request，通过builder.url(newFullUrl).build()；
                // 然后返回一个response至此结束修改
                LOGUtils.LOG("Url", "intercept: " + newFullUrl.toString());
                return chain.proceed(builder.url(newFullUrl).build());
            }
            return chain.proceed(request);
        }
    }

    /**
     * Retrofit上传文件
     *
     * @param mImagePath
     * @return
     */
    public RequestBody getUploadFileRequestBody(String mImagePath) {
        File file = new File(mImagePath);
        //构建body
        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file))
                .build();
        return requestBody;
    }

}
