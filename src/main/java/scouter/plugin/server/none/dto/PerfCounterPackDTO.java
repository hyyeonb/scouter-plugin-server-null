package scouter.plugin.server.none.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO for PerfCounterPack with Lombok Builder
 */
@Getter
@Builder
public class PerfCounterPackDTO {
    private long time;
    private String objName;
    private byte timetype;
    private String data; // JSON string representation of MapValue
}
