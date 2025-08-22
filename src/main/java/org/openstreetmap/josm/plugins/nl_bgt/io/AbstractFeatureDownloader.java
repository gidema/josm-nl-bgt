package org.openstreetmap.josm.plugins.nl_bgt.io;

import java.util.concurrent.FutureTask;

import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.plugins.nl_bgt.features.FeatureTagBuilder;
import org.openstreetmap.josm.plugins.nl_bgt.features.FeatureTagBuilderCache;
import org.openstreetmap.josm.plugins.nl_bgt.jts.Boundary;

import nl.pdok.ogc.bgt.model.FeatureGeoJSONWaterdeel;

public abstract class AbstractFeatureDownloader<T> implements FeatureDownloader<T> {

    private DataSet dataSet; 
    private Boundary boundary;
    private final FeatureTagBuilder tagBuilder; 


    public AbstractFeatureDownloader(Class<T> clazz) {
        super();
        tagBuilder = FeatureTagBuilderCache.forClass(clazz); 
    }

    protected static String getFeatureName(Class<?> clazz) {
        var className = clazz.getName();
        return className.substring(0, 14).toLowerCase() + className.substring(15); 
    }

    public FeatureTagBuilder getTagBuilder() {
        return tagBuilder;
    }

    @SuppressWarnings("hiding")
    @Override
    public FutureTask<TaskStatus> getFetchTask(Boundary boundary, DataSet dataSet) {
        this.dataSet = dataSet;
        this.boundary = boundary;
        return new FutureTask<>(this);
    }
    
    public DataSet getDataSet() {
        return dataSet;
    }

    protected Boundary getBoundary() {
        return boundary;
    }
}
