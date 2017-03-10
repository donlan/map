package dong.lan.mapeye.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Single;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by 梁桂栋 on 16-11-9 ： 下午12:26.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: map
 */

public class HttpManager {
    private static HttpManager manager;
    private OkHttpClient client;

    private HttpManager() {
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(1000, TimeUnit.SECONDS)
                .build();
    }

    public static HttpManager instance() {
        if (manager == null) {
            manager = new HttpManager();
        }
        return manager;
    }

    public void post(String url, Map<String, String> body, final Subscriber<String> subscriber) {
        RequestBody requestBody = null;
        FormBody.Builder builder = null;
        if (body != null && !body.isEmpty()) {
            builder = new FormBody.Builder();
            for (String key : body.keySet()) {
                builder.add(key, body.get(key));
            }
            requestBody = builder.build();
        }
        Request.Builder reqBuilder = new Request.Builder();

        reqBuilder.url(url);
        if (requestBody != null)
            reqBuilder.post(requestBody);
        final Request request = reqBuilder.build();
        Single.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                Response response = client.newCall(request).execute();
                int code = response.code();
                if(code!=200)
                    throw new Exception("status code:"+code+","+response.body().string());
                String res = response.body().string();
                response.close();
                return res;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }


    public static Map<String,String> createBody(String...strings){
        Map<String,String> body = new HashMap<>();
        int length = strings.length;
        if(length<=0 || length%2!=0)
            throw new IllegalArgumentException("Must be a multiple of 2");
        for(int i = 0;i<length;i+=2)
            body.put(strings[i],strings[i+1]);
        return body;
    }

}
