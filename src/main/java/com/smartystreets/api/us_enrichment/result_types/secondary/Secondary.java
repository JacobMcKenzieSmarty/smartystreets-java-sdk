package com.smartystreets.api.us_enrichment.result_types.secondary;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Secondary {

    private String smartyKey;

    private int smartyKeyExt;

    private String secondaryDesignator;

    private String secondaryNumber;

    private String plus4Code;

    @JsonProperty("smarty_key")
    public String getSmartyKey() {
        return smartyKey;
    }

    @JsonProperty("smarty_key_ext")
    public int getSmartyKeyExt() {
        return smartyKeyExt;
    }

    @JsonProperty("secondary_designator")
    public String getSecondaryDesignator() {
        return secondaryDesignator;
    }

    @JsonProperty("secondary_number")
    public String getSecondaryNumber() {
        return secondaryNumber;
    }

    @JsonProperty("plus4_code")
    public String getPlus4Code() {
        return plus4Code;
    }

    @Override
    public String toString() {
        return "Secondary{" +
                "smarty_key='" + getSmartyKey() + '\'' +
                ", smarty_key_ext=" + getSmartyKeyExt() +
                ", secondary_designator='" + getSecondaryDesignator() + '\'' +
                ", secondary_number='" + getSecondaryNumber() + '\'' +
                ", plus4_code='" + getPlus4Code() + '\'' +
                '}';
    }
}
