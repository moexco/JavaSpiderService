package moe.moexco.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;

public interface BiQuGeService {

    JSONObject biQuGeIndex() throws IOException;

    JSONArray biQuSearch(String value, String page) throws IOException;

    JSONArray biQuBook(String url) throws IOException;

    JSONObject biQuChapter(String url) throws IOException;

    JSONArray biQuSortAnalysis(String url) throws IOException;

}
