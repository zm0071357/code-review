package code.review.sdk.infrastructure.deepseek.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class DeepSeekResponseDTO {
    private String id;
    private String object;
    private long created;
    private String model;
    private List<Choice> choices;
    private Usage usage;
    private String system_fingerprint;
}
