package org.openstreetmap.josm.plugins.nl_bgt.io;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.FutureTask;

import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.layer.MainLayerManager;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.nl_bgt.jts.Boundary;

// TODO decide upon and document Class lifecycle
public class MultiFeatureDownloader {
    private final static MainLayerManager layerManager = MainApplication.getLayerManager();
    private static String layerName = "NL_BGT";
    List<FeatureDownloader<?>> downloaders = new ArrayList<>();
    private boolean cancelled = false;
    
    public MultiFeatureDownloader() {
        downloaders.add(new WaterdeelDownloader());
        downloaders.add(new WegdeelDownloader());
        downloaders.add(new BegroeidTerreindeelDownloader());
        downloaders.add(new OnbegroeidterreindeelDownloader());
        downloaders.add(new OndersteunendwegdeelDownloader());
        downloaders.add(new OndersteunendwaterdeelDownloader());
    }

    public void run(Boundary boundary) {
        final List<FutureTask<TaskStatus>> fetchTasks = new LinkedList<>();
        OsmDataLayer dataLayer = getOsmDataLayer();
        downloaders.forEach(featureDownloader -> {
            fetchTasks.add(featureDownloader.getFetchTask(boundary, dataLayer.getDataSet()));
        });
        var TaskStatus = TaskRunner.runTasks(fetchTasks);
        layerManager.setActiveLayer(dataLayer);
    }
    
    private static OsmDataLayer getOsmDataLayer() {
        return layerManager.getLayers().stream()
            .filter(layer -> layer.getName().equals(layerName)) 
            .filter(layer -> layer instanceof OsmDataLayer)
            .map(OsmDataLayer.class::cast)
            .findFirst()
            .orElseGet(() -> {
                var dataLayer = new OsmDataLayer(new DataSet(), layerName, null);
                layerManager.addLayer(dataLayer);
                return dataLayer;
            });
    }
    
    public void cancel() {
        this.cancelled = true;
    }
}
