package org.openstreetmap.josm.plugins.nl_bgt.io;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.openstreetmap.josm.data.osm.DataSet;
import org.openstreetmap.josm.plugins.nl_bgt.jts.Boundary;

public interface FeatureDownloader<T> extends Callable<TaskStatus> {
    public FutureTask<TaskStatus> getFetchTask(Boundary boundary, DataSet dataSet);
    public void addToOsm(T feature);
}
