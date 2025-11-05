package scouter.plugin.server.none.dto.step;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO for ApiCallStep with Lombok Builder
 */
@Getter
@Builder
public class ApiCallStepDTO {
    private byte stepType;
    private String stepTypeName;
    private long txid;
    private int hash;
    private int elapsed;
    private int cputime;
    private int error;
    private String address;
}
