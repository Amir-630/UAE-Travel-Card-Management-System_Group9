package com.demo.travelcardsystem.entity;

import com.demo.travelcardsystem.constant.Zone;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Station {

    @Id
    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "station_zones", joinColumns = @JoinColumn(name = "station_name"))
    @Column(name = "zone")
    private Set<Zone> zones;
}
