package com.demo.travelcardsystem.businessrule;

import com.demo.travelcardsystem.constant.TransportType;
import com.demo.travelcardsystem.entity.Journey;
import com.demo.travelcardsystem.entity.ZonePair;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a business rule for travel card fare calculation.
 * A rule defines the chargeable fare for a specific transport type and a set of zone pairs.
 */
@Data
public class Rule {
    
    /**
     * The type of transport (e.g., BUS, METRO) this rule applies to.
     */
    private TransportType transportType;
    
    /**
     * The fare amount to be charged if this rule is satisfied.
     */
    private Double chargeableFare;
    
    /**
     * A set of valid zone pairs for which this rule is applicable.
     */
    private final Set<ZonePair> zonePairSet = new HashSet<>();

    /**
     * Adds a new zone pair to the set of valid zone pairs for this rule.
     * This method is synchronized to ensure thread safety when adding zone pairs concurrently.
     *
     * @param zonePair the zone pair to be added
     */
    public synchronized void addZonePair(ZonePair zonePair) {
        zonePairSet.add(zonePair);
    }

    /**
     * Checks if a given journey satisfies the conditions of this rule.
     * A journey satisfies the rule if it matches any of the defined zone pairs,
     * or if the rule's transport type matches the journey's transport type and no specific zone pairs are defined (applicable to any zones).
     *
     * @param journey the journey to be checked against this rule
     * @return true if the journey satisfies the rule, false otherwise
     */
    public boolean isRuleSatisfied(Journey journey) {
        return zonePairSet.stream().anyMatch(zonePair -> zonePair.checkIfJourneyMatchToThisZonePair(journey))
                || (journey.getTransportType().equals(this.getTransportType()) && zonePairSet.isEmpty());
    }
}
