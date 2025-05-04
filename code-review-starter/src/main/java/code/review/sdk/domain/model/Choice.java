package code.review.sdk.domain.model;

import lombok.Data;

@Data
public class Choice {
    private int index;
    private Message message;
    private String logprobs;
    private String finish_reason;
}
