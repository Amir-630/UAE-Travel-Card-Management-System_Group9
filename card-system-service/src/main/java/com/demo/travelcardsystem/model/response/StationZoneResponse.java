package com.demo.travelcardsystem.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StationZoneResponse {
    private String stationName;
    private Set<String> zones;
}
