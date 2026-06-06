package com.demo.travelcardsystem.service.util;

import com.demo.travelcardsystem.businessrule.Rule;
import com.demo.travelcardsystem.businessrule.TravelStrategy;
import com.demo.travelcardsystem.entity.Journey;
import com.demo.travelcardsystem.exception.NoApplicableRuleException;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.function.Predicate;

@Getter
@Component
@RequiredArgsConstructor
public class FareCalculator {

    @NonNull
    private final TravelStrategy travelStrategy;

    // Compare rules and pick the one with the lower chargeable fare
    private final Comparator<Rule> ruleComparator = Comparator.comparing(Rule::getChargeableFare);

    public Double calculate(Journey journey) {
        Predicate<Rule> rulePredicate = rule -> rule.isRuleSatisfied(journey);

        // Figure out which rule will be applicable out of all provided business rules
        Rule applicableRule = travelStrategy.getRuleCollection().getRules()
                .stream()
                .filter(rulePredicate)
                .min(ruleComparator)
                .orElseThrow(() -> new NoApplicableRuleException("No applicable fare rule could be found for the journey."));

        //finally, return the chargeable fare
        return applicableRule.getChargeableFare();
    }
}
