package org.openstreetmap.josm.plugins.nl_bgt.io;

import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.plugins.nl_bgt.BGTClient;
import org.openstreetmap.josm.plugins.nl_bgt.data.PrimitiveFactory;

import nl.pdok.ogc.bgt.ApiException;
import nl.pdok.ogc.bgt.model.FeatureGeoJSONWegdeel;

public class WegdeelDownloader extends AbstractFeatureDownloader<FeatureGeoJSONWegdeel> {

    public WegdeelDownloader() {
        super(FeatureGeoJSONWegdeel.class);
    }

    @Override
    public TaskStatus call() {
        var client = new BGTClient();
        var bbox = getBoundary().toBigDecimalList();
        try {
            var features = client.getWegdeel(bbox);
            features.getFeatures().forEach(feature -> {
                if (getFeatureIdCache().add(feature.getProperties().getLokaalId())) {
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
    public void addToOsm(FeatureGeoJSONWegdeel feature) {
        // TODO filter obsolete features with the feature request. Not here. 
        if (feature.getProperties().getEindRegistratie() != null) return;
        var geometry = feature.getGeometry().getActualInstance();
        var osmPrimitive = PrimitiveFactory.createPrimitive(geometry, getDataSet());
        osmPrimitive.put("source", "NL:BGT");
        osmPrimitive.put("ref:NL_BGT", feature.getProperties().getLokaalId());
        var functie = feature.getProperties().getFunctie();
        getTagBuilder().buildTags(osmPrimitive, functie, null);
    }
}
