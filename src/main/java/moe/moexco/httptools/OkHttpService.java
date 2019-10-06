package moe.moexco.httptools;


import okhttp3.FormBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.Map;

/**
 * @author 虚无
 */
public interface OkHttpService {

    /**
     * 发送一个GET请求，使用默认headers则传入null
     *
     * @param url
     * @param headerMap
     * @return
     * @throws IOException
     */
    Response httpGet(String url, Map<String, String> headerMap) throws IOException;


    /**
     * 发送一个POST请求，使用默认headers则传入null
     *
     * @param url
     * @param headerMap
     * @param body
     * @return
     * @throws IOException
     */
    Response httpPost(String url, Map<String, String> headerMap, FormBody body) throws IOException;
}
