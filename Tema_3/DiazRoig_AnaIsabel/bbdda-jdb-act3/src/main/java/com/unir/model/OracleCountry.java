package com.unir.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class OracleCountry {

    private String countryId;

    @Setter
    private int regionId;

    @Setter
    private String countryName;
}
