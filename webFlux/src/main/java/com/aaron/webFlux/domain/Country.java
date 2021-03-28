package com.aaron.webFlux.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@Document(value = "countries")
public class Country {

    @Id
    private String id;
    @Field
    private String name;
    @Field
    private String region;
    @Field
    private String subregion;
    @Field
    private int population;
    @Field
    private String flag;
}
