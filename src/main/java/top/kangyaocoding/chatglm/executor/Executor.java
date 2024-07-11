package top.kangyaocoding.chatglm.executor;

import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import top.kangyaocoding.chatglm.model.ChatCompletionRequest;
import top.kangyaocoding.chatglm.model.ChatCompletionSyncResponse;
import top.kangyaocoding.chatglm.model.ImageCompletionRequest;
import top.kangyaocoding.chatglm.model.ImageCompletionResponse;

import java.util.concurrent.CompletableFuture;

/**
 * 描述:
 *
 * @author K·Herbert
 * @since 2024-07-10 16:14
 */
public interface Executor {

    EventSource completions(ChatCompletionRequest chatCompletionRequest, EventSourceListener eventSourceListener) throws Exception;

    CompletableFuture<String> completions(ChatCompletionRequest chatCompletionRequest) throws InterruptedException;

    ChatCompletionSyncResponse completionsSync(ChatCompletionRequest chatCompletionRequest) throws Exception;

    ImageCompletionResponse genImages(ImageCompletionRequest request) throws Exception;

}
