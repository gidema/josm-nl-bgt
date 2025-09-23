package org.openstreetmap.josm.plugins.nl_bgt.jts;

import java.util.LinkedList;
import java.util.List;

import org.locationtech.jts.coverage.CoverageSimplifier;
import org.locationtech.jts.geom.Geometry;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.shared.nl_ogc.data.OgcLayerManager;

public class GeometrySimplifier implements Runnable {
    private final Osm2JtsGeometryBuilder geoBuilder = new Osm2JtsGeometryBuilder();
    private final OgcLayerManager layerManager;
    
    
    public GeometrySimplifier(OgcLayerManager layerManager) {
        super();
        this.layerManager = layerManager;
    }

    @Override
    public void run() {
        List<Geometry> geometries = new LinkedList<>(); 
        layerManager.getDataSet().getPrimitives(OsmPrimitive::isTagged).forEach(primitive -> {
            geometries.add(geoBuilder.buildArea(primitive));
        });
        CoverageSimplifier simplifier = new CoverageSimplifier(geometries.toArray(new Geometry[0]));
        Geometry[] simplified = simplifier.simplify(0.075, 0);
        var i = 0;
    }

}
