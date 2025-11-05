package scouter.plugin.server.none.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO for ObjectPack with Lombok Builder
 */
@Getter
@Builder
public class ObjectPackDTO {
    private String objType;
    private int objHash;
    private String objName;
    private String address;
    private String version;
    private boolean alive;
    private long wakeup;
    private String tags; // JSON string representation
}
