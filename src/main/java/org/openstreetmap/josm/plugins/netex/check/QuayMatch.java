package org.openstreetmap.josm.plugins.netex.check;

import java.util.Objects;

import org.openstreetmap.josm.plugins.netex.ChbQuayService;
import org.openstreetmap.josm.plugins.netex.model.NetexRouteQuay;
import org.openstreetmap.josm.plugins.netex.model.OsmPtQuay;

public class QuayMatch {
    final static ChbQuayService chbQuayService = ChbQuayService.getInstance();

    private final OsmPtQuay osmQuay;
    private final NetexRouteQuay netexQuay;
    private boolean quayCodeMatch = false;
    private boolean areaCodeMatch = false;
    private boolean nameMatch = false;

    public QuayMatch(OsmPtQuay osmQuay, NetexRouteQuay netexQuay) {
        super();
        this.osmQuay = osmQuay;
        this.netexQuay = netexQuay;
        quayCodeMatch = Objects.equals(osmQuay.getQuayCode(), netexQuay.getQuayCode());
        if (isQuayCodeMatch()) {
            areaCodeMatch = true;
//                nameMatch = Objects.equals(osmQuay.getName(), netexQuay.getName());
        } else {
            checkAreaMatch();
        }
    }

    private void checkAreaMatch() {
        if (osmQuay.getQuayCode() != null) {
            var chbQuay = chbQuayService.getQuay(osmQuay.getQuayCode());
            if (chbQuay != null) {
                var areaCode = chbQuay.getAreaCode();
                if (areaCode != null && areaCode.equals(netexQuay.getStopplaceCode())) {
                    areaCodeMatch = true;
                }
            }
        }
    }

    public OsmPtQuay getOsmQuay() {
        return osmQuay;
    }

    public NetexRouteQuay getNetexQuay() {
        return netexQuay;
    }

    public boolean isQuayCodeMatch() {
        return quayCodeMatch;
    }

    public void setQuayCodeMatch(boolean quayCodeMatch) {
        this.quayCodeMatch = quayCodeMatch;
    }

    public boolean isAreaCodeMatch() {
        return areaCodeMatch;
    }

    public void setAreaCodeMatch(boolean areaCodeMatch) {
        this.areaCodeMatch = areaCodeMatch;
    }

    public boolean isNameMatch() {
        return nameMatch;
    }

    public void setNameMatch(boolean nameMatch) {
        this.nameMatch = nameMatch;
    }

    public boolean isMatch() {
        return isQuayCodeMatch() || isAreaCodeMatch() || isNameMatch();
    }
}