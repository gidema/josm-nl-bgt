package org.openstreetmap.josm.plugins.nl_bgt.io;

import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.plugins.nl_bgt.BGTClient;
import org.openstreetmap.josm.plugins.nl_bgt.features.BgtFeatureTags;
import org.openstreetmap.josm.shared.nl_ogc.data.OgcLayerManager;
import org.openstreetmap.josm.shared.nl_ogc.io.TaskStatus;

import nl.pdok.ogc.bgt.ApiException;
import nl.pdok.ogc.bgt.model.FeatureGeoJSONScheidingVlak;

public class ScheidingVlakDownloader extends AbstractFeatureDownloader<FeatureGeoJSONScheidingVlak> {

    public ScheidingVlakDownloader(OgcLayerManager layerManager) {
        super(FeatureGeoJSONScheidingVlak.class, layerManager);
    }

    @Override
    public TaskStatus call() {
        var client = new BGTClient();
        var bbox = getBoundary().toBigDecimalList();
        try {
            var features = client.getScheidingVlak(bbox);
            features.getFeatures().forEach(feature -> {
                // TODO filter obsolete features with the feature request. Not here. 
                if (feature.getProperties().getEindRegistratie() == null &&
                        getFeatureIdCache().add(feature.getProperties().getLokaalId())) {
                    addToOsm(feature);
                }
            });
            MainApplication.getMainPanel().repaint();
        } catch (ApiException e) {
            return TaskStatus.exception(e);
        }
        return TaskStatus.ok;
    }

    @Override
    public void addToOsm(FeatureGeoJSONScheidingVlak feature) {
        var geometry = feature.getGeometry().getActualInstance();
        var osmPrimitive = getPrimitiveFactory().createAreaPrimitive(geometry, false);
        osmPrimitive.put("source", "NL:BGT");
        osmPrimitive.put("ref:NL:BGT", feature.getProperties().getLokaalId());
        var type = feature.getProperties().getType();
        var plusType = feature.getProperties().getPlusType();
        getTagBuilder().buildTags(osmPrimitive, new BgtFeatureTags(type, plusType, null, null, null));
    }
}
