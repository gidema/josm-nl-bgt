package org.openstreetmap.josm.plugins.nl_bgt.features;

import java.util.Objects;

public class BgtFeatureTags {
    private final String type;
    private final String plusType;
    private final String functie;
    private final String functioneelVoorkomen;
    private final String plusFunctioneelVoorkomen;

    public BgtFeatureTags(String type, String plusType, String functie, String functioneelVoorkomen,
            String plusFunctioneelVoorkomen) {
        super();
        this.type = type;
        this.plusType = plusType;
        this.functie = functie;
        this.functioneelVoorkomen = functioneelVoorkomen;
        this.plusFunctioneelVoorkomen = plusFunctioneelVoorkomen;
    }

    @Override
    public int hashCode() {
        return Objects.hash(functie, functioneelVoorkomen, plusFunctioneelVoorkomen, plusType, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BgtFeatureTags other = (BgtFeatureTags) obj;
        return Objects.equals(functie, other.functie)
                && Objects.equals(functioneelVoorkomen, other.functioneelVoorkomen)
                && Objects.equals(plusFunctioneelVoorkomen, other.plusFunctioneelVoorkomen)
                && Objects.equals(plusType, other.plusType) && Objects.equals(type, other.type);
    }
}
