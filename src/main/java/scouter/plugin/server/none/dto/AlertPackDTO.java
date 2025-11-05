package scouter.plugin.server.none.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO for AlertPack with Lombok Builder
 */
@Getter
@Builder
public class AlertPackDTO {
    private long time;
    private String objType;
    private int objHash;
    private byte level;
    private String title;
    private String message;
    private String tags;
}
