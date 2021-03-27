package com.aaron.AA2_psp.domain;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Country {

    private String name;
    private String capital;
    private String region;
    private String subregion;
    private int population;
    private String flag;
}
