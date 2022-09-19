package com.jdbcStarter;

import com.jdbcStarter.dao.TicketDao;
import com.jdbcStarter.entity.TicketEntity;

import java.math.BigDecimal;

public class DaoRunner {

    public static void main(String[] args) {
        TicketDao ticketDao = TicketDao.getInstance();
        TicketEntity ticketEntity = new TicketEntity();
        ticketEntity.setPassengerNo("1234567");
        ticketEntity.setPassengerName("test");
        ticketEntity.setFlightId(3L);
        ticketEntity.setSeatNo("B3");
        ticketEntity.setCost(BigDecimal.TEN);

//        TicketEntity savedTicketEntity = ticketDao.save(ticketEntity);
//        System.out.println(savedTicketEntity);
        boolean isDelete = ticketDao.delete(276L);
        System.out.println(isDelete);

    }
}
