package com.gameguide.config;

import com.gameguide.service.manager.AiGuideService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时任务：自动为无 embedding 的攻略生成向量
 * cron 表达式通过 application.yml 中 zhipu.ai.embedding-cron 配置
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmbeddingScheduler {

    private final AiGuideService aiGuideService;

    /**
     * 定时回填 embedding
     * 默认：每天凌晨 2:00 执行
     * 可在 application.yml 中修改 zhipu.ai.embedding-cron
     */
    @Scheduled(cron = "${zhipu.ai.embedding-cron:0 0 2 * * ?}")
    public void scheduledBatchEmbedding() {
        log.info("[定时任务] 开始执行 Embedding 批量回填...");
        try {
            aiGuideService.batchGenerateEmbedding();
        } catch (Exception e) {
            log.error("[定时任务] Embedding 批量回填异常", e);
        }
    }
}

