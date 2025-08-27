package org.openstreetmap.josm.plugins.nl_bgt.features;

import java.util.Objects;

import org.csv4pojoparser.annotation.FieldType;
import org.csv4pojoparser.annotation.Type;

public class FeatureDto {
    @FieldType(dataType = Type.STRING, csvColumnName = "feature")
    private String feature;
    @FieldType(dataType = Type.STRING, csvColumnName = "type")
    private String type;
    @FieldType(dataType = Type.STRING, csvColumnName = "plusType")
    private String plusType;
    @FieldType(dataType = Type.STRING, csvColumnName = "functie")
    private String functie;
    @FieldType(dataType = Type.STRING, csvColumnName = "fysiekVoorkomen")
    private String fysiekVoorkomen;
    @FieldType(dataType = Type.STRING, csvColumnName = "plusFysiekVoorkomen")
    private String plusFysiekVoorkomen;
    @FieldType(dataType = Type.STRING, csvColumnName = "key")
    private String key;
    @FieldType(dataType = Type.STRING, csvColumnName = "value")
    private String value;

    public FeatureDto() {
        super();
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPlusType() {
        return plusType;
    }

    public void setPlusType(String plusType) {
        this.plusType = plusType;
    }

    public String getFunctie() {
        return functie;
    }

    public void setFunctie(String functie) {
        this.functie = functie;
    }

    public String getFysiekVoorkomen() {
        return fysiekVoorkomen;
    }

    public void setFysiekVoorkomen(String fysiekVoorkomen) {
        this.fysiekVoorkomen = fysiekVoorkomen;
    }

    public String getPlusFysiekVoorkomen() {
        return plusFysiekVoorkomen;
    }

    public void setPlusFysiekVoorkomen(String plusFysiekVoorkomen) {
        this.plusFysiekVoorkomen = plusFysiekVoorkomen;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(feature, functie, fysiekVoorkomen, key, plusFysiekVoorkomen, plusType, type, value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FeatureDto other = (FeatureDto) obj;
        return Objects.equals(feature, other.feature) && Objects.equals(functie, other.functie)
                && Objects.equals(fysiekVoorkomen, other.fysiekVoorkomen) && Objects.equals(key, other.key)
                && Objects.equals(plusFysiekVoorkomen, other.plusFysiekVoorkomen)
                && Objects.equals(plusType, other.plusType) && Objects.equals(type, other.type)
                && Objects.equals(value, other.value);
    }
}
