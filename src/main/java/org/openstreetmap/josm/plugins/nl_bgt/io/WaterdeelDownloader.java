package org.openstreetmap.josm.plugins.nl_bgt.io;

import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.plugins.nl_bgt.BGTClient;
import org.openstreetmap.josm.plugins.nl_bgt.data.PrimitiveFactory;

import nl.pdok.ogc.bgt.ApiException;
import nl.pdok.ogc.bgt.model.FeatureGeoJSONWaterdeel;

public class WaterdeelDownloader extends AbstractFeatureDownloader<FeatureGeoJSONWaterdeel> {

    public WaterdeelDownloader() {
        super(FeatureGeoJSONWaterdeel.class);
    }

    @Override
    public TaskStatus call() {
        var client = new BGTClient();
        var bbox = getBoundary().toBigDecimalList();
        try {
            var features = client.getWaterdeel(bbox);
            features.getFeatures().forEach(feature -> {
                addToOsm(feature);
            });
            MainApplication.getMainPanel().repaint();
        } catch (ApiException e) {
            return TaskStatus.exception(e);
        }
        return TaskStatus.ok;
    }

    @Override
    public void addToOsm(FeatureGeoJSONWaterdeel feature) {
        // TODO filter obsolete features with the feature request. Not here. 
        if (feature.getProperties().getEindRegistratie() != null) return;
        var geometry = feature.getGeometry().getActualInstance();
        var osmPrimitive = PrimitiveFactory.createPrimitive(geometry, getDataSet());
        osmPrimitive.put("source", "NL:BGT");
        osmPrimitive.put("ref:NL_BGT", feature.getProperties().getLokaalId());
        var functie = feature.getProperties().getType();
        getTagBuilder().buildTags(osmPrimitive, functie, null);
    }
}
