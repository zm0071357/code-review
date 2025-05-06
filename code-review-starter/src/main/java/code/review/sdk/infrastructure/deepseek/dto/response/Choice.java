package code.review.sdk.infrastructure.deepseek.dto.response;

import lombok.Data;

@Data
public class Choice {
    private int index;
    private Message message;
    private String logprobs;
    private String finish_reason;
}
