package com.demo.travelcardsystem.controller;

import com.demo.travelcardsystem.model.request.CardRegistrationRequest;
import com.demo.travelcardsystem.model.request.SwipeRequest;
import com.demo.travelcardsystem.model.response.StationZoneResponse;
import com.demo.travelcardsystem.model.response.TravelCardResponse;
import com.demo.travelcardsystem.service.TravellerService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = "/api")
@AllArgsConstructor
public class TravellerController {

    private TravellerService travellerService;

    @GetMapping(value = "/card/ping")
    public String pingMe() {
        return "Service is UP and Running";
    }

    @PostMapping(value = "/card/register")
    public TravelCardResponse registerNewUser(@RequestBody CardRegistrationRequest cardRegistrationRequest) {
        return travellerService.registerNewCard(cardRegistrationRequest);
    }

    @PostMapping(value = "/card/register/batch")
    public List<TravelCardResponse> registerNewUsersBatch(@RequestBody List<CardRegistrationRequest> cardRegistrationRequests) {
        return travellerService.registerNewCards(cardRegistrationRequests);
    }

    @PostMapping(value = "/card/recharge/{rechargeAmount}", consumes = "text/plain")
    public TravelCardResponse rechargeTheCard(@RequestBody String cardNumber, @PathVariable double rechargeAmount) {
        return travellerService.rechargeTheCard(cardNumber, rechargeAmount);
    }

    @PostMapping(value = "/card/swipe")
    public TravelCardResponse swipeCard(@RequestBody SwipeRequest swipeRequest) {
        return travellerService.swipeCard(swipeRequest);
    }

    @GetMapping(value = "/card/{cardNumber}")
    public TravelCardResponse checkCardDetail(@PathVariable String cardNumber) {
        return travellerService.checkCardDetail(cardNumber);
    }

    @GetMapping(value = "/card")
    public List<String> fetchAllCard() {
        return travellerService.fetchAllCard();
    }

    @GetMapping(value = "/stations-zones")
    public Set<StationZoneResponse> fetchAllStationsAndZones() {
        return travellerService.fetchAllStationsAndZones();
    }
}
