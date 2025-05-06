package code.review.sdk.infrastructure.deepseek.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class DeepSeekRequestDTO {

    private String model;

    private List<Message> messages;

    private boolean stream;
}
