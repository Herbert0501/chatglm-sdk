package top.kangyaocoding.chatglm.executor.aigc;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.jetbrains.annotations.Nullable;
import top.kangyaocoding.chatglm.IOpenAIApi;
import top.kangyaocoding.chatglm.executor.Executor;
import top.kangyaocoding.chatglm.executor.result.ResultHandler;
import top.kangyaocoding.chatglm.model.*;
import top.kangyaocoding.chatglm.session.Configuration;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class GLMExecutor implements Executor, ResultHandler {

    /**
     * OpenAI 接口
     */
    private final Configuration configuration;
    /**
     * 工厂事件
     */
    private final EventSource.Factory factory;
    /**
     * 统一接口
     */
    private final IOpenAIApi openAIApi;

    private final OkHttpClient okHttpClient;

    public GLMExecutor(Configuration configuration) {
        this.configuration = configuration;
        this.factory = configuration.createRequestFactory();
        this.openAIApi = configuration.getOpenAIApi();
        this.okHttpClient = configuration.getOkHttpClient();
    }

    @Override
    public EventSource completions(ChatCompletionRequest chatCompletionRequest, EventSourceListener eventSourceListener) throws Exception {
        // 构建请求信息
        Request request = new Request.Builder()
                .url(configuration.getApiHost().concat(IOpenAIApi.chatglm_completions))
                .post(RequestBody.create(chatCompletionRequest.toString(),
                        MediaType.parse(Configuration.JSON_CONTENT_TYPE)))
                .build();

        // 返回事件结果
        return factory.newEventSource(request, chatCompletionRequest.getIsCompatible() ? eventSourceListener(eventSourceListener) : eventSourceListener);
    }

    @Override
    public CompletableFuture<String> completions(ChatCompletionRequest chatCompletionRequest) throws InterruptedException {
        // 用于执行异步任务并获取结果
        CompletableFuture<String> future = new CompletableFuture<>();
        StringBuffer dataBuffer = new StringBuffer();

        // 构建请求信息
        Request request = new Request.Builder()
                .url(configuration.getApiHost().concat(IOpenAIApi.chatglm_completions))
                .post(RequestBody.create(chatCompletionRequest.toString(),
                        MediaType.parse("application/json;charset=utf-8")))
                .build();

        factory.newEventSource(request, new EventSourceListener() {
            @Override
            public void onEvent(EventSource eventSource, @Nullable String id, @Nullable String type, String data) {
                if ("[DONE]".equals(data)) {
                    log.info("[输出结束] Tokens {}", JSON.toJSONString(data));
                    return;
                }

                ChatCompletionResponse response = JSON.parseObject(data, ChatCompletionResponse.class);
                log.info("测试结果：{}", JSON.toJSONString(response));
                List<ChatCompletionResponse.Choice> choices = response.getChoices();
                for (ChatCompletionResponse.Choice choice : choices) {
                    if (!"stop".equals(choice.getFinishReason())) {
                        dataBuffer.append(choice.getDelta().getContent());
                    }
                }

            }

            @Override
            public void onClosed(EventSource eventSource) {
                future.complete(dataBuffer.toString());
            }

            @Override
            public void onFailure(EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
                future.completeExceptionally(new RuntimeException("Request closed before completion"));
            }
        });

        return future;
    }

    @Override
    public ChatCompletionSyncResponse completionsSync(ChatCompletionRequest chatCompletionRequest) throws Exception {
        // sync 同步请求，stream 为 false
        chatCompletionRequest.setStream(false);
        // 构建请求信息
        Request request = new Request.Builder()
                .url(configuration.getApiHost().concat(IOpenAIApi.chatglm_completions))
                .post(RequestBody.create(chatCompletionRequest.toString(),
                        MediaType.parse(Configuration.JSON_CONTENT_TYPE)))
                .build();
        OkHttpClient okHttpClient = configuration.getOkHttpClient();
        Response response = okHttpClient.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new RuntimeException("Request failed");
        }
        return JSON.parseObject(response.body().string(), ChatCompletionSyncResponse.class);
    }

    /**
     * 图片生成方法
     *
     * @param imageCompletionRequest 图片生成请求对象
     * @return 图片生成响应对象
     * @throws Exception 处理过程中的异常
     */
    public ImageCompletionResponse genImages(ImageCompletionRequest imageCompletionRequest) throws Exception {
        // 构建请求信息
        Request request = new Request.Builder()
                .url(configuration.getApiHost().concat(IOpenAIApi.chatglm_cogview))
                .post(RequestBody.create(imageCompletionRequest.toString(),
                        MediaType.parse(Configuration.JSON_CONTENT_TYPE)))
                .build();

        // 执行请求
        Response response = okHttpClient.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Failed to get image response");
        }

        // 处理响应
        String responseBody = response.body().string();
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(responseBody, ImageCompletionResponse.class);
    }

    @Override
    public EventSourceListener eventSourceListener(EventSourceListener eventSourceListener) {
        return new EventSourceListener() {
            @Override
            public void onEvent(EventSource eventSource, @Nullable String id, @Nullable String type, String data) {
                if ("[DONE]".equals(data)) {
                    return;
                }
                ChatCompletionResponse response = JSON.parseObject(data, ChatCompletionResponse.class);
                if (response.getChoices() != null && 1 == response.getChoices().size() && "stop".equals(response.getChoices().get(0).getFinishReason())) {
                    eventSourceListener.onEvent(eventSource, id, EventType.finish.getCode(), data);
                    return;
                }
                eventSourceListener.onEvent(eventSource, id, EventType.add.getCode(), data);
            }

            @Override
            public void onClosed(EventSource eventSource) {
                eventSourceListener.onClosed(eventSource);
            }

            @Override
            public void onFailure(EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
                eventSourceListener.onFailure(eventSource, t, response);
            }
        };
    }

}