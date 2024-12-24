package org.openstreetmap.josm.plugins.netex.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RouteMatch {
    private Long osmRouteId;
    private String osmRouteName;
    private String netexRouteId;
    private String netexLineName;
    private String netexDirection;
    private Integer quayCountDifference;
    private Boolean startAreaMatch;
    private Boolean endAreaMatch;
    
    // Get the match score. The lower the score, the better the match;
    public int getScore() {
        int score = quayCountDifference + 2;
        if (Boolean.TRUE.equals(startAreaMatch)) score--;
        if (Boolean.TRUE.equals(endAreaMatch)) score--;
        return score;
    }
}
