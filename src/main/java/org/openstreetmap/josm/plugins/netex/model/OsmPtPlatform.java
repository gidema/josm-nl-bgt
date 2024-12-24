package org.openstreetmap.josm.plugins.netex.model;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OsmPtPlatform {
    Long osmPrimitiveId;
    OsmPrimitiveType primitiveType;
    String name;
    Boolean isBus;
    String stopSideCode;
    String refIfopt;
    String note;
    
    @Override
    public String toString() {
        return refIfopt + " (" + name + 
            stopSideCode == null ? "" : " - " + stopSideCode +
            ")";
    }
    
    public static OsmPtPlatform parse(Node node) {
        var platform = parse((OsmPrimitive) node);
        return platform;
    }

    public static OsmPtPlatform parse(OsmPrimitive p) {
        var platform = new OsmPtPlatform();
        platform.setOsmPrimitiveId(p.getId());
        platform.setPrimitiveType(p.getType());
        platform.setName(p.get("name"));
        platform.setIsBus(p.hasTag("bus", "yes"));
        platform.setStopSideCode(p.get("ref"));
        platform.setRefIfopt(p.get("ref:IFOPT"));
        platform.setNote(p.get("note"));
        return platform;
    }
}
