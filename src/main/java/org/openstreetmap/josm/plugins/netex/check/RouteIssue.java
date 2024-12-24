package org.openstreetmap.josm.plugins.netex.check;

public interface RouteIssue {
    public IssueSeverity getSeverity();

    public String getMessage();
}
