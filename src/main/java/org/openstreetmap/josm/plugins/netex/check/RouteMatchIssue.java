package org.openstreetmap.josm.plugins.netex.check;

public class RouteMatchIssue implements RouteIssue {
    private String message;
    
    @Override
    public IssueSeverity getSeverity() {
       return IssueSeverity.Major;
    }

    public RouteMatchIssue(String message) {
        super();
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
