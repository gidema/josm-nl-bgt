package org.openstreetmap.josm.plugins.netex;

import java.util.HashMap;
import java.util.Map;

import org.openstreetmap.josm.plugins.netex.model.DimNetexRoute;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

public class NetexRouteService {
    private final static NetexRouteService INSTANCE = new NetexRouteService();
    
    private final RestClient restClient = RestClient.create();
    private final Map<String, DimNetexRoute> routeCache = new HashMap<>();
    
    public static NetexRouteService getInstance() {
        return INSTANCE;
    }
    
    private NetexRouteService() {
        // Empty private constructor for singleton class
    }
    
    public DimNetexRoute getRoute(String routeId) {
        DimNetexRoute route = routeCache.get(routeId);
        if (route == null) {
            route = restClient.get()
                .uri("http://localhost:8080/netex/route/{routeId}", routeId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {/** */});
            routeCache.put(route.getId(), route);
        }
        return route;
    }
}
