package com.gameguide.service.manager;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface AiGuideService {

    /**
     * 流式 AI 问答（SSE）
     */
    void chatStream(String question, Long gameId, SseEmitter emitter);

    /**
     * 为指定攻略生成并保存 Embedding（异步）
     */
    void generateAndSaveEmbedding(Long guideId, String title, String content,
                                   Long gameId, String gameName);

    /**
     * 批量为所有 embedding 为空的攻略生成向量（异步，用于历史数据回填）
     */
    void batchGenerateEmbedding();
}

