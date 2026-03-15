package com.gameguide.spider;

import cn.hutool.core.util.ObjectUtil;
import com.gameguide.config.SpiderProperties;
import com.gameguide.dao.GameGuideSpiderDao;
import com.gameguide.entity.GameGuideSpider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(SpiderProperties.class)
public class VgoverSpiderStrategy implements SpiderStrategy {

    private static final String BASE_URL = "https://www.vgover.com";

    private final GameGuideSpiderDao gameGuideSpiderDao;
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
    public void spider() {
        spider(spiderProperties.getVgover().getStartPage(), spiderProperties.getVgover().getEndPage());
    }

    /**
     * 爬取指定页码范围的文章
     *
     * @param startPage 起始页码
     * @param endPage   结束页码
     */
    public void spider(int startPage, int endPage) {
        SpiderProperties.VgoverConfig config = spiderProperties.getVgover();
        String topicId = config.getTopicId();
        long delayMillis = config.getDelayMillis();

        log.info("开始爬取 Vgover，页码范围: {} - {}，topic: {}", startPage, endPage, topicId);

        for (int page = startPage; page <= endPage; page++) {
            try {
                String url = BASE_URL + "/topic/" + topicId + "?page=" + page;
                log.info("开始爬取第 {} 页: {}", page, url);

                Document doc = Jsoup.parse(get(url));
                Elements links = doc.select("a[href^=/topic/]");

                for (Element link : links) {
                    String articleUrl = BASE_URL + link.attr("href");
                    try {
                        parseArticle(articleUrl);
                    } catch (Exception e) {
                        log.error("解析文章失败，跳过: {}，原因: {}", articleUrl, e.getMessage());
                    }
                    Thread.sleep(delayMillis);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("爬虫线程被中断", e);
                break;
            } catch (Exception e) {
                log.error("爬取第 {} 页失败: {}", page, e.getMessage());
            }
        }

        log.info("爬虫任务完成");
    }

    private void parseArticle(String url) throws Exception {
        // 去重：已存在相同 source_url 则跳过
        if (gameGuideSpiderDao.selectBySourceUrl(url) != null) {
            log.debug("文章已存在，跳过: {}", url);
            return;
        }

        Document doc = Jsoup.parse(get(url));

        String title = doc.select("h1").text();
        String content = doc.select("article").text();

        if (title.isEmpty() || content.length() < 20) {
            log.debug("文章内容无效，跳过: {}", url);
            return;
        }

        GameGuideSpider gameGuideSpider = new GameGuideSpider();
        gameGuideSpider.setTitle(title);
        gameGuideSpider.setContent(content);
        gameGuideSpider.setSourceUrl(url);
        gameGuideSpider.setCreateTime(LocalDateTime.now());

        gameGuideSpiderDao.insert(gameGuideSpider);
        log.info("保存文章成功: {}", title);
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