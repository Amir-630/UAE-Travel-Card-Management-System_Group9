package com.demo.travelcardsystem.repository;

import com.demo.travelcardsystem.entity.TravelCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TravelCardRepository extends JpaRepository<TravelCard, String> {
}
