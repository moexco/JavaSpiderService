package moe.moexco.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import moe.moexco.httptools.impl.HttpService;
import moe.moexco.service.ZhiYinManKeService;
import moe.moexco.httptools.OkHttpService;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author moexco
 * @email moexco.moe@Gmail.com
 * @date 2019/9/11 14:20
 */
public class ZhiYinManKeServiceImpl implements ZhiYinManKeService {

    private OkHttpService httpService = new HttpService();

    @Override
    public List<JSONObject> getAllComic() throws IOException {

        String indexUrl = "https://www.zymk.cn/nodeapi/comic/allComic/";
        String withUrl = "https://www.zymk.cn/";
        Response response = httpService.httpGet(indexUrl, null);
        assert response.body() != null;
        Document document = Jsoup.parse(response.body().string());
        String data = document.selectFirst("body").text();
        JSONObject jsonObject = JSON.parseObject(data);
        JSONArray jsonArray = (JSONArray) jsonObject.get("data");

        List<JSONObject> list = jsonArray.stream().map(e -> {
            JSONObject object = JSON.parseObject(e.toString());
            JSONObject jsonData = new JSONObject();
            List<String> imgUrlCode = new ArrayList<>();
            String str = String.format("%9d", Integer.valueOf(object.getString("comic_id"))).replace(" ", "0");
            for (int i = 0; i < 9; i += 3) {
                imgUrlCode.add(str.substring(i, i + 3));
            }
            jsonData.put("comicName", object.get("comic_name"));
            jsonData.put("url", withUrl + object.get("comic_id"));
            jsonData.put("imgUrl", "https://image.zymkcdn.com/file/cover/" + imgUrlCode.get(0) + "/" + imgUrlCode.get(1) + "/" + imgUrlCode.get(2) + ".jpg-300x400.webp");
            return jsonData;
        }).collect(Collectors.toList());

        return list;
    }

    @Override
    public JSONObject getAllChapters(String url) throws IOException {

        Response response = httpService.httpGet(url, null);
        assert response.body() != null;
        Document document = Jsoup.parse(response.body().string());
        Elements html = document.select("ul#chapterList li");
        String comicIntroduce = document.selectFirst("div.desc-con").text();
        List<String> chapterLinkList = html.select("a").eachAttr("href").stream().map(e -> url + "/" + e).collect(Collectors.toList());
        List<String> chapterNameList = html.select("a").eachAttr("title");
        List<JSONObject> list = new ArrayList<>();
        for (int i = 0; i < chapterLinkList.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("chapterName", chapterNameList.get(i));
            jsonObject.put("chapterLink", chapterLinkList.get(i));
            list.add(jsonObject);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("comicIntroduce", comicIntroduce);
        jsonObject.put("chapters", list);
        return jsonObject;
    }

    @Override
    public List<String> getChapterImages(String url) throws IOException {

        Response response = httpService.httpGet(url, null);
        assert response.body() != null;
        Document document = Jsoup.parse(response.body().string());
        String totalPage = document.selectFirst("span.totalPage").text();

        StringBuilder imgPath = new StringBuilder();

        String data = document.selectFirst("div.comiclist script").toString();
        Matcher matcherAddr =  Pattern.compile("chapter_addr:\"(.*?)\"").matcher(data);
        Matcher matcherId = Pattern.compile("chapter_id:(.*?),").matcher(data);
        if (matcherAddr.find() && matcherId.find()) {
            String a = matcherAddr.group().replace("chapter_addr:", "").replace("\"", "");
            String id = matcherId.group().replace("chapter_id:", "").replace(",", "");
            for (int i = 0; i < a.length(); i++) {
                String b =  fromCharCode(new String(a.substring(i, i+1).getBytes("unicode"),"unicode").hashCode()- Integer.parseInt(id)%10);
                imgPath.append(b);
            }
        }else {
            return null;
        }

        List<String> list = new ArrayList<>();
        for (int i = 1; i <= Integer.valueOf(totalPage); i++) {
            list.add("https://mhpic.zymkcdn.com/comic/" + imgPath + String.valueOf(i) + ".jpg-zymk.high.webp");
        }
        return list;
    }

    private String fromCharCode(int... codePoints) {
        return new String(codePoints, 0, codePoints.length);
    }
}
