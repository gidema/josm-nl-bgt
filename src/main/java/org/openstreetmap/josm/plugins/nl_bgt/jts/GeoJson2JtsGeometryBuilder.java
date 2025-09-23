package org.openstreetmap.josm.plugins.nl_bgt.jts;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPolygon;
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
import org.openstreetmap.josm.plugins.nl_bgt.data.BgtGeometryHandler;
import org.openstreetmap.josm.shared.nl_ogc.data.GeometryHandler;

public class GeoJson2JtsGeometryBuilder {
    private GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 28992);
    private final GeometryHandler geometryHandler = new BgtGeometryHandler();
    {
        assert ProjectionRegistry.getProjection().toCode().equals("EPSG:28992");
    }

    public Geometry buildArea(Object geometry, boolean keepHoles) {
        switch (geometry.getClass().getSimpleName()) {
        case "PolygonGeoJSON":
           return buildPolygon(geometry, keepHoles);
        case "MultipolygonGeoJSON":
            return buildMultiPolygon(geometry, keepHoles);
        default:
            return null;
        }
    }

    private MultiPolygon buildMultiPolygon(Object geometry, boolean keepHoles) {
        var coordinates = geometryHandler.getMultipolygonCoordinates(geometry);
        var polygons = new Polygon[coordinates.size()];
        var i = 0;
        for (var polygonCoords : coordinates) {
            polygons[i++] = buildPolygon(polygonCoords, keepHoles);
        }
        return geometryFactory.createMultiPolygon(polygons);
    }

    private Polygon buildPolygon(Object geometry, boolean keepHoles) {
        var coordinates = geometryHandler.getPolygonCoordinates(geometry);
        if (coordinates.size() > 1 && keepHoles) {
            return createPolygonWithHoles(coordinates);
        }
        return createPolygon(coordinates.get(0));
    }

    private Polygon createPolygonWithHoles(List<List<List<BigDecimal>>> coordinates) {
        var it = coordinates.iterator();
        var shell = createLinearRing(it.next());
        List<LinearRing> holes = new ArrayList<>(coordinates.size() - 1);
        it.forEachRemaining(coords -> {
            holes.add(createLinearRing(coords));
        });
        return geometryFactory.createPolygon(shell, holes.toArray(new LinearRing[holes.size()]));
    }

    private Polygon createPolygon(List<List<BigDecimal>> coordinates) {
        return geometryFactory.createPolygon(createLinearRing(coordinates));
    }

    private LinearRing createLinearRing(List<List<BigDecimal>> coordinates) {
        Coordinate[] jtsCoords = new Coordinate[coordinates.size()];
        Coordinate previousCoord = null;
        int i = 0;
        for (List<BigDecimal> coord : coordinates) {
            var jtsCoord = createCoordinate(coord);
            if (!jtsCoord.equals(previousCoord)) {
                jtsCoords[i++] = jtsCoord;
            }
            previousCoord = jtsCoord;
        }
        return geometryFactory.createLinearRing(jtsCoords);
    }

    private static Coordinate createCoordinate(List<BigDecimal> coord) {
        return new Coordinate(coord.get(0).doubleValue(), coord.get(1).doubleValue());
    }
}
