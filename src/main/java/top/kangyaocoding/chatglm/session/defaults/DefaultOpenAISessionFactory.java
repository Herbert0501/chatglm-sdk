package top.kangyaocoding.chatglm.session.defaults;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import top.kangyaocoding.chatglm.IOpenAIApi;
import top.kangyaocoding.chatglm.executor.Executor;
import top.kangyaocoding.chatglm.interceptor.OpenAiHTTPInterceptor;
import top.kangyaocoding.chatglm.model.Model;
import top.kangyaocoding.chatglm.session.Configuration;
import top.kangyaocoding.chatglm.session.OpenAISession;
import top.kangyaocoding.chatglm.session.OpenAISessionFactory;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class DefaultOpenAISessionFactory implements OpenAISessionFactory {

    private final Configuration configuration;

    public DefaultOpenAISessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public OpenAISession openSession() {
        // 1. 日志配置
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(configuration.getLevel());

        // 2. 开启 Http 客户端
        OkHttpClient okHttpClient = new OkHttpClient
                .Builder()
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(new OpenAiHTTPInterceptor(configuration))
                .connectTimeout(configuration.getConnectTimeout(), TimeUnit.SECONDS)
                .writeTimeout(configuration.getWriteTimeout(), TimeUnit.SECONDS)
                .readTimeout(configuration.getReadTimeout(), TimeUnit.SECONDS)
                .build();

        configuration.setOkHttpClient(okHttpClient);

        // 3. 创建 API 服务
        IOpenAIApi openAiApi = new Retrofit.Builder()
                .baseUrl(configuration.getApiHost())
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .build().create(IOpenAIApi.class);

        configuration.setOpenAIApi(openAiApi);

        // 4. 实例化执行器
        HashMap<Model, Executor> executorGroup = configuration.newExecutorGroup();

        return new DefaultOpenAISession(configuration, executorGroup);
    }

}