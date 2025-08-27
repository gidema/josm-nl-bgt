package org.openstreetmap.josm.plugins.nl_bgt.features;

import java.util.Map;

import org.openstreetmap.josm.data.osm.OsmPrimitive;

public class FeatureTagBuilder {
    private final String feature;
    private final Map<BgtFeatureTags, Map<String,String>> tagMaps;
    
    public FeatureTagBuilder(String feature, Map<BgtFeatureTags, Map<String, String>> tagMaps) {
        super();
        this.feature = feature;
        this.tagMaps = tagMaps;
    }

    public void buildTags(OsmPrimitive primitive, BgtFeatureTags featureTags) {
        var tagMap = tagMaps.get(featureTags);
        if (tagMap != null) {
            tagMap.forEach(primitive::put);
        }
        else {
            // TODO Report unknown feature tags
            int i=0;
        }
    }
}
