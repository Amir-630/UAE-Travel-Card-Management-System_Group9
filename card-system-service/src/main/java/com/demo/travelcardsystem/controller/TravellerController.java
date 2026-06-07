package com.demo.travelcardsystem.controller;

import com.demo.travelcardsystem.model.request.CardRegistrationRequest;
import com.demo.travelcardsystem.model.request.SwipeRequest;
import com.demo.travelcardsystem.model.response.StationZoneResponse;
import com.demo.travelcardsystem.model.response.TravelCardResponse;
import com.demo.travelcardsystem.service.TravellerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(value = "/api")
@AllArgsConstructor
@Tag(name = "Travel Card System", description = "APIs for managing travel cards, journeys, and system data")
public class TravellerController {

    private final TravellerService travellerService;

    @Operation(summary = "Health Check", description = "Verifies that the service is up and running.")
    @GetMapping(value = "/card/ping")
    public String pingMe() {
        return "Service is UP and Running";
    }

    @Operation(summary = "Register a New Card", description = "Registers a single new travel card in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card registered successfully",
                    content = @Content(schema = @Schema(implementation = TravelCardResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid card data provided")
    })
    @PostMapping(value = "/card/register")
    public TravelCardResponse registerNewUser(@RequestBody CardRegistrationRequest cardRegistrationRequest) {
        return travellerService.registerNewCard(cardRegistrationRequest);
    }

    @Operation(summary = "Register Multiple Cards (Batch)", description = "Registers a list of new travel cards in a single batch operation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cards registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data in one or more card requests")
    })
    @PostMapping(value = "/card/register/batch")
    public List<TravelCardResponse> registerNewUsersBatch(@RequestBody List<CardRegistrationRequest> cardRegistrationRequests) {
        return travellerService.registerNewCards(cardRegistrationRequests);
    }

    @Operation(summary = "Recharge a Card", description = "Adds funds to an existing travel card. The card number must be sent as a raw string in the request body.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card recharged successfully",
                    content = @Content(schema = @Schema(implementation = TravelCardResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid card number or recharge amount")
    })
    @PostMapping(value = "/card/recharge/{rechargeAmount}", consumes = "text/plain")
    public TravelCardResponse rechargeTheCard(@RequestBody String cardNumber, @PathVariable double rechargeAmount) {
        return travellerService.rechargeTheCard(cardNumber, rechargeAmount);
    }

    @Operation(summary = "Swipe Card (Check-In / Check-Out)", description = "Handles both check-in and check-out events. Deducts max fare on check-in; calculates and settles the final fare on check-out.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Swipe processed successfully",
                    content = @Content(schema = @Schema(implementation = TravelCardResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid swipe data or insufficient balance for check-in")
    })
    @PostMapping(value = "/card/swipe")
    public TravelCardResponse swipeCard(@RequestBody SwipeRequest swipeRequest) {
        return travellerService.swipeCard(swipeRequest);
    }

    @Operation(summary = "Get Card Details", description = "Retrieves the current balance and transit status for a specific card.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card details retrieved successfully",
                    content = @Content(schema = @Schema(implementation = TravelCardResponse.class))),
            @ApiResponse(responseCode = "404", description = "Card not found")
    })
    @GetMapping(value = "/card/{cardNumber}")
    public TravelCardResponse checkCardDetail(@PathVariable String cardNumber) {
        return travellerService.checkCardDetail(cardNumber);
    }

    @Operation(summary = "Get All Registered Cards", description = "Retrieves a list of all registered card numbers.")
    @GetMapping(value = "/card")
    public List<String> fetchAllCard() {
        return travellerService.fetchAllCard();
    }

    @Operation(summary = "Get All Stations and Zones", description = "Retrieves a list of all available stations and their associated fare zones.")
    @GetMapping(value = "/stations-zones")
    public Set<StationZoneResponse> fetchAllStationsAndZones() {
        return travellerService.fetchAllStationsAndZones();
    }
}
