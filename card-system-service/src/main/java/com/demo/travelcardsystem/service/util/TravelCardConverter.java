package com.demo.travelcardsystem.service.util;

import com.demo.travelcardsystem.entity.TravelCard;
import com.demo.travelcardsystem.model.response.TravelCardResponse;
import org.springframework.stereotype.Component;

import java.util.function.Function;


@Component
public class TravelCardConverter {
    private TravelCardConverter() {
        /* This utility class should not be instantiated */
        //travelCard2TravelCardResponseConverter
    }


    public static final  Function<TravelCard, TravelCardResponse> TRAVEL_CARD_TRAVEL_CARD_RESPONSE_FUNCTION= travelCard -> {
        TravelCardResponse travelCardResponse = new TravelCardResponse();
        travelCardResponse.setCardNumber(travelCard.getCardNumber());
        travelCardResponse.setBalance(travelCard.getBalance());
        travelCardResponse.setInTransit(travelCard.isInTransit());
        travelCardResponse.setTransportType(travelCard.getTransportType());
        return travelCardResponse;
    };
}
