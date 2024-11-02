package top.kangyaocoding.chatglm.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Model {
    /** 智谱AI 23年06月发布 */
    /* CHATGLM_TURBO("chatglm_turbo", "适用于对知识量、推理能力、创造力要求较高的场景"), */
    /**
     * 智谱AI 24年01月发布
     */
    GLM_3_5_TURBO("glm-3-turbo", "适用于对知识量、推理能力、创造力要求较高的场景"),
    GLM_4_FLASH("glm-4-flash", "免费模型：智谱AI首个免费大模型API，兼具“高速度”和“经济性”两大特点，支持128K上下文。"),
    GLM_4_PLUS("glm-4-plus", "高智能旗舰: 性能全面提升，长文本和复杂任务能力显著增强"),
    GLM_4_AIR("glm-4-air", "性价比最高的版本，综合性能接近GLM-4，具有128k上下文，速度快，价格实惠"),
    GLM_4V("glm-4v", "根据输入的自然语言指令和图像信息完成任务，推荐使用 SSE 或同步调用方式请求接口"),
    COGVIEW_3("cogview-3", "根据用户的文字描述生成图像,使用同步调用方式请求接口"),
    ;
    private final String code;
    private final String info;
}