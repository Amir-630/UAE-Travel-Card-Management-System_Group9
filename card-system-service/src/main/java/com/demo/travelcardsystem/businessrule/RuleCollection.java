package com.demo.travelcardsystem.businessrule;

import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * A singleton component that holds the collection of business rules for the travel card system.
 * It also maintains the maximum possible fare that can be charged across all rules.
 */
@Data
@Component
@Scope("singleton")
public class RuleCollection {
    
    /**
     * The maximum fare that can be charged for a journey, typically used as an upfront charge.
     */
    private Double maxFare;
    
    /**
     * The set of all defined business rules for fare calculation.
     */
    private Set<Rule> rules = new HashSet<>();

    /**
     * Adds a new rule to the collection of business rules.
     *
     * @param rule the rule to be added
     */
    public void addRules(Rule rule) {
        rules.add(rule);
    }
}
