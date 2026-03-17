package com.gameguide.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gameguide.config.ZhipuAiProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

/**
 * 智谱 AI 服务 - 直接调用 HTTP API，不依赖 SDK 内部类型
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ZhipuAiService {

    private static final String EMBEDDING_URL = "https://open.bigmodel.cn/api/paas/v4/embeddings";
    private static final String CHAT_URL      = "https://open.bigmodel.cn/api/paas/v4/chat/completions";
    private static final MediaType JSON_TYPE   = MediaType.get("application/json; charset=utf-8");

    private final ZhipuAiProperties props;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private OkHttpClient httpClient;

    @PostConstruct
    public void init() {
        httpClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        log.info("智谱 AI HTTP 客户端初始化完成，chat={}, embedding={}",
                props.getChatModel(), props.getEmbeddingModel());
    }

    /**
     * 获取文本 Embedding 向量（2048 维）
     */
    public float[] getEmbedding(String text) {
        if (text != null && text.length() > 2048) {
            text = text.substring(0, 2048);
        }
        try {
            ObjectNode body = objectMapper.createObjectNode();
            body.put("model", props.getEmbeddingModel());
            ArrayNode input = body.putArray("input");
            input.add(text);

            String respJson = post(EMBEDDING_URL, body.toString());
            JsonNode root = objectMapper.readTree(respJson);
            JsonNode embedding = root.path("data").get(0).path("embedding");

            float[] result = new float[embedding.size()];
            for (int i = 0; i < embedding.size(); i++) {
                result[i] = (float) embedding.get(i).asDouble();
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("获取 Embedding 失败: " + e.getMessage(), e);
        }
    }

    /**
     * 流式对话，通过 SSE 推送给前端
     */
    public void chatStream(String systemPrompt, String userQuestion, SseEmitter emitter) {
        try {
            String bodyStr = buildChatBody(systemPrompt, userQuestion, true);

            Request request = new Request.Builder()
                    .url(CHAT_URL)
                    .header("Authorization", "Bearer " + props.getApiKey())
                    .header("Content-Type", "application/json")
                    .header("Accept", "text/event-stream")
                    .post(RequestBody.create(bodyStr, JSON_TYPE))
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful() || response.body() == null) {
                    throw new IOException("智谱 API 响应异常: " + response.code());
                }
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(response.body().byteStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.startsWith("data:")) continue;
                    String data = line.substring(5).trim();
                    if ("[DONE]".equals(data)) {
                        emitter.send(SseEmitter.event().name("done").data("[DONE]"));
                        emitter.complete();
                        return;
                    }
                    if (data.isEmpty()) continue;
                    JsonNode chunk = objectMapper.readTree(data);
                    JsonNode choices = chunk.path("choices");
                    if (choices.isArray() && choices.size() > 0) {
                        JsonNode content = choices.get(0).path("delta").path("content");
                        if (!content.isMissingNode() && !content.isNull()) {
                            emitter.send(SseEmitter.event().data(content.asText()));
                        }
                        String finish = choices.get(0).path("finish_reason").asText("");
                        if ("stop".equals(finish)) {
                            emitter.send(SseEmitter.event().name("done").data("[DONE]"));
                            emitter.complete();
                            return;
                        }
                    }
                }
                emitter.complete();
            }
        } catch (IOException e) {
            log.error("流式对话 IO 异常", e);
            sendError(emitter, "AI 服务异常，请稍后重试");
        } catch (Exception e) {
            log.error("流式对话异常", e);
            sendError(emitter, "AI 服务异常，请稍后重试");
        }
    }

    /**
     * 同步对话（备用）
     */
    public String chat(String systemPrompt, String userQuestion) {
        try {
            String bodyStr = buildChatBody(systemPrompt, userQuestion, false);
            String respJson = post(CHAT_URL, bodyStr);
            JsonNode root = objectMapper.readTree(respJson);
            return root.path("choices").get(0).path("message").path("content").asText("");
        } catch (Exception e) {
            throw new RuntimeException("同步对话失败: " + e.getMessage(), e);
        }
    }

    // ---- 私有工具方法 ----

    private String buildChatBody(String systemPrompt, String userQuestion, boolean stream) {
        try {
            ObjectNode body = objectMapper.createObjectNode();
            body.put("model", props.getChatModel());
            body.put("stream", stream);
            ArrayNode messages = body.putArray("messages");
            ObjectNode sys = messages.addObject();
            sys.put("role", "system");
            sys.put("content", systemPrompt);
            ObjectNode user = messages.addObject();
            user.put("role", "user");
            user.put("content", userQuestion);
            return objectMapper.writeValueAsString(body);
        } catch (Exception e) {
            throw new RuntimeException("构建请求体失败", e);
        }
    }

    private String post(String url, String jsonBody) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + props.getApiKey())
                .post(RequestBody.create(jsonBody, JSON_TYPE))
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new IOException("HTTP " + response.code() + ": " + response.message());
            }
            return response.body().string();
        }
    }

    private void sendError(SseEmitter emitter, String msg) {
        try {
            emitter.send(SseEmitter.event().name("error").data(msg));
            emitter.complete();
        } catch (IOException ex) {
            emitter.completeWithError(ex);
        }
    }
}
