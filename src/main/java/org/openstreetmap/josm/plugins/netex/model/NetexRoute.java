package org.openstreetmap.josm.plugins.netex.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NetexRoute {
    private String id;
    private String name;
    private String line_ref;
    private String direction_type;
}
