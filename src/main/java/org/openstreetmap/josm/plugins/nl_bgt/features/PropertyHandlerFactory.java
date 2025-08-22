package org.openstreetmap.josm.plugins.nl_bgt.features;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class PropertyHandlerFactory {
    private final static MethodHandles.Lookup publicLookup = MethodHandles.publicLookup();
    
    public PropertyHandlerFactory() {
        super();
    }

    public static StringPropertyGetter createPropertiesHandler(String package_, String feature, String property) {
        try {
            String className = package_ + ".FeatureGeoJSON" + capitalize(feature);
            Class<?> featureClass = Class.forName(className);
            className = package_ + ".PropertiesGeoJSON" + capitalize(feature);
            Class<?> propertiesClass = Class.forName(className);
            MethodType methodType = MethodType.methodType(propertiesClass);
            MethodHandle getPropertiesHandle = publicLookup.findVirtual(featureClass, "get" +capitalize("properties"), methodType);
            methodType = MethodType.methodType(String.class);
            MethodHandle getPropertyHandle = publicLookup.findVirtual(propertiesClass, "get" + capitalize(property), methodType);
            return new StringPropertyGetter(getPropertiesHandle, getPropertyHandle);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String capitalize(String name) {
        return name.substring(0,1).toUpperCase() + name.substring(1);
    }
}
