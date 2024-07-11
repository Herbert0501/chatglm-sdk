package top.kangyaocoding.chatglm.session;

import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import top.kangyaocoding.chatglm.model.ChatCompletionRequest;
import top.kangyaocoding.chatglm.model.ChatCompletionSyncResponse;
import top.kangyaocoding.chatglm.model.ImageCompletionRequest;
import top.kangyaocoding.chatglm.model.ImageCompletionResponse;

import java.util.concurrent.CompletableFuture;

public interface OpenAISession {

    EventSource completions(ChatCompletionRequest chatCompletionRequest, EventSourceListener eventSourceListener) throws Exception;

    CompletableFuture<String> completions(ChatCompletionRequest chatCompletionRequest) throws Exception;

    ChatCompletionSyncResponse completionsSync(ChatCompletionRequest chatCompletionRequest) throws Exception;

    ImageCompletionResponse genImages(ImageCompletionRequest imageCompletionRequest) throws Exception;

    Configuration configuration();

}
