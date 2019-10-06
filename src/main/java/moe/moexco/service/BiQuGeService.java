package moe.moexco.service;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.List;

public interface BiQuGeService {

    /**
     * 获取笔趣阁小说首页分类和代表作品
     *
     * @return
     * @throws IOException
     */
    JSONObject biQuGeIndex() throws IOException;

    /**
     * 分页搜索
     *
     * @param value
     * @param page
     * @return
     * @throws IOException
     */
    List<JSONObject> biQuSearch(String value, String page) throws IOException;

    /**
     * 获取作品详细内容
     *
     * @param url
     * @return
     * @throws IOException
     */
    JSONObject biQuBook(String url) throws IOException;

    /**
     * 获取作品章节内容
     *
     * @param url
     * @return
     * @throws IOException
     */
    JSONObject biQuChapter(String url) throws IOException;

    /**
     * 获取笔趣阁分类内容
     *
     * @param url
     * @return
     * @throws IOException
     */
    List<JSONObject> biQuSortAnalysis(String url) throws IOException;

}
