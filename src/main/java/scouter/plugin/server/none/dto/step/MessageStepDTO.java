package scouter.plugin.server.none.dto.step;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO for MessageStep with Lombok Builder
 */
@Getter
@Builder
public class MessageStepDTO {
    private byte stepType;
    private String stepTypeName;
    private int hash;
    private int time;
    private int value;
}
