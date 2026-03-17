package com.gameguide.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "zhipu.ai")
public class ZhipuAiProperties {
    private String apiKey;
    private String embeddingModel = "embedding-3";
    private String chatModel = "glm-4-flash";
    private int topK = 5;
    /** 定时回填 embedding 的 cron 表达式，默认每天凌晨2点 */
    private String embeddingCron = "0 0 2 * * ?";
    private Prompts prompts = new Prompts();

    @Data
    public static class Prompts {
        /** 有攻略上下文时的系统提示，{context} 占位符会被替换为实际攻略内容 */
        private String system =
                "你是一个专业的游戏攻略助手。\n" +
                "请严格根据下方提供的攻略内容回答用户问题，不要编造信息。\n" +
                "如果攻略中没有相关内容，请如实告知并建议用户浏览攻略列表。\n\n" +
                "【参考攻略内容】\n{context}";

        /** 向量检索无结果时的兜底提示 */
        private String noContext =
                "你是一个专业的游戏攻略助手。\n" +
                "当前攻略库中暂无与该问题匹配的内容，请根据通用游戏知识回答，\n" +
                "并在回答末尾注明【以上为通用建议，非攻略库内容】。";
    }
}
