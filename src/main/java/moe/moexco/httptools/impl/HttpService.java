package moe.moexco.httptools.impl;


import moe.moexco.httptools.OkHttpService;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author moexco
 * @email moexco.moe@Gmail.com
 * @date 2019/9/9 09:04
 */
public class HttpService implements OkHttpService {

    private Headers defaultHeaders() {
        Map<String, String> header = new HashMap<>();
        header.put("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3724.8 Safari/537.36");
        return Headers.of(header);
    }

    @Override
    public Response httpGet(String url, Headers headers) throws IOException {
        if (null == headers) {
            headers = defaultHeaders();
        }
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .headers(headers)
                .build();
        return httpClient.newCall(request).execute();
    }

    @Override
    public Response httpPost(String url, Headers headers, FormBody body) throws IOException {
        if (null == headers) {
            headers = defaultHeaders();
        }
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .headers(headers)
                .build();
        return httpClient.newCall(request).execute();
    }

}
