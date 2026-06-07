package com.demo.travelcardsystem.businessrule;

import com.demo.travelcardsystem.constant.TransportType;
import com.demo.travelcardsystem.constant.Zone;
import com.demo.travelcardsystem.entity.ZonePair;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;
import java.util.function.DoubleConsumer;

/**
 * Defines and loads all business rules for travel fare calculation into a RuleCollection.
 * This class encapsulates various strategies for creating rules based on zones and transport types.
 */
@Data
@Component
@RequiredArgsConstructor
public class TravelStrategy {

    /**
     * The collection where all created business rules are stored.
     */
    @NonNull
    private RuleCollection ruleCollection;

    /**
     * Strategy for creating a rule for travel anywhere within Zone One.
     */
    private final DoubleConsumer anyWhereInZoneOneStrategy = chargeableAmount -> {
        Rule rule = new Rule();
        rule.setChargeableFare(chargeableAmount);

        //Create all possible ZonePair for Zone 1
        ZonePair zonePair = new ZonePair(Zone.ONE, Zone.ONE);
        rule.addZonePair(zonePair);

        ruleCollection.addRules(rule);

    };

    /**
     * Strategy for creating a rule for travel within any single zone outside of Zone One.
     */
    private final DoubleConsumer anyOneZoneOutsideZoneOneStrategy = chargeableAmount -> {
        Rule rule = new Rule();
        rule.setChargeableFare(chargeableAmount);

        //create all possible pair of any zone outside zone one.
        rule.addZonePair(new ZonePair(Zone.TWO, Zone.TWO));
        rule.addZonePair(new ZonePair(Zone.THREE, Zone.THREE));

        ruleCollection.addRules(rule);
    };

    /**
     * Strategy for creating a rule for travel across any two zones, including Zone One.
     */
    private final DoubleConsumer anyTwoZoneIncludingZoneOneStrategy = chargeableAmount -> {
        Rule rule = new Rule();
        rule.setChargeableFare(chargeableAmount);

        //create all possible pair of any zone outside zone one.
        rule.addZonePair(new ZonePair(Zone.ONE, Zone.TWO));
        rule.addZonePair(new ZonePair(Zone.TWO, Zone.ONE));
        rule.addZonePair(new ZonePair(Zone.ONE, Zone.THREE));
        rule.addZonePair(new ZonePair(Zone.THREE, Zone.ONE));

        ruleCollection.addRules(rule);
    };

    /**
     * Strategy for creating a rule for travel across any two zones, excluding Zone One.
     */
    private final DoubleConsumer anyTwoZoneExcludingZoneOneStrategy = chargeableAmount -> {
        Rule rule = new Rule();
        rule.setChargeableFare(chargeableAmount);

        //create all possible pair of any two zone excluding zone one.
        rule.addZonePair(new ZonePair(Zone.TWO, Zone.THREE));
        rule.addZonePair(new ZonePair(Zone.THREE, Zone.TWO));

        ruleCollection.addRules(rule);
    };

    /**
     * Strategy for creating a rule for travel across any three zones.
     */
    private final DoubleConsumer anyThreeZoneStrategy = chargeableAmount -> {
        Rule rule = new Rule();
        rule.setChargeableFare(chargeableAmount);



        ruleCollection.addRules(rule);
    };

    /**
     * Strategy for creating a rule for any journey by a specific transport type (e.g., Bus).
     */
    private final BiConsumer<Double, TransportType> anyJourneyByBus = (chargeableAmount, transType) -> {
        Rule rule = new Rule();
        rule.setChargeableFare(chargeableAmount);
        rule.setTransportType(transType);

        ruleCollection.addRules(rule);


    };

    /**
     * Loads all predefined business rules with their respective fares into the RuleCollection.
     * This method orchestrates the creation of all rules by invoking the defined strategies.
     *
     * @return The populated RuleCollection.
     */
    public RuleCollection loadAllBusinessRules() {
        anyWhereInZoneOneStrategy.accept(2.50);
        anyOneZoneOutsideZoneOneStrategy.accept(2.00);
        anyTwoZoneIncludingZoneOneStrategy.accept(3.00);
        anyTwoZoneExcludingZoneOneStrategy.accept(2.25);
        anyThreeZoneStrategy.accept(3.20);
        anyJourneyByBus.accept(1.80, TransportType.BUS);

        this.ruleCollection.setMaxFare(3.20);

        return this.ruleCollection;
    }

}
