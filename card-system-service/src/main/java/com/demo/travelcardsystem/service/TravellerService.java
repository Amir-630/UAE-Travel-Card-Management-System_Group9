package com.demo.travelcardsystem.service;

import com.demo.travelcardsystem.entity.Journey;
import com.demo.travelcardsystem.entity.Station;
import com.demo.travelcardsystem.entity.TravelCard;
import com.demo.travelcardsystem.exception.InvalidCardException;
import com.demo.travelcardsystem.exception.InvalidDataProvidedException;
import com.demo.travelcardsystem.exception.InvalidRechargeAmount;
import com.demo.travelcardsystem.model.request.CardRegistrationRequest;
import com.demo.travelcardsystem.model.request.SwipeRequest;
import com.demo.travelcardsystem.model.response.StationZoneResponse;
import com.demo.travelcardsystem.model.response.TravelCardResponse;
import com.demo.travelcardsystem.repository.StationRepository;
import com.demo.travelcardsystem.repository.TravelCardRepository;
import com.demo.travelcardsystem.service.util.FareCalculator;
import com.demo.travelcardsystem.service.util.TravelCardConverter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TravellerService {

    private static final String INVALID_CARD_EXC_MESSAGE = "This card is Invalid. Please use a valid card";

    private final TravelCardRepository travelCardRepository;
    private final StationRepository stationRepository;
    private final FareCalculator fareCalculator;

    @Transactional
    public TravelCardResponse registerNewCard(CardRegistrationRequest cardRegistrationRequest) {
        if (cardRegistrationRequest == null || cardRegistrationRequest.getCardNumber() == null || cardRegistrationRequest.getCardNumber().isEmpty()) {
            throw new InvalidCardException(INVALID_CARD_EXC_MESSAGE);
        }
        if (cardRegistrationRequest.getBalance() < 0) {
            throw new InvalidRechargeAmount("Recharge amount must not be negative");
        }
        if (travelCardRepository.existsById(cardRegistrationRequest.getCardNumber())) {
            throw new InvalidCardException("This card is already registered.");
        }

        TravelCard travelCard = new TravelCard();
        travelCard.setCardNumber(cardRegistrationRequest.getCardNumber());
        travelCard.setBalance(cardRegistrationRequest.getBalance());

        TravelCard savedCard = travelCardRepository.save(travelCard);
        return  TravelCardConverter.TRAVEL_CARD_TRAVEL_CARD_RESPONSE_FUNCTION.apply(savedCard);
    }

    @Transactional
    public List<TravelCardResponse> registerNewCards(List<CardRegistrationRequest> cardRegistrationRequests) {
        List<TravelCard> travelCards = cardRegistrationRequests.stream()
                .map(req -> {
                    if (req == null || req.getCardNumber() == null || req.getCardNumber().isEmpty()) {
                        throw new InvalidCardException("One of the cards is Invalid. Please use a valid card");
                    }
                    if (req.getBalance() < 0) {
                        throw new InvalidRechargeAmount("Recharge amount for one of the cards must not be negative");
                    }
                    if (travelCardRepository.existsById(req.getCardNumber())) {
                        throw new InvalidCardException("One of the cards is already registered: " + req.getCardNumber());
                    }
                    TravelCard travelCard = new TravelCard();
                    travelCard.setCardNumber(req.getCardNumber());
                    travelCard.setBalance(req.getBalance());
                    return travelCard;
                }).collect(Collectors.toList());

        List<TravelCard> savedCards = travelCardRepository.saveAll(travelCards);
        return savedCards.stream()
                .map( TravelCardConverter.TRAVEL_CARD_TRAVEL_CARD_RESPONSE_FUNCTION)
                .collect(Collectors.toList());
    }

    @Transactional
    public TravelCardResponse rechargeTheCard(String cardNumber, double rechargeAmount) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            throw new InvalidCardException(INVALID_CARD_EXC_MESSAGE);
        }
        if (rechargeAmount < 0) {
            throw new InvalidRechargeAmount("Recharge amount must not be negative");
        }

        TravelCard travelCard = travelCardRepository.findById(cardNumber)
                .orElseThrow(() -> new InvalidCardException(INVALID_CARD_EXC_MESSAGE));
        travelCard.addCredit(rechargeAmount);
        TravelCard savedCard = travelCardRepository.save(travelCard);
        return  TravelCardConverter.TRAVEL_CARD_TRAVEL_CARD_RESPONSE_FUNCTION.apply(savedCard);
    }

    @Transactional
    public TravelCardResponse swipeCard(SwipeRequest swipeRequest) {
        if (null == swipeRequest.getTransportType()) {
            throw new InvalidDataProvidedException();
        }

        TravelCard travelCard = travelCardRepository.findById(swipeRequest.getCardNumber())
                .orElseThrow(() -> new InvalidCardException(INVALID_CARD_EXC_MESSAGE));
        Station station = stationRepository.findById(swipeRequest.getStationName())
                .orElseThrow(InvalidDataProvidedException::new);

        if (travelCard.isInTransit()) { // Check-out
            Journey journey = Journey.builder()
                    .startStation(travelCard.getStartStation())
                    .endStation(station)
                    .transportType(travelCard.getTransportType())
                    .journeyCompleted(true)
                    .build();

            double fare = fareCalculator.calculate(journey);
            
            // Refund the max fare and deduct the actual fare
            double maxFare = fareCalculator.getTravelStrategy().getRuleCollection().getMaxFare();
            travelCard.addCredit(maxFare);
            travelCard.debitAmount(fare);

            travelCard.setInTransit(false);
            travelCard.setStartStation(null);
            travelCard.setTransportType(null);
        } else { // Check-in
            double maxFare = fareCalculator.getTravelStrategy().getRuleCollection().getMaxFare();
            if (travelCard.getBalance() < maxFare) {
                throw new InvalidCardException("Insufficient balance to start journey. Please recharge.");
            }
            travelCard.debitAmount(maxFare); // Deduct max fare at check-in

            travelCard.setInTransit(true);
            travelCard.setStartStation(station);
            travelCard.setTransportType(swipeRequest.getTransportType());
        }

        TravelCard savedCard = travelCardRepository.save(travelCard);
        return  TravelCardConverter.TRAVEL_CARD_TRAVEL_CARD_RESPONSE_FUNCTION.apply(savedCard);
    }



    public TravelCardResponse checkCardDetail(String cardNumber) {
        TravelCard travelCard = travelCardRepository.findById(cardNumber)
                .orElseThrow(() -> new InvalidCardException(INVALID_CARD_EXC_MESSAGE));
        return  TravelCardConverter.TRAVEL_CARD_TRAVEL_CARD_RESPONSE_FUNCTION.apply(travelCard);
    }

    public List<String> fetchAllCard() {
        return travelCardRepository.findAll().stream()
                .map(TravelCard::getCardNumber)
                .collect(Collectors.toList());
    }

    public Set<StationZoneResponse> fetchAllStationsAndZones() {
        return stationRepository.findAll().stream()
                .map(station -> new StationZoneResponse(station.getName(), station.getZones().stream().map(Enum::name).collect(Collectors.toSet())))
                .collect(Collectors.toSet());
    }
}
