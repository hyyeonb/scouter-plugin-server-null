package scouter.plugin.server.none.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO for TextPack with Lombok Builder
 */
@Getter
@Builder
public class TextPackDTO {
    private String xtype;
    private int hash;
    private String text;
}
