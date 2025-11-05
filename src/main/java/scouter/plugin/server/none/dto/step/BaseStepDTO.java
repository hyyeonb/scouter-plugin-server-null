package scouter.plugin.server.none.dto.step;

import lombok.Getter;

/**
 * Base DTO for Step with common fields
 */
@Getter
public abstract class BaseStepDTO {
    private final byte stepType;
    private final String stepTypeName;

    protected BaseStepDTO(byte stepType, String stepTypeName) {
        this.stepType = stepType;
        this.stepTypeName = stepTypeName;
    }
}
