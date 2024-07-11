package top.kangyaocoding.chatglm.executor.result;

import okhttp3.sse.EventSourceListener;

public interface ResultHandler {

    EventSourceListener eventSourceListener(EventSourceListener eventSourceListener);

}