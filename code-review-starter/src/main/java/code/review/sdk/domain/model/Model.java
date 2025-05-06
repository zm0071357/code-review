package code.review.sdk.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Model {
    DEEPSEEK_CHAT("deepseek-chat", "DeepSeek-V3"),
    DEEPSEEK_REASONER("deepseek-reasoner", "DeepSeek-R1"),;

    private final String code;
    private final String info;

}
