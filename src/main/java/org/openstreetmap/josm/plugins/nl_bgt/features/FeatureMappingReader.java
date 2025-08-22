package org.openstreetmap.josm.plugins.nl_bgt.features;

import java.io.FileInputStream;
import java.io.IOException;

import org.csv4pojoparser.util.impl.CSVReaderImpl;

public class FeatureMappingReader {
    public static void main(String... args) throws IOException {
        FeatureMappingReader.read();
    }
    
    public static void read() throws IOException {
        String file = "/home/gertjan/temp/bgt_tags.csv";
        try (var is = new FileInputStream(file)) {
            var list = new CSVReaderImpl().createPojoStreamFromCSVInputStream(FeatureDto.class, is)
            .toList();
            list.size();
        }
    }
}
