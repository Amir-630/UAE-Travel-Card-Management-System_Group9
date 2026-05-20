package com.demo.travelcardsystem.entity;

import com.demo.travelcardsystem.constant.Zone;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents a pair of zones, typically denoting the start and end zones of a journey leg.
 * Used primarily for checking if a specific journey falls within this zone configuration.
 */
@Data
@AllArgsConstructor
public class ZonePair {
    
    /**
     * The starting zone of the pair.
     */
    private Zone startZone;
    
    /**
     * The ending zone of the pair.
     */
    private Zone endZone;

    /**
     * Checks if the given journey matches this specific zone pair.
     * A journey matches if its starting station belongs to the start zone of this pair
     * and its ending station belongs to the end zone of this pair.
     *
     * @param journey the journey to be checked
     * @return true if the journey's start and end stations fall within this zone pair, false otherwise
     */
    public boolean checkIfJourneyMatchToThisZonePair(Journey journey) {
        return journey.getStartStation().getZones().contains(startZone) && journey.getEndStation().getZones().contains(endZone);
    }
}
