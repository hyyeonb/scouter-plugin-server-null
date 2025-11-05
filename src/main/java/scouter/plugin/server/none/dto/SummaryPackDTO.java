package scouter.plugin.server.none.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO for SummaryPack with Lombok Builder
 */
@Getter
@Builder
public class SummaryPackDTO {
    private long time;
    private int objHash;
    private String objType;
    private byte stype;
    private String table; // JSON string representation
}
