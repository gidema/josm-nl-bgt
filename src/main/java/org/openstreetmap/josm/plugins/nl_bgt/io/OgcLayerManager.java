package org.openstreetmap.josm.plugins.nl_bgt.io;

import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.DownloadPolicy;
import org.openstreetmap.josm.data.osm.UploadPolicy;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.layer.MainLayerManager;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.plugins.nl_bgt.data.PrimitiveFactory;

public class OgcLayerManager {
    private final static MainLayerManager layerManager = MainApplication.getLayerManager();
    private static String layerName = "NL_BGT";
    private OsmDataLayer dataLayer;
    private PrimitiveFactory primitiveFactory;
    
    public DataSet getDataSet() {
        return getDataLayer().getDataSet();
    }
    
    public OsmDataLayer getDataLayer() {
        if (dataLayer == null) {
            dataLayer = new OsmDataLayer(new DataSet(), layerName, null);
            layerManager.addLayer(dataLayer);
            dataLayer.setUploadDiscouraged(true);
            dataLayer.getDataSet().setUploadPolicy(UploadPolicy.BLOCKED);
            dataLayer.getDataSet().setDownloadPolicy(DownloadPolicy.BLOCKED);
        }
        return this.dataLayer;
    }
    
    public PrimitiveFactory getPrimitiveFactory() {
        if (primitiveFactory == null) {
            primitiveFactory = new PrimitiveFactory(getDataSet());
        }
        return primitiveFactory;
    }
    
    public void reset() {
        this.dataLayer = null;
        this.primitiveFactory = null;
    }
}
