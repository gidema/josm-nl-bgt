package org.openstreetmap.josm.plugins.nl_bgt.io;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.FutureTask;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.visitor.BoundingXYVisitor;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.layer.MainLayerManager;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.nl_bgt.jts.Boundary;
import org.openstreetmap.josm.tools.Logging;

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
        Logging.info("Downloading {0} sets of BGT data with boundary {1}", downloaders.size(), boundary);
        OsmDataLayer dataLayer = getOsmDataLayer();
        downloaders.forEach(featureDownloader -> {
            fetchTasks.add(featureDownloader.getFetchTask(boundary, dataLayer.getDataSet()));
        });
        var taskStatus = TaskRunner.runTasks(fetchTasks);
        if (taskStatus.hasExceptions()) {
            Logging.error(taskStatus.getExceptions().toString());
        }
        if (taskStatus.hasErrors()) {
            Logging.error(taskStatus.getErrors().toString());
        }
        layerManager.setActiveLayer(dataLayer);
        computeBboxAndCenterScale(boundary.getBounds());
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
    
    protected static void computeBboxAndCenterScale(Collection<Bounds> bounds) {
        BoundingXYVisitor v = new BoundingXYVisitor();
        
        if (bounds != null && !bounds.isEmpty()) {
            bounds.forEach(v::visit);
            MainApplication.getMap().mapView.zoomTo(v.getBounds());
        }
    }

}
