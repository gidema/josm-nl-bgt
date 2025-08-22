package org.openstreetmap.josm.plugins.nl_bgt.features;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.tools.Logging;

public class FeatureTagBuilder {
    private final String feature;
    private final Map<List<String>, Map<String,String>> tagMaps;
    
    public FeatureTagBuilder(String feature, Map<List<String>, Map<String, String>> tagMaps) {
        super();
        this.feature = feature;
        this.tagMaps = tagMaps;
    }

    public void buildTags(OsmPrimitive primitive, String functie, String fysiekVoorkomen) {
        var selector = Arrays.asList(new String[] {functie, fysiekVoorkomen});
        var tagMap = tagMaps.get(selector);
        if (tagMap != null) {
            tagMap.forEach(primitive::put);
        }
        else {
            if (fysiekVoorkomen == null) {
                Logging.warn("Onbekende functie '{0}' voor feature {1}" , functie, feature);
            }
            else if (functie == null) {
                Logging.warn("Onbekende fysiek voorkomen '{0}' voor feature {1}" , fysiekVoorkomen, feature);
            }
            else {
                Logging.warn("Onbekende combinatie van functie '{0}' en fysiek voorkomen '{1}' voor feature {2}" , functie, fysiekVoorkomen, feature);
            }
        }
    }
}
