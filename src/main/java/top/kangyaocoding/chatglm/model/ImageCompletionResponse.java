package top.kangyaocoding.chatglm.model;

import lombok.Data;

import java.util.List;

@Data
public class ImageCompletionResponse {

    /**
     * 请求创建时间，是以秒为单位的Unix时间戳。
     */
    private Long created;

    private List<Image> data;

    @Data
    public static class Image {
        private String url;
    }

}