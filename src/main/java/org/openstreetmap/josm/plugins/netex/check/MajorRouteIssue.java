package org.openstreetmap.josm.plugins.netex.check;

public class MajorRouteIssue implements RouteIssue {

    private final String message;

    public MajorRouteIssue(String message) {
        this.message = message;
    }

    @Override
    public IssueSeverity getSeverity() {
        return IssueSeverity.Major;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
