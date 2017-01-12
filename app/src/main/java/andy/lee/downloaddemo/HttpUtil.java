package andy.lee.downloaddemo;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * andy.lee.downloaddemo
 * Created by andy on 17-1-12.
 */

public class HttpUtil {

    public static void sendRequest(String url, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void sendRequest(String url, long downloadedLength, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .addHeader("RANGE", "bytes=" + downloadedLength + "-")
                .url(url)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
