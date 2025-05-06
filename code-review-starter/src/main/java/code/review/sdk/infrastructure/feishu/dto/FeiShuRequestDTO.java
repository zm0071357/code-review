package code.review.sdk.infrastructure.feishu.dto;

import lombok.Data;

@Data
public class FeiShuRequestDTO {
    private String msg_type;
    private Content content;
}
