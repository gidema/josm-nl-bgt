package org.openstreetmap.josm.plugins.netex.check;

import java.util.List;

import org.openstreetmap.josm.plugins.netex.model.DimNetexRoute;
import org.openstreetmap.josm.plugins.netex.model.OsmPtRoute;
import org.openstreetmap.josm.tools.I18n;

public class RouteCheckResults {
    private final OsmPtRoute osmRoute;
    private final DimNetexRoute netexRoute;
    private final List<RouteIssue> matchIssues;
    private int quayCountDifference;
    
    public RouteCheckResults(OsmPtRoute osmRoute, DimNetexRoute netexRoute, List<RouteIssue> matchIssues, int quayCountDifference) {
        super();
        this.osmRoute = osmRoute;
        this.netexRoute = netexRoute;
        this.matchIssues = matchIssues;
        this.quayCountDifference = quayCountDifference;
    }

    public OsmPtRoute getOsmRoute() {
        return osmRoute;
    }

    public DimNetexRoute getNetexRoute() {
        return netexRoute;
    }

    public List<RouteIssue> getMatchIssues() {
        return matchIssues;
    }

//    public int getIssueCount() {
//        return getMatchIssues().size();
//    }
//    
    /**
     * Get a score which is an indication of how similar the compared routes are.
     * Lower scores are better, with 0 indicating a perfect match
     * 
     * @return
     */
    public int getScore() {
        return quayCountDifference + getMatchIssues().size();
    }
    public String getIssueReport() {
        var sb = new StringBuilder();
        sb.append(I18n.tr("Checking OSM route {0} {1} (line {2}).\n\n", 
            osmRoute.getPrimitiveId(),
            osmRoute.getName(), 
            osmRoute.getRef()));
        if (matchIssues.size() == 0) {
            sb.append(I18n.tr("No issues were found for this route"));
        }
        else {
            matchIssues.forEach(issue -> sb.append(issue.getMessage()).append("\n"));
        }
        return sb.toString();
    }


}
