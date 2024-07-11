package top.kangyaocoding.chatglm.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
    /**
     * 用户输入的内容
     */
    user("user"),
    /**
     * 模型生成的内容
     */
    assistant("assistant"),

    /**
     * 系统
     */
    system("system"),

    ;
    private final String code;

}