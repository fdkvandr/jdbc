package com.jdbcStarter.entity;

import java.math.BigDecimal;

public class TicketEntity {

    private Long id;
    private String passengerNo;
    private String passengerName;
    private FlightEntity flightEntity; //Вместо flight_id мы ссылаемся на объект типа flight
    private String seatNo;
    private BigDecimal cost;

    public TicketEntity(Long id, String passengerNo, String passengerName, FlightEntity flightEntity, String seatNo, BigDecimal cost) {
        this.id = id;
        this.passengerNo = passengerNo;
        this.passengerName = passengerName;
        this.flightEntity = flightEntity;
        this.seatNo = seatNo;
        this.cost = cost;
    }

    public TicketEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassengerNo() {
        return passengerNo;
    }

    public void setPassengerNo(String passengerNo) {
        this.passengerNo = passengerNo;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public FlightEntity getFlightEntity() {
        return flightEntity;
    }

    public void setFlightEntity(FlightEntity flightEntity) {
        this.flightEntity = flightEntity;
    }

    public String getSeatNo() {
        return seatNo;
    }

    public void setSeatNo(String seatNo) {
        this.seatNo = seatNo;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "TicketEntity{" +
               "id=" + id +
               ", passengerNo='" + passengerNo + '\'' +
               ", passengerName='" + passengerName + '\'' +
               ", flightEntity=" + flightEntity +
               ", seatNo='" + seatNo + '\'' +
               ", cost=" + cost +
               '}';
    }
}
