package org.openstreetmap.josm.plugins.netex.model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.RelationMember;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.dialogs.relation.DownloadRelationMemberTask;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OsmPtRoute {
    private Long primitiveId;
    private List<OsmPtQuay> quays;
    private String transportMode;
    private String name;
    private String operator;
    private String ref;
    private String network;
    private String from;
    private String to;
    private String colour;

    public static OsmPtRoute parse(Relation r) {
        if (r.isIncomplete()) {
            Collection<OsmPrimitive> missingChildren = r.getMemberPrimitives().stream().filter(p->p.isIncomplete()).collect(Collectors.toList());
            var task = new DownloadRelationMemberTask(r, missingChildren, MainApplication.getLayerManager().getActiveDataLayer());
//            task.
        }
        var route = new OsmPtRoute();
        route.setPrimitiveId(r.getId());
        route.setTransportMode(r.get("type"));
        route.setName(r.get("name"));
        route.setOperator(r.get("operator"));
        route.setRef(r.get("ref"));
        route.setNetwork(r.get("network"));
        route.setFrom(r.get("from"));
        route.setTo(r.get("to"));
        route.setColour(r.get("colour"));
        route.setQuays(parseQuays(r.getMembers()));
        return route;
    }

    private static List<OsmPtQuay> parseQuays(List<RelationMember> members) {
        OsmPtQuay lastQuay = null; 
        var quayList = new LinkedList<OsmPtQuay>();
        for (var member : members) {
            if (member.getRole().startsWith("platform")) {
                var platform = OsmPtPlatform.parse(member.getMember());
                if (lastQuay == null) {
                    lastQuay = new OsmPtQuay(platform);
                    quayList.add(lastQuay);
                    continue;
                }
                if (platform.getRefIfopt() == null) {
                    if (Objects.equals(lastQuay.getName(), platform.getName())) {
                        lastQuay.addPlatform(platform);
                        continue;
                    }
                    lastQuay = new OsmPtQuay(platform);
                    quayList.add(lastQuay);
                    continue;
                }
                if (Objects.equals(lastQuay.getQuayCode(), platform.getRefIfopt())) {
                    lastQuay.addPlatform(platform);
                    continue;
                }
                lastQuay = new OsmPtQuay(platform);
                quayList.add(lastQuay);
            }
        }
        return quayList;
    }
}