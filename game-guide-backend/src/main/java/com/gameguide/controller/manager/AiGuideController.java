package com.gameguide.controller.manager;

import com.gameguide.service.manager.AiGuideService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * AI 攻略问答接口
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class AiGuideController {

    private final AiGuideService aiGuideService;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * 前台：流式 AI 问答（SSE）
     * GET /api/public/ai/chat?question=xxx
     */
    @GetMapping("/api/public/ai/chat")
    public SseEmitter chat(
            @RequestParam String question,
            @RequestParam(required = false) Long gameId) {
        SseEmitter emitter = new SseEmitter(180_000L);
        executor.execute(() -> aiGuideService.chatStream(question, gameId, emitter));
        emitter.onTimeout(emitter::complete);
        emitter.onError(e -> log.warn("SSE 连接异常: {}", e.getMessage()));
        return emitter;
    }

    /**
     * 管理后台：触发历史攻略 Embedding 批量回填
     * POST /api/admin/ai/embedding/batch
     * 需要 ADMIN 权限，异步执行，立即返回
     */
    @PostMapping("/api/admin/ai/embedding/batch")
    public com.gameguide.common.Result<String> batchEmbedding() {
        aiGuideService.batchGenerateEmbedding();
        return com.gameguide.common.Result.success("批量生成 Embedding 任务已启动，请查看后台日志");
    }
}

