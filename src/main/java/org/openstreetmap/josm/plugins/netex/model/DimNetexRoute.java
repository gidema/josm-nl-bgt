package org.openstreetmap.josm.plugins.netex.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode()
public class DimNetexRoute {
    @EqualsAndHashCode.Include
    private String id;
    private String name;
    private String brandingRef;
    private String directionType;
    private String transportMode;
    private String publicCode;
    private String privateCode;
    private String colour;
    private String textColour;
    private boolean mobilityImpairedAccess;
}
