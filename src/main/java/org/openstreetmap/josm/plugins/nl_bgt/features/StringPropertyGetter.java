package org.openstreetmap.josm.plugins.nl_bgt.features;

import java.lang.invoke.MethodHandle;

public class StringPropertyGetter {
    private MethodHandle getPropertiesHandle;
    private MethodHandle getPropertyHandle;
    
    public StringPropertyGetter(MethodHandle getPropertiesHandle, MethodHandle getPropertyHandle) {
        super();
        this.getPropertiesHandle = getPropertiesHandle;
        this.getPropertyHandle = getPropertyHandle;
    }
    
    public String getProperty(Object feature) {
        Object properties;
        try {
            properties = getPropertiesHandle.invoke(feature);
            return (String) getPropertyHandle.invoke(properties);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
