package moe.moexco.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import moe.moexco.httptools.OkHttpService;
import moe.moexco.httptools.impl.HttpService;
import moe.moexco.service.BiQuGeService;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author moexco
 * @email moexco.moe@Gmail.com
 * @date 2019/9/11 11:23
 */
public class BiQuGeServiceImpl implements BiQuGeService {

    private OkHttpService httpService = new HttpService();
    private String withUrl = "https://www.xbiquge6.com";

    @Override
    public JSONObject biQuGeIndex() throws IOException {
        String url = "https://www.xbiquge6.com/";
        Response response = httpService.httpGet(url, null);
        assert response.body() != null;
        Document document = Jsoup.parse(response.body().string());

        Elements sort = document.select("div.nav ul li a");
        List<String> sortName = sort.eachText();
        List<String> sortLink = sort.eachAttr("href").stream().map(s -> url + s.replace("/", "")).collect(Collectors.toList());

        Elements books = document.select("div.novelslist div.top");
        List<String> covers = books.stream().map(e -> e.select("div.image img").attr("src")).collect(Collectors.toList());
        List<String> booksName = books.stream().map(e -> e.select("div.image img").attr("alt")).collect(Collectors.toList());
        List<String> booksLink = books.stream().map(e -> url + e.select("div.image a").attr("href")).collect(Collectors.toList());
        List<String> booksIntroduction = books.stream().map(e -> e.select("dd").text()).collect(Collectors.toList());

        JSONObject jsonObject = new JSONObject();
        JSONArray jsonTitleArray = new JSONArray();
        for (int i = 0; i < sortName.size(); i++) {
            if (!sortName.get(i).endsWith("书架")) {
                JSONObject jsonTitle = new JSONObject();
                jsonTitle.put("sortName", sortName.get(i));
                jsonTitle.put("sortLink", sortLink.get(i));
                jsonTitleArray.add(jsonTitle);
            }
        }
        JSONArray jsonBookArray = new JSONArray();
        for (int i = 0; i < booksName.size(); i++) {
            JSONObject jsonBook = new JSONObject();
            jsonBook.put("bookName", booksName.get(i));
            jsonBook.put("bookCover", covers.get(i));
            jsonBook.put("bookLink", booksLink.get(i));
            jsonBook.put("bookIntroduction", booksIntroduction.get(i));
            jsonBookArray.add(jsonBook);
        }
        jsonObject.put("Title", jsonTitleArray);
        jsonObject.put("Body", jsonBookArray);
        return jsonObject;
    }

    @Override
    public List<JSONObject> biQuSearch(String value, String page) throws IOException {
        String url = "https://www.xbiquge6.com/search.php?keyword=";
        String urlEndWith = "&page=";

        if (null != page && !"".equals(page)) {
            url = url + value + urlEndWith + page;
        } else {
            url = url + value;
        }

        Response response = httpService.httpGet(url, null);
        Document document = Jsoup.parse(response.body().string());

        String allPage = document.select(":contains(末页)").attr("href").split("&page=")[1];
        if (null != allPage && Integer.parseInt(page) > Integer.parseInt(allPage)) {
            return null;
        }

        Elements list = document.select("div.result-item.result-game-item");
        List<String> introductionList = list.stream().map(e -> e.selectFirst("p.result-game-item-desc").text()).filter(Objects::nonNull).collect(Collectors.toList());
        List<String> nameList = list.select("div.result-game-item-detail a").eachAttr("title");
        List<String> links = list.select("div.result-game-item-detail a.result-game-item-title-link").eachAttr("href");
        List<String> coverList = list.select("img.result-game-item-pic-link-img").eachAttr("src");
        List<String> authorList = list.select(":contains(作者) + span").eachText();
        List<String> sortList = list.select(":contains(类型) + span").eachText();
        List<String> updateTimeList = list.select(":contains(更新时间) + span").eachText();
        List<String> latestChapterList = list.select(":contains(最新章节) + a").eachText();

        List<JSONObject> data = new ArrayList<>();
        for (int i = 0; i < nameList.size(); i++) {
            JSONObject jsonBook = new JSONObject();
            jsonBook.put("bookName", nameList.get(i));
            jsonBook.put("bookCover", coverList.get(i));
            jsonBook.put("bookLink", links.get(i));
            jsonBook.put("introduction", introductionList.get(i));
            jsonBook.put("author", authorList.get(i));
            jsonBook.put("sort", sortList.get(i));
            jsonBook.put("updateTime", updateTimeList.get(i));
            jsonBook.put("latestChapter", latestChapterList.get(i));
            data.add(jsonBook);
        }
        return data;
    }

    @Override
    public JSONObject biQuBook(String url) throws IOException {
        Response response = httpService.httpGet(url, null);
        Document document = Jsoup.parse(response.body().string());

        String title = document.selectFirst("div#info > h1").text();
        String author = document.selectFirst("div#info > p").text().split("：")[1];
        String status = document.selectFirst("div#info > p + p").text().split(",")[0].split("：")[1];
        String latestUpdateTime = document.selectFirst(":containsOwn(最后更新：)").text().split("：")[1];
        String[] updateChapter = document.selectFirst(":containsOwn(最新章节：)").text().split("：");
        String latestUpdateChapter;
        if (updateChapter.length < 2) {
            latestUpdateChapter = "暂无";
        } else {
            latestUpdateChapter = updateChapter[1];
        }
        String introduction = document.selectFirst("div#intro").text();
        String cover = document.select("div#fmimg img").attr("src");

        List<String> chapterNameList = document.select("div#list dd").eachText();
        List<String> linkList = document.select("div#list dd a").eachAttr("href").stream().map(e -> withUrl + e).collect(Collectors.toList());

        List<JSONObject> chapterArray = new ArrayList<>();
        for (int i = 0; i < chapterNameList.size(); i++) {
            JSONObject chapterJson = new JSONObject();
            chapterJson.put("chapterName", chapterNameList.get(i));
            chapterJson.put("chapterLink", linkList.get(i));
            chapterArray.add(chapterJson);
        }

        JSONObject data = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", title);
        jsonObject.put("author", author);
        jsonObject.put("status", status);
        jsonObject.put("latestUpdateTime", latestUpdateTime);
        jsonObject.put("latestUpdateChapter", latestUpdateChapter);
        jsonObject.put("introduction", introduction);
        jsonObject.put("chapter", chapterArray);
        jsonObject.put("cover", cover);
        data.put("title", jsonObject);
        data.put("body", chapterArray);

        return data;
    }

    @Override
    public JSONObject biQuChapter(String url) throws IOException {
        Response response = httpService.httpGet(url, null);
        assert response.body() != null;
        Document document = Jsoup.parse(response.body().string());

        String title = document.selectFirst("div.bookname > h1").text();
        String content = document.select("div#content").text().replace(" ", "\n\n");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("title", title);
        jsonObject.put("content", content);

        return jsonObject;

    }

    @Override
    public List<JSONObject> biQuSortAnalysis(String url) throws IOException {
        Response response = httpService.httpGet(url, null);
        assert response.body() != null;
        Document document = Jsoup.parse(response.body().string());
        List<String> bookName = document.select("div.item div.image img").eachAttr("alt");
        List<String> bookLink = document.select("div.item div.image a").eachAttr("href").stream().map(e -> withUrl + e).collect(Collectors.toList());

        bookName.addAll(document.select("span.s2 a").eachText());
        bookLink.addAll(document.select("span.s2 a").eachAttr("href").stream().map(e -> withUrl + e).collect(Collectors.toList()));

        List<JSONObject> list = new ArrayList<>();
        for (int i = 0; i < bookName.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("bookName", bookName.get(i));
            jsonObject.put("bookLink", bookLink.get(i));
            list.add(jsonObject);
        }
        return list;
    }

}
