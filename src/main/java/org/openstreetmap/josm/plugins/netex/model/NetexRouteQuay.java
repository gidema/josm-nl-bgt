package org.openstreetmap.josm.plugins.netex.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NetexRouteQuay {
    String routeId;
    Long quayIndex;
    String lineNumber;
    String quayCode;
    String quayName;
    String stopSideCode;
    String stopplaceCode;
    String quayLocationType;
    
    @Override
    public String toString() {
        var sb = new StringBuilder();
        sb.append(quayCode).append(" - ").append(quayName);
        if (stopSideCode != null) {
            sb.append(" (").append(stopSideCode).append(")");
        }
        return sb.toString();
    }
}
