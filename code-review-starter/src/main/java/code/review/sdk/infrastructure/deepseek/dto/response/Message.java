package code.review.sdk.infrastructure.deepseek.dto.response;

import lombok.Data;

@Data
public class Message {
    private String role;
    private String content;
}
