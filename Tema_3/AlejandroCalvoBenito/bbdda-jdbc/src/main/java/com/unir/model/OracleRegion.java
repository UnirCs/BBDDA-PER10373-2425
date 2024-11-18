package com.unir.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class OracleRegion {

    private int regionId;

    @Setter
    private String regionName;
}
