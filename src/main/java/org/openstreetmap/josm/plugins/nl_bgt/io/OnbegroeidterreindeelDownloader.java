package org.openstreetmap.josm.plugins.nl_bgt.io;

import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.plugins.nl_bgt.BGTClient;
import org.openstreetmap.josm.plugins.nl_bgt.data.PrimitiveFactory;

import nl.pdok.ogc.bgt.ApiException;
import nl.pdok.ogc.bgt.model.FeatureGeoJSONOnbegroeidterreindeel;

public class OnbegroeidterreindeelDownloader extends AbstractFeatureDownloader<FeatureGeoJSONOnbegroeidterreindeel> {
    
    public OnbegroeidterreindeelDownloader() {
        super(FeatureGeoJSONOnbegroeidterreindeel.class);
    }

    @Override
    public TaskStatus call() {
        var client = new BGTClient();
        var bbox = getBoundary().toBigDecimalList();
        try {
            var features = client.getOnbegroeidterreindeel(bbox);
            features.getFeatures().forEach(feature -> {
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
    public void addToOsm(FeatureGeoJSONOnbegroeidterreindeel feature) {
        var geometry = feature.getGeometry().getActualInstance();
        var osmPrimitive = PrimitiveFactory.createPrimitive(geometry, getDataSet());
        osmPrimitive.put("source", "NL:BGT");
        osmPrimitive.put("ref:NL_BGT", feature.getProperties().getLokaalId());
        var fysiekVoorkomen = feature.getProperties().getFysiekVoorkomen();
        getTagBuilder().buildTags(osmPrimitive, null, fysiekVoorkomen);
    }
}
