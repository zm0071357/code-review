package code.review.sdk.infrastructure.deepseek.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Message {
    private String role;
    private String content;
}
