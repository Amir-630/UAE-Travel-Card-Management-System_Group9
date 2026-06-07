package com.demo.travelcardsystem.config;

import com.demo.travelcardsystem.businessrule.RuleCollection;
import com.demo.travelcardsystem.businessrule.TravelStrategy;
import com.demo.travelcardsystem.constant.Zone;
import com.demo.travelcardsystem.entity.Station;
import com.demo.travelcardsystem.entity.TravelCard;
import com.demo.travelcardsystem.repository.StationRepository;
import com.demo.travelcardsystem.repository.TravelCardRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@SpringBootApplication(scanBasePackages = {"com.demo.travelcardsystem"})
@EnableJpaRepositories(basePackages = "com.demo.travelcardsystem.repository")
@EntityScan(basePackages = "com.demo.travelcardsystem.entity")
public class TravelcardsystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(TravelcardsystemApplication.class, args);
    }

    @Bean
    public RuleCollection loadAllTravelStrategy(TravelStrategy travelStrategy) {
        return travelStrategy.loadAllBusinessRules();
    }

    @Bean
    public CommandLineRunner loadInitialData(StationRepository stationRepository, TravelCardRepository travelCardRepository) {
        return args -> {
            Set<Station> stations = new HashSet<>();
            stations.add(new Station("Algubaiba", new HashSet<>(Arrays.asList(Zone.ONE))));
            stations.add(new Station("Jumeirah", new HashSet<>(Arrays.asList(Zone.ONE, Zone.TWO))));
            stations.add(new Station("Bur Dubai", new HashSet<>(Arrays.asList(Zone.THREE))));
            stations.add(new Station("Deirah", new HashSet<>(Arrays.asList(Zone.TWO))));
            stationRepository.saveAll(stations);

            TravelCard firstTravelCard = new TravelCard();
            firstTravelCard.setCardNumber("A101");
            firstTravelCard.setBalance(30);

            TravelCard secondTravelCard = new TravelCard();
            secondTravelCard.setCardNumber("B201");
            secondTravelCard.setBalance(50);

            travelCardRepository.save(firstTravelCard);
            travelCardRepository.save(secondTravelCard);
        };
    }
}
