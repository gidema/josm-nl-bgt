package org.openstreetmap.josm.plugins.netex.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode()
public class ChbQuay {
    @EqualsAndHashCode.Include
    private String quayCode;
    private String quayName;
    private String stopSideCode;
    private String areaCode;
    private String quayStatus;
    private String quayType;
    private String areaType;
    private Integer bearing;
}
