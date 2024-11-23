package com.unir.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class OracleDep {
    private int depId;

    @Setter
    private String depName;

    @Setter
    private int managerId;

    @Setter
    private int locationId;
}
