package org.openstreetmap.josm.plugins.netex;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.openstreetmap.josm.plugins.netex.model.DimNetexRoute;
import org.openstreetmap.josm.plugins.netex.model.NetexRouteQuay;
import org.openstreetmap.josm.plugins.netex.model.RouteMatch;
import org.openstreetmap.josm.tools.I18n;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

public class NetexRestClient {
    private final RestClient restClient = RestClient.create();
    private final ChbQuayService quayService = ChbQuayService.getInstance();
    private final NetexRouteService routeService = NetexRouteService.getInstance();

    public DimNetexRoute getRoute(String routeId) {
        return routeService.getRoute(routeId);
    }
            
    public List<NetexRouteQuay> getRouteQuays(String routeId) {
        List<NetexRouteQuay> routeQuays = restClient.get()
            .uri("http://localhost:8080/netex/routequays/{routeId}", routeId)
            .retrieve()
            .body(new ParameterizedTypeReference<>() { /**/ });
        Set<String> quayCodes = routeQuays.stream().map(rq -> rq.getQuayCode()).collect(Collectors.toSet());
        quayService.fetchMissingCodes(quayCodes);
        routeQuays.forEach(routeQuay -> {
            var dimChbQuay = quayService.getQuay(routeQuay.getQuayCode());
            routeQuay.setQuayName(dimChbQuay == null ? I18n.tr("Unknown") : dimChbQuay.getQuayName());
        });
        return routeQuays;
    }

    public List<RouteMatch> getRouteMatches(Long osmRouteId) {
        try {
            return restClient.get().uri("http://localhost:8080/routematches/{osmRouteId}", osmRouteId).retrieve()
                    .body(new ParameterizedTypeReference<>() {
                        //
                    });
        } catch (@SuppressWarnings("unused") ResourceAccessException e) {
            throw new RuntimeException(I18n.tr("The OSM netex server is not available"));
        }
    }
}
