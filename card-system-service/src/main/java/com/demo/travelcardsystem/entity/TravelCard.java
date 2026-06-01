package com.demo.travelcardsystem.entity;

import com.demo.travelcardsystem.constant.TransportType;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class TravelCard {

    @Id
    private String cardNumber;
    private double balance;
    private boolean inTransit;

    @ManyToOne
    @JoinColumn(name = "start_station_id")
    private Station startStation;

    @Enumerated(EnumType.STRING)
    private TransportType transportType;

    public synchronized void addCredit(double amount) {
        this.balance += amount;
    }

    public synchronized void debitAmount(double amount) {
        this.balance -= amount;
    }
}
