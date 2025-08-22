package org.openstreetmap.josm.plugins.nl_bgt.features;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.gui.MainApplication;

public class TagScraper {
    private final static Set<String> ignorableTags = Set.of("bgt_tags", "type");
    private Map<String, TagInfo> tagInfoCache = new HashMap<>();
    
    public void run() throws IOException {
        try (
            var writer = new FileWriter("/home/gertjan/temp/bgt_tags.csv");
        ) {
            writer.write("label,key,value\n");
        DataSet dataSet = MainApplication.getLayerManager().getActiveDataSet();
        dataSet.getPrimitives(p -> p.getType() != OsmPrimitiveType.NODE).stream()
            .filter(way -> way.hasKey("bgt_tags"))
            .forEach(way -> {
                var tagInfo = tagInfoCache.get(way.get("bgt_tags"));
                if (tagInfo == null) {
                    var tags = new HashMap<String, String>();
                    way.getKeys().forEach((key, value) -> {
                        if (!ignorableTags.contains(key)) {
                            tags.put(key, value);
                        }
                    });
                    tagInfo = new TagInfo(way.get("bgt_tags"), tags);
                    tagInfoCache.put(tagInfo.getLabel(), tagInfo);
                }
            });
        tagInfoCache.forEach((label, tagInfo) ->{
            tagInfo.getTags().forEach((key, value) -> {
                try {
                    writer.write(String.format("\"%s\",\"%s\",\"%s\"\n", label, key, value));
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            });
        });
        }
    }
    
    class TagInfo {
        private final String label;
        private final Map<String, String> tags;

        public TagInfo(String label, Map<String, String> tags) {
            super();
            this.label = label;
            this.tags = tags;
        }

        public String getLabel() {
            return label;
        }

        public Map<String, String> getTags() {
            return tags;
        }
    }
}
