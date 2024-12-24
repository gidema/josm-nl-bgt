package org.openstreetmap.josm.plugins.netex.check;

import org.openstreetmap.josm.tools.I18n;

public class QuayMismatchIssue implements RouteIssue {
    private String message;
    
    public QuayMismatchIssue(QuayMatch mismatch, int osmPosition) {
        var osmQuay = mismatch.getOsmQuay();
        var netexQuay = mismatch.getNetexQuay();
        this.message = I18n.tr("Deviating quays at position {0}. Expected {1}, but found {2}.",
            osmPosition, netexQuay, osmQuay);
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
