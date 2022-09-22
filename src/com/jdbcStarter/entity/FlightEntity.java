package com.jdbcStarter.entity;

import java.time.LocalDateTime;

public record FlightEntity(Long id,
                           String flightNo,
                           LocalDateTime departureDate,
                           String departureAirportCode,
                           LocalDateTime arrivalDate,
                           String arrivalAirportCode,
                           Integer aircraftId,
                           String status) {

}
