package com.example.demo_2.jsonEntity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class Shp {

    @JsonProperty("type")
    private String type;
    @JsonProperty("geometry")
    private GeometryDTO geometry;
    @JsonProperty("properties")
    private PropertiesDTO properties;
    @JsonProperty("id")
    private String id;

    @NoArgsConstructor
    @Data
    public static class GeometryDTO {
        @JsonProperty("type")
        private String type;
        @JsonProperty("coordinates")
        private List<List<List<List<Double>>>> coordinates;
    }

    @NoArgsConstructor
    @Data
    public static class PropertiesDTO {
        @JsonProperty("ID")
        private Integer id;
    }
}
