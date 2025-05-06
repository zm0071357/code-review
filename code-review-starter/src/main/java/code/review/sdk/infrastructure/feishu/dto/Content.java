package code.review.sdk.infrastructure.feishu.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Content {
    private String text;
}
