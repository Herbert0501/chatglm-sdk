package top.kangyaocoding.chatglm.session;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSources;
import top.kangyaocoding.chatglm.IOpenAIApi;
import top.kangyaocoding.chatglm.executor.Executor;
import top.kangyaocoding.chatglm.executor.aigc.GLMExecutor;
import top.kangyaocoding.chatglm.model.Model;

import java.util.HashMap;

@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class Configuration {

    // 智普Ai 请求地址
    @Getter
    @Setter
    private String apiHost = "https://open.bigmodel.cn/";

    // 智普Ai https://open.bigmodel.cn/usercenter/apikeys - apiSecretKey = {apiKey}.{apiSecret}
    private String apiSecretKey;

    public void setApiSecretKey(String apiSecretKey) {
        this.apiSecretKey = apiSecretKey;
        String[] arrStr = apiSecretKey.split("\\.");
        if (arrStr.length != 2) {
            throw new RuntimeException("invalid apiSecretKey");
        }
        this.apiKey = arrStr[0];
        this.apiSecret = arrStr[1];
    }

    @Getter
    private String apiKey;
    @Getter
    private String apiSecret;

    // Api 服务
    @Setter
    @Getter
    private IOpenAIApi openAIApi;

    @Getter
    @Setter
    private OkHttpClient okHttpClient;

    public EventSource.Factory createRequestFactory() {
        return EventSources.createFactory(okHttpClient);
    }

    // OkHttp 配置信息
    @Setter
    @Getter
    private HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.HEADERS;
    @Setter
    @Getter
    private long connectTimeout = 450;
    @Setter
    @Getter
    private long writeTimeout = 450;
    @Setter
    @Getter
    private long readTimeout = 450;

    private HashMap<Model, Executor> executorGroup;

    public static final String SSE_CONTENT_TYPE = "text/event-stream";
    public static final String DEFAULT_USER_AGENT = "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)";
    public static final String APPLICATION_JSON = "application/json";
    public static final String JSON_CONTENT_TYPE = APPLICATION_JSON + "; charset=utf-8";

    public HashMap<Model, Executor> newExecutorGroup() {
        this.executorGroup = new HashMap<>();
        // 新版模型，配置
        Executor glmExecutor = new GLMExecutor(this);
        this.executorGroup.put(Model.GLM_3_5_TURBO, glmExecutor);
        this.executorGroup.put(Model.GLM_4, glmExecutor);
        this.executorGroup.put(Model.GLM_4V, glmExecutor);
        this.executorGroup.put(Model.COGVIEW_3, glmExecutor);
        return this.executorGroup;
    }

}