package scouter.plugin.server.none.dto.step;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO for SqlStep with Lombok Builder
 */
@Getter
@Builder
public class SqlStepDTO {
    private byte stepType;
    private String stepTypeName;
    private int hash;
    private int elapsed;
    private int cputime;
    private String param;
    private int error;
}
