package org.openstreetmap.josm.plugins.nl_bgt.data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.RelationMember;
import org.openstreetmap.josm.data.osm.Way;

import nl.pdok.ogc.bgt.model.MultipolygonGeoJSON;
import nl.pdok.ogc.bgt.model.PolygonGeoJSON;

public class PrimitiveFactory {
    private static Map<LatLon, Node> nodeCache = new HashMap<>();
    
    public static OsmPrimitive createPrimitive(Object geometry, DataSet dataSet) {
        OsmPrimitive mainPrimitive = null;
        if (geometry instanceof PolygonGeoJSON) {
            var coordinates = ((PolygonGeoJSON)geometry).getCoordinates();
            if (coordinates.size() > 1) {
                mainPrimitive = createComplexPolygon(coordinates, dataSet);
            }
            else {
                mainPrimitive = createSimplePolygon(coordinates.get(0), dataSet);
            }
        }
        else if (geometry instanceof MultipolygonGeoJSON) {
            var coordinates = ((MultipolygonGeoJSON)geometry).getCoordinates();
            mainPrimitive = createMultiPolygon(coordinates, dataSet);
        }
        if (mainPrimitive != null) {
            if (mainPrimitive.getType().equals(OsmPrimitiveType.RELATION)) {
                dataSet.addPrimitive(mainPrimitive);
            }
        }
        return mainPrimitive;
    }

    private static OsmPrimitive createMultiPolygon(List<List<List<List<BigDecimal>>>> coordinates, DataSet dataSet) {
        var relation = new Relation();
        relation.put("type", "multipolygon");
        coordinates.forEach(polygonCoords -> {
            addPolygon(relation, polygonCoords, dataSet);
        });
        return relation;
    }

    private static OsmPrimitive createComplexPolygon(List<List<List<BigDecimal>>> coordinates, DataSet dataSet) {
        var relation = new Relation();
        relation.put("type", "multipolygon");
        addPolygon(relation, coordinates, dataSet);
        return relation;
    }

    private static void addPolygon(Relation relation, List<List<List<BigDecimal>>> coordinates, DataSet dataSet) {
        var it = coordinates.iterator();
        var outerRing = new RelationMember("outer", createLinearRing(it.next(), dataSet));
        relation.addMember(outerRing);
        while (it.hasNext()) {
            var innerRing = new RelationMember("inner", createLinearRing(it.next(), dataSet));
            relation.addMember(innerRing);
        }
    }

    private static OsmPrimitive createSimplePolygon(List<List<BigDecimal>> coordinates, DataSet dataSet) {
        return createLinearRing(coordinates, dataSet);
    }
    
    private static Way createLinearRing(List<List<BigDecimal>> coordinates, DataSet dataSet) {
        List<Node> nodes = new ArrayList<>(coordinates.size());
        coordinates.forEach(coord -> {
            nodes.add(createNode(coord, dataSet));
        });
        var way = new Way();
        way.setNodes(nodes);
        dataSet.addPrimitive(way);
        return way;
    }
    
    private static Node createNode(List<BigDecimal> coords, DataSet dataSet) {
        BigDecimal b;
        var latLon = new LatLon(coords.get(1).setScale(7, RoundingMode.HALF_UP).doubleValue(),
                coords.get(0).setScale(7, RoundingMode.HALF_UP).doubleValue());
        var node = nodeCache.get(latLon);
        if (node == null) {
            node = new Node(latLon);
            nodeCache.put(latLon, node);
            dataSet.addPrimitive(node);
        }
        return node;
    }
}
