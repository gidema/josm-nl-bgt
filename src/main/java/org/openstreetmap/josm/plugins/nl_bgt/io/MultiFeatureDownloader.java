package org.openstreetmap.josm.plugins.nl_bgt.io;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.FutureTask;

import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.osm.visitor.BoundingXYVisitor;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerAddEvent;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerChangeListener;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerOrderChangeEvent;
import org.openstreetmap.josm.gui.layer.LayerManager.LayerRemoveEvent;
import org.openstreetmap.josm.plugins.nl_bgt.jts.Boundary;
import org.openstreetmap.josm.tools.Logging;

// TODO decide upon and document Class lifecycle
public class MultiFeatureDownloader implements LayerChangeListener {
    private OgcLayerManager layerManager = new OgcLayerManager();
    List<FeatureDownloader<?>> downloaders = new ArrayList<>();
    private boolean cancelled = false;
    
    public MultiFeatureDownloader() {
        MainApplication.getLayerManager().addLayerChangeListener(this);
        downloaders.add(new WaterdeelDownloader(layerManager));
        downloaders.add(new WegdeelDownloader(layerManager));
        downloaders.add(new BegroeidTerreindeelDownloader(layerManager));
        downloaders.add(new OnbegroeidterreindeelDownloader(layerManager));
        downloaders.add(new OndersteunendwegdeelDownloader(layerManager));
        downloaders.add(new OndersteunendwaterdeelDownloader(layerManager));
    }

    public void run(Boundary boundary) {
        final List<FutureTask<TaskStatus>> fetchTasks = new LinkedList<>();
        Logging.info("Downloading {0} sets of BGT data with boundary {1}", downloaders.size(), boundary);
        downloaders.forEach(featureDownloader -> {
            fetchTasks.add(featureDownloader.getFetchTask(boundary));
        });
        var taskStatus = TaskRunner.runTasks(fetchTasks);
        if (taskStatus.hasExceptions()) {
            Logging.error(taskStatus.getExceptions().toString());
        }
        if (taskStatus.hasErrors()) {
            Logging.error(taskStatus.getErrors().toString());
        }
        MainApplication.getLayerManager().setActiveLayer(layerManager.getDataLayer());
        computeBboxAndCenterScale(boundary.getBounds());
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

    @Override
    public void layerAdded(LayerAddEvent e) {
        // Ignore this event
    }

    @Override
    public void layerRemoving(LayerRemoveEvent e) {
        layerManager.reset();
    }

    @Override
    public void layerOrderChanged(LayerOrderChangeEvent e) {
        // Ignore this event
    }

    
}
