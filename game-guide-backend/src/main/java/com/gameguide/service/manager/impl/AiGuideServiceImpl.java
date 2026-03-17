package com.gameguide.service.manager.impl;

import com.gameguide.config.ZhipuAiProperties;
import com.gameguide.dao.GuideSearchDao;
import com.gameguide.service.ZhipuAiService;
import com.gameguide.service.manager.AiGuideService;
import com.gameguide.vo.GuideSearchVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiGuideServiceImpl implements AiGuideService {

    private final ZhipuAiService zhipuAiService;
    private final GuideSearchDao guideSearchDao;
    private final ZhipuAiProperties props;

    @Override
    public void chatStream(String question, Long gameId, SseEmitter emitter) {
        try {
            // 1. 将用户问题转为 Embedding 向量
            float[] questionVec = zhipuAiService.getEmbedding(question);
            String embeddingStr = toVectorString(questionVec);

            // 2. 向量相似度检索 Top-K 相关攻略
            List<GuideSearchVO> results = guideSearchDao.vectorSearch(
                    embeddingStr, gameId, props.getTopK());

            // 3. 若向量库无结果，降级到全文搜索
            if (results.isEmpty()) {
                log.info("向量检索无结果，降级到全文搜索，question={}", question);
                results = guideSearchDao.fullTextSearch(question, gameId);
                if (results.size() > props.getTopK()) {
                    results = results.subList(0, props.getTopK());
                }
            }

            // 4. 构建 Prompt
            String systemPrompt = buildSystemPrompt(results);

            // 5. 流式调用智谱 GLM
            zhipuAiService.chatStream(systemPrompt, question, emitter);

        } catch (Exception e) {
            log.error("AI 问答异常", e);
            try {
                emitter.send(SseEmitter.event().name("error").data("AI 服务暂时不可用，请稍后重试"));
                emitter.complete();
            } catch (IOException ex) {
                emitter.completeWithError(ex);
            }
        }
    }

    @Override
    @Async
    public void generateAndSaveEmbedding(Long guideId, String title, String content,
                                          Long gameId, String gameName) {
        try {
            String text = title + "\n" + (content.length() > 1000 ? content.substring(0, 1000) : content);
            float[] vec = zhipuAiService.getEmbedding(text);
            String embeddingStr = toVectorString(vec);
            guideSearchDao.updateEmbedding(guideId, embeddingStr);
            log.info("Embedding 保存成功，guideId={}", guideId);
        } catch (Exception e) {
            log.error("生成 Embedding 失败，guideId={}", guideId, e);
        }
    }

    @Override
    @Async
    public void batchGenerateEmbedding() {
        List<com.gameguide.entity.GuideSearch> list = guideSearchDao.selectWithoutEmbedding();
        log.info("开始批量生成 Embedding，共 {} 条", list.size());
        int success = 0, fail = 0;
        for (com.gameguide.entity.GuideSearch gs : list) {
            try {
                String text = gs.getTitle() + "\n"
                        + (gs.getContent().length() > 1000 ? gs.getContent().substring(0, 1000) : gs.getContent());
                float[] vec = zhipuAiService.getEmbedding(text);
                guideSearchDao.updateEmbedding(gs.getGuideId(), toVectorString(vec));
                success++;
                log.info("Embedding 生成成功，guideId={}", gs.getGuideId());
                // 每条间隔 200ms，避免触发智谱 API 限速
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("批量 Embedding 被中断");
                break;
            } catch (Exception e) {
                fail++;
                log.error("Embedding 生成失败，guideId={}, error={}", gs.getGuideId(), e.getMessage());
            }
        }
        log.info("批量 Embedding 完成，成功={}, 失败={}", success, fail);
    }

    // ---- 构建系统 Prompt（使用 application.yml 中配置的模板） ----
    private String buildSystemPrompt(List<GuideSearchVO> contexts) {
        if (contexts.isEmpty()) {
            return props.getPrompts().getNoContext();
        }
        StringBuilder contextText = new StringBuilder();
        for (int i = 0; i < contexts.size(); i++) {
            GuideSearchVO vo = contexts.get(i);
            contextText.append(String.format("[攻略%d] 《%s》- %s\n摘要：%s\n\n",
                    i + 1,
                    vo.getGameName() != null ? vo.getGameName() : "未知游戏",
                    vo.getTitle(),
                    stripHtml(vo.getHeadline() != null ? vo.getHeadline() : "")));
        }
        return props.getPrompts().getSystem()
                .replace("{context}", contextText.toString());
    }

    /** float[] 转 pgvector 字符串格式，如 [0.1,0.2,...] */
    private String toVectorString(float[] vec) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < vec.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(vec[i]);
        }
        sb.append(']');
        return sb.toString();
    }

    /** 简单去除 HTML 标签 */
    private String stripHtml(String html) {
        return html.replaceAll("<[^>]+>", "");
    }
}

