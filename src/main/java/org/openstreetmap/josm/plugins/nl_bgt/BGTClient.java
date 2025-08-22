package org.openstreetmap.josm.plugins.nl_bgt;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import nl.pdok.ogc.bgt.ApiClient;
import nl.pdok.ogc.bgt.ApiException;
import nl.pdok.ogc.bgt.api.FeaturesApi;
import nl.pdok.ogc.bgt.model.FeatureCollectionGeoJSONBegroeidterreindeel;
import nl.pdok.ogc.bgt.model.FeatureCollectionGeoJSONOnbegroeidterreindeel;
import nl.pdok.ogc.bgt.model.FeatureCollectionGeoJSONOndersteunendwaterdeel;
import nl.pdok.ogc.bgt.model.FeatureCollectionGeoJSONOndersteunendwegdeel;
import nl.pdok.ogc.bgt.model.FeatureCollectionGeoJSONWaterdeel;
import nl.pdok.ogc.bgt.model.FeatureCollectionGeoJSONWegdeel;

public class BGTClient {
    private URI crs;
    private URI bboxCrs;
    private final ApiClient apiClient = new ApiClient();

    public BGTClient() {
        super();
        try {
            crs = new URI("http://www.opengis.net/def/crs/OGC/1.3/CRS84");
            bboxCrs = new URI("http://www.opengis.net/def/crs/EPSG/0/28992");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        apiClient.setBasePath("https://api.pdok.nl/lv/bgt/ogc/v1");
    }
    
    public FeatureCollectionGeoJSONWaterdeel getWaterdeel(List<BigDecimal> bbox) throws ApiException {
        var api = new FeaturesApi(apiClient);
        return api.waterdeelGetFeatures("json", 1000, crs, bbox, crs, null, null, null, null);
    }
    
    public FeatureCollectionGeoJSONWegdeel getWegdeel(List<BigDecimal> bbox) throws ApiException {
        var api = new FeaturesApi(apiClient);
        return api.wegdeelGetFeatures("json", 1000, crs, bbox, crs, null, null, null, null);
    }
    
    public FeatureCollectionGeoJSONBegroeidterreindeel getBegroeidterreindeel(List<BigDecimal> bbox) throws ApiException {
        var api = new FeaturesApi(apiClient);
        return api.begroeidterreindeelGetFeatures("json", 1000, crs, bbox, crs, null, null, null, null);
    }
    
    public FeatureCollectionGeoJSONOnbegroeidterreindeel getOnbegroeidterreindeel(List<BigDecimal> bbox) throws ApiException {
        var api = new FeaturesApi(apiClient);
        return api.onbegroeidterreindeelGetFeatures("json", 1000, crs, bbox, crs, null, null, null, null);
    }

    public FeatureCollectionGeoJSONOndersteunendwaterdeel getOndersteunendwaterdeel(List<BigDecimal> bbox) throws ApiException {
        var api = new FeaturesApi(apiClient);
        return api.ondersteunendwaterdeelGetFeatures("json", 1000, crs, bbox, crs, null, null, null, null);
    }

    public FeatureCollectionGeoJSONOndersteunendwegdeel getOndersteunendwegdeel(List<BigDecimal> bbox) throws ApiException {
        var api = new FeaturesApi(apiClient);
        return api.ondersteunendwegdeelGetFeatures("json", 1000, crs, bbox, crs, null, null, null, null);
    }
}
