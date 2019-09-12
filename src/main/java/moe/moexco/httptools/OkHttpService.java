package moe.moexco.httptools;



import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Response;

import java.io.IOException;

/**
 * @author 虚无
 */
public interface OkHttpService {

    /**
     * 发送一个GET请求，使用默认headers则传入null
     * @param url
     * @param headers
     * @return
     * @throws IOException
     */
    Response httpGet(String url, Headers headers) throws IOException;


    /**
     * 发送一个POST请求，使用默认headers则传入null
     * @param url
     * @param headers
     * @param body
     * @return
     * @throws IOException
     */
    Response httpPost(String url, Headers headers, FormBody body) throws IOException;
}
