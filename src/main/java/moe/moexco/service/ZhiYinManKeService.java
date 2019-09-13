package moe.moexco.service;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * @author moexco
 * @email moexco.moe@Gmail.com
 * @date 2019/9/11 14:20
 */
public interface ZhiYinManKeService {

    /**
     * 获取知音漫客全部漫画，返回封面、名字、链接
     * @return
     * @throws IOException
     */
    List<JSONObject> getAllComic() throws IOException;


    /**
     * 获取漫画的全部章节，返回漫画介绍，章节，链接
     * @param url
     * @return
     * @throws IOException
     */
    JSONObject getAllChapters(String url) throws  IOException;

    /**
     * 获取漫画的全部页， 返回链接list
     * @param url
     * @return
     * @throws IOException
     */
    List<String> getChapterImages(String url) throws IOException;


    List<JSONObject> comicSearch(String value, List<JSONObject> data) throws IOException;

}
