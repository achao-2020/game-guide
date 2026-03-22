package com.gameguide.spider;

import cn.hutool.core.util.ObjectUtil;
import com.gameguide.config.SpiderProperties;
import com.gameguide.dao.GameGuideSpiderDao;
import com.gameguide.dao.GameGuideSpiderTaskDao;
import com.gameguide.entity.GameGuideSpider;
import com.gameguide.entity.GameGuideSpiderTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(SpiderProperties.class)
public class VgoverSpiderStrategy implements SpiderStrategy {

    private static final String BASE_URL = "https://www.vgover.com";

    private final GameGuideSpiderDao gameGuideSpiderDao;
    private final GameGuideSpiderTaskDao gameGuideSpiderTaskDao;
    private final OkHttpClient okHttpClient;
    private final SpiderProperties spiderProperties;

    @Override
    public boolean supports(String site) {
        if (ObjectUtil.isEmpty(site)) {
            log.warn("站点名称不能为空");
            return false;
        }
        return site.startsWith(BASE_URL);
    }

    @Override
    public void spider(String site) {
        SpiderProperties.VgoverConfig config = spiderProperties.getVgover();
        String topicId = config.getTopicId();

        log.info("开始爬取 Vgover，site: {}，topic: {}", site, topicId);

        // 创建任务记录（title 先用默认值，爬到第一篇文章后回填）
        GameGuideSpiderTask task = new GameGuideSpiderTask();
        task.setUrl(site);
        task.setPageSize(1);
        task.setTitle(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                + "-爬取游戏攻略");
        gameGuideSpiderTaskDao.insert(task);
        Long taskId = task.getId();
        log.info("创建爬虫任务，taskId={}", taskId);

        try {
            log.info("开始爬取: {}", site);

            Document doc = Jsoup.parse(get(site));
            Elements linkEl = doc.select("head > meta:nth-child(8)");
            if (linkEl.isEmpty()) {
                log.warn("无法获取文章链接，跳过: {}", site);
            }
            String articleUrl = linkEl.attr("content");
            try {
                String savedTitle = parseArticle(articleUrl, taskId);
                // 用第一篇成功保存的文章标题回填任务标题
                if (savedTitle != null) {
                    gameGuideSpiderTaskDao.updateTitle(taskId, savedTitle);
                    log.info("任务标题已回填: {}", savedTitle);
                }
            } catch (Exception e) {
                log.error("解析文章失败，跳过: {}，原因: {}", articleUrl, e.getMessage());
            }
        } catch (Exception e) {
            log.error("爬取失败: {}", e.getMessage());
        }

        // 任务完成，记录完成时间
        gameGuideSpiderTaskDao.updateFinishTime(taskId);
        log.info("爬虫任务完成，taskId={}", taskId);
    }

    /**
     * 使用自定义 selector 爬取，不保存，直接返回结果列表
     */
    @Override
    public List<GameGuideSpider> spider(String site, String titleSelector, String contentSelector) {
        log.info("开始预览爬取 Vgover，site: {}", site);
        List<GameGuideSpider> results = new ArrayList<>();
        try {
            Document doc = Jsoup.parse(get(site));
            Elements linkEl = doc.select("head > meta:nth-child(8)");
            if (linkEl.isEmpty()) {
                log.warn("无法获取文章链接，跳过: {}", site);
                return results;
            }
            String articleUrl = linkEl.attr("content");
            GameGuideSpider item = previewArticle(articleUrl, titleSelector, contentSelector);
            if (item != null) {
                results.add(item);
            }
        } catch (Exception e) {
            log.error("预览爬取失败: {}", e.getMessage());
        }
        return results;
    }

    /**
     * 解析单篇文章（不保存），使用自定义 selector
     */
    private GameGuideSpider previewArticle(String url, String titleSelector, String contentSelector) throws Exception {
        Document doc = Jsoup.parse(get(url));
        String title = doc.select(titleSelector).text();
        Elements contentEl = doc.select(contentSelector);
        String content = contentEl.html();
        if (title.isEmpty() || content.length() < 20) {
            log.debug("文章内容无效，跳过: {}", url);
            return null;
        }
        GameGuideSpider item = new GameGuideSpider();
        item.setTitle(title);
        item.setContent(content);
        item.setSourceUrl(url);
        item.setCreateTime(LocalDateTime.now());
        return item;
    }

    /**
     * 解析并保存单篇文章
     *
     * @return 保存成功时返回文章标题，已存在或无效时返回 null
     */
    private String parseArticle(String url, Long taskId) throws Exception {
        // 去重：已存在相同 source_url 则跳过
        if (gameGuideSpiderDao.selectBySourceUrl(url) != null) {
            log.debug("文章已存在，跳过: {}", url);
            return null;
        }

        Document doc = Jsoup.parse(get(url));

        String title = doc.select("head > title").text();
        //document.querySelector()
        Elements contentEl = doc.select("body > div > div.vg-main.clearfix > div.vg-content > article > div.ss-html-container.J-photoSwiper");
        if (title.isEmpty() || contentEl.html().length() < 20) {
            log.debug("文章内容无效，跳过: {}", url);
            return null;
        }

        GameGuideSpider gameGuideSpider = new GameGuideSpider();
        gameGuideSpider.setTaskId(taskId);
        gameGuideSpider.setTitle(title);
        gameGuideSpider.setContent(contentEl.html());
        gameGuideSpider.setSourceUrl(url);
        gameGuideSpider.setCreateTime(LocalDateTime.now());

        gameGuideSpiderDao.insert(gameGuideSpider);
        log.info("保存文章成功: {}", title);
        return title;
    }

    private String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            ResponseBody body = response.body();
            if (body == null) {
                throw new IOException("响应体为空: " + url);
            }
            return body.string();
        }
    }
}