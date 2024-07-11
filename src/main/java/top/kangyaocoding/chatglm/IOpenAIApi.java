package top.kangyaocoding.chatglm;

/**
 * 描述: chatglm 通用类
 *
 * @author K·Herbert
 * @since 2024-07-10 0:15
 */

public interface IOpenAIApi {
    String chatglm_completions = "api/paas/v4/chat/completions";
    String chatglm_cogview = "api/paas/v4/images/generations";
    String chatglm_embeddings = "api/paas/v4/embeddings";
    String chatglm_batches = "api/paas/v4/batches";
}
