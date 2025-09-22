package org.openstreetmap.josm.plugins.nl_bgt.io;

import java.util.List;

import org.openstreetmap.josm.plugins.nl_bgt.data.BgtGeometryHandler;
import org.openstreetmap.josm.shared.nl_ogc.data.OgcLayerManager;
import org.openstreetmap.josm.shared.nl_ogc.io.FeatureDownloader;
import org.openstreetmap.josm.shared.nl_ogc.io.MultiFeatureDownloader;

public class BgtMultiFeatureDownloader extends MultiFeatureDownloader {
    private static OgcLayerManager layerManager = new OgcLayerManager("NL_BGT", new BgtGeometryHandler());
    private static List<FeatureDownloader<?>> downloaders = List.of(
       new WaterdeelDownloader(layerManager),
       new WegdeelDownloader(layerManager),
       new BegroeidTerreindeelDownloader(layerManager),
       new OnbegroeidterreindeelDownloader(layerManager),
       new OndersteunendwegdeelDownloader(layerManager),
       new OndersteunendwaterdeelDownloader(layerManager));
    private boolean cancelled = false;
    
    public BgtMultiFeatureDownloader() {
        super(layerManager, downloaders);
    }
}
