package org.openstreetmap.josm.plugins.netex;

import java.util.Comparator;
import java.util.stream.Collectors;

import org.openstreetmap.josm.plugins.netex.check.RouteCheckResults;
import org.openstreetmap.josm.plugins.netex.check.RouteChecker;
import org.openstreetmap.josm.plugins.netex.model.OsmPtRoute;
import org.openstreetmap.josm.plugins.netex.model.RouteMatch;
import org.openstreetmap.josm.tools.I18n;

public class RouteMatcher {
    final static ChbQuayService chbQuayService = ChbQuayService.getInstance();

    private final NetexRestClient restClient = new NetexRestClient();
    private final OsmPtRoute osmRoute;

    private RouteCheckResults bestResult;

    public RouteMatcher(OsmPtRoute osmRoute) {
        this.osmRoute = osmRoute;
    }

    public void run() {
        var matchingRoutes = restClient.getRouteMatches(osmRoute.getPrimitiveId());
        if (!matchingRoutes.isEmpty()) {
            var checkResults = matchingRoutes.stream()
                    .map(this::checkRoute)
                    .sorted(new Comparator<RouteCheckResults>() {

                        @Override
                        public int compare(RouteCheckResults r1, RouteCheckResults r2) {
                            // TODO Auto-generated method stub
                            return Integer.compare(r1.getScore(), r2.getScore());
                        }
                    })
                    .collect(Collectors.toList());
            bestResult = checkResults.get(0);
        }
    }
    
    public String getIssueReport() {
        if (bestResult == null) {
            return I18n.tr("No matching Netex route was found for this OSM route.");
        }
        return bestResult.getIssueReport();
    }

    private RouteCheckResults checkRoute(RouteMatch routeMatch) {
        var netexRoute = restClient.getRoute(routeMatch.getNetexRouteId());
        var routeChecker = new RouteChecker(osmRoute, netexRoute);
        return routeChecker.run();
    }
}
