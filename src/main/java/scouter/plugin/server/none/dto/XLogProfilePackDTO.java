package scouter.plugin.server.none.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

/**
 * DTO for XLogProfilePack with Lombok Builder
 */
@Getter
@Builder
public class XLogProfilePackDTO {
    private long time;
    private int objHash;
    private int service;
    private long txid;
    private int elapsed;
    private List profileSteps; // List of StepDTO
}
