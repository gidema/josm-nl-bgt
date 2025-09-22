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
    private final DataSet dataSet;
    private final Map<LatLon, Node> nodeCache = new HashMap<>();
    
    public PrimitiveFactory(DataSet dataSet) {
        super();
        this.dataSet = dataSet;
    }

    public OsmPrimitive createPrimitive(Object geometry, boolean keepHoles) {
        OsmPrimitive mainPrimitive = null;
        if (geometry instanceof PolygonGeoJSON) {
            var coordinates = ((PolygonGeoJSON)geometry).getCoordinates();
            if (coordinates.size() > 1) {
                mainPrimitive = createArea(coordinates, keepHoles);
            }
            else {
                mainPrimitive = createArea(coordinates.get(0));
            }
        }
        else if (geometry instanceof MultipolygonGeoJSON) {
            var coordinates = ((MultipolygonGeoJSON)geometry).getCoordinates();
            mainPrimitive = createMultiPolygonArea(coordinates, keepHoles);
        }
        if (mainPrimitive != null) {
            if (mainPrimitive.getType().equals(OsmPrimitiveType.RELATION)) {
                dataSet.addPrimitive(mainPrimitive);
            }
        }
        return mainPrimitive;
    }

    private OsmPrimitive createMultiPolygonArea(List<List<List<List<BigDecimal>>>> coordinates, boolean keepHoles) {
        var relation = new Relation();
        relation.put("type", "multipolygon");
        coordinates.forEach(polygonCoords -> {
            addPolygon(relation, polygonCoords, keepHoles);
        });
        return relation;
    }

    private OsmPrimitive createArea(List<List<List<BigDecimal>>> coordinates, boolean keepHoles) {
        if (keepHoles) {
            var relation = new Relation();
            relation.put("type", "multipolygon");
            addPolygon(relation, coordinates, keepHoles);
            return relation;
        }
        return createArea(coordinates.get(0));
    }

    private void addPolygon(Relation relation, List<List<List<BigDecimal>>> coordinates, boolean keepHoles) {
        var it = coordinates.iterator();
        var outerRing = new RelationMember("outer", createLinearRing(it.next()));
        relation.addMember(outerRing);
        if (keepHoles)
        while (it.hasNext()) {
            var innerRing = new RelationMember("inner", createLinearRing(it.next()));
            relation.addMember(innerRing);
        }
    }

    private OsmPrimitive createArea(List<List<BigDecimal>> coordinates) {
        return createLinearRing(coordinates);
    }
    
    private Way createLinearRing(List<List<BigDecimal>> coordinates) {
        List<Node> nodes = new ArrayList<>(coordinates.size());
        Node previousNode= null;
        for (List<BigDecimal> coord : coordinates) {
            var node = createNode(coord);
            if (!node.equals(previousNode)) {
                nodes.add(createNode(coord));
            }
            previousNode = node;
        }
        var way = new Way();
        way.setNodes(nodes);
        dataSet.addPrimitive(way);
        return way;
    }
    
    private Node createNode(List<BigDecimal> coords) {
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
