package org.openstreetmap.josm.plugins.nl_bgt.jts;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.data.osm.visitor.paint.relations.Multipolygon;
import org.openstreetmap.josm.data.osm.visitor.paint.relations.Multipolygon.PolyData;
import org.openstreetmap.josm.data.projection.ProjectionRegistry;

public class Osm2JtsGeometryBuilder {
    private GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 28992);
    {
        assert ProjectionRegistry.getProjection().toCode().equals("EPSG:28992");
    }
    
    public Geometry buildArea(OsmPrimitive primitive) {
        Geometry geometry;
        switch (primitive.getType()) {
        case CLOSEDWAY:
        case WAY:
            var way = (Way)primitive;
            geometry = geometryFactory.createPolygon(buildCoordinates(way.getNodes()));
            break;
        case RELATION:
            var mp = new Multipolygon((Relation)primitive);
            var polygons = mp.getCombinedPolygons();
            if (polygons.size() == 1 ) {
                geometry = buildPolygon(polygons.get(0));
            }
            else {
                geometry = buildMultipolygon(polygons);
            }
            break;
        default:
            return null;
        }
        geometry.setUserData(primitive);
        return geometry;
    }
    
    private Polygon buildPolygon(PolyData polyData) {
        if (polyData.getInners() == null || polyData.getInners().isEmpty()) {
            return geometryFactory.createPolygon(buildCoordinates(polyData.getNodes()));
        }
        LinearRing[] inners = new LinearRing[polyData.getInners().size()];
        var i = 0;
        var shell = geometryFactory.createLinearRing(buildCoordinates(polyData.getNodes()));
        for (var inner : polyData.getInners()) {
            inners[i++] = geometryFactory.createLinearRing(buildCoordinates(inner.getNodes()));
        }
        return geometryFactory.createPolygon(shell, inners);
    }

    private Geometry buildMultipolygon(List<PolyData> polygons) {
        var jtsPolygons = new Polygon[polygons.size()];
        int i = 0;
        for (var polyData : polygons) {
            jtsPolygons[i++] = buildPolygon(polyData);
        }
        return geometryFactory.createMultiPolygon(jtsPolygons);
    }

    private static Coordinate[] buildCoordinates(List<Node> nodes) {
        var coordinates = new ArrayList<Coordinate>(nodes.size());
        nodes.forEach(node -> {
            coordinates.add(buildCoordinate(node.getEastNorth()));
        });
        return coordinates.toArray(new Coordinate[0]);
    }

    private static Coordinate buildCoordinate(EastNorth en) {
        return new Coordinate(en.getX(), en.getY());
    }
}
